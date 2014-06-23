package com.puc.parte_electronico.uploader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.globals.Settings;
import com.puc.parte_electronico.model.Database;
import com.puc.parte_electronico.model.TrafficTicket;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.Random;

/**
 * Ever running service that manages file fragmentation and upload towards a
 * middleware. This service is designed not to hog all network resources and
 * battery. It sleeps whenever there is no file to be uploaded.
 * 
 * @author Jose Ignacio Benedetto (jibenedettoc@gmail.com)
 * 
 */
public class Uploader extends IntentService {
    public enum UploadState {
        PENDING, UPLOADING, DONE, ERROR;

        public String getLabel(Context context) {
            if (this == UploadState.PENDING) {
                return context.getString(R.string.upload_state_pending);
            } else if (this == UploadState.DONE) {
                return context.getString(R.string.upload_state_done);
            } else if (this == UploadState.UPLOADING) {
                return context.getString(R.string.upload_state_uploading);
            } else {
                return context.getString(R.string.upload_state_error);
            }
        }
    }

    // Defaults
    // TODO: move to resource file

    public static final int DEFAULT_TIMEOUT_MS = 60 * 1000;
    public static final int MINIMUM_BACKOFF_MS = 5 * 1000;
    public static int MAXIMUM_BACKOFF_MS = 1000 * 60 * 60;

    private final static String DEFAULT_HOST = "http://middleware.frogmi.com";

    // Class attributes

    private int mConnectionFailures;
    private TrafficTicket mCurrentTicket;
    private Object mBackoffLock = new Object();
    private Random mRandom = new Random();

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, Uploader.class);
        return intent;
    }


    public Uploader() {
        super("UploaderService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Database db = Settings.getSettings().getDatabase();
        TrafficTicket ticket;
        while((ticket = TrafficTicket.getPendingTicket(db)) != null) {
            mCurrentTicket = ticket;
            File file = new File(ticket.getZipPath());
            if (file.exists()) {
                loadFile(file);
            } else {
                mCurrentTicket.setState(UploadState.ERROR);
                mCurrentTicket.update();
            }

        }
    }

    /**
     * Configures the http url request to perform.
     *
     * @param connection The http url connection.
     * @param length The length of the whole file.
     */
    private void configureConnection(HttpURLConnection connection, long length) {
        connection.setConnectTimeout(DEFAULT_TIMEOUT_MS);
        connection.setReadTimeout(DEFAULT_TIMEOUT_MS);
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
        }
        connection.setDoOutput(true);
        connection.setFixedLengthStreamingMode(length);
        connection.addRequestProperty("Content-Type", "application/zip");
    }

    /**
     * Attempts to load one package to the server.
     *
     * @param file The zipped file to upload.
     */
    private void loadFile(File file) {
        Log.i("Uploader", "Beginning: " + new Date().toString());
        try {
            long startTime = System.currentTimeMillis();

            URL url = new URL(DEFAULT_HOST);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            configureConnection(connection, file.length());

            FileInputStream fis = new FileInputStream(file);
            Log.i("Uploader", "Write started");

            OutputStream os = connection.getOutputStream();
            int bytesSent = writeStream(fis, os);
            os.close();

            int responseCode = connection.getResponseCode();
            Log.i("Uploader", "Response code: " + responseCode);

            InputStream is;
            if (responseCode / 100 == 4 || responseCode / 100 == 5) {
                is = connection.getErrorStream();
            } else {
                is = connection.getInputStream();
            }
            byte[] response = readStream(is);
            is.close();

            fis.close();
            connection.disconnect();

            finishUpload(file);
        } catch (Exception e) {
            Log.i("Uploader", "Connection failed");
            e.printStackTrace();
            backoff();
        }
    }

    /**
     * Broadcasts an intent indicating the file has been completely uploaded
     * and deletes the file from the database and filesystem.
     */
    private void finishUpload(File file) {
        file.delete();
        mCurrentTicket.setState(UploadState.DONE);
        mCurrentTicket.update();

    }


    /**
     * Pauses the thread execution for an exponential amount of time
     * relative to the the amount of consecutive connection failures.
     */
    private void backoff() {
        mConnectionFailures++;

        synchronized (mBackoffLock) {

            double waitTime = Math.min(MAXIMUM_BACKOFF_MS,
                    MINIMUM_BACKOFF_MS * Math.pow(2, mConnectionFailures));
            // Wait time is multiplied by a random number between 1 and
            // 1.25 to add entropy.
            waitTime *= 1.0 + (25.0 * mRandom.nextDouble() / 100.0);

            Log.i("Uploader", String.format("Backoff for %d milliseconds",
                    (long) waitTime));

            try {
                mBackoffLock.wait((long) waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private int writeStream(FileInputStream fis, OutputStream output)
            throws IOException {
        OutputStream os = new BufferedOutputStream(output);
        int bytesSent = 0;
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) != -1) {
            os.write(buffer, 0, length);
        }
        os.flush();
        os.close();
        return bytesSent;
    }

    private byte[] readStream(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }

    private boolean validateResponse(byte[] response) {
        String s = new String(response);
        Log.i("Uploader", "Response received from server: " + s);
        System.out.println("Respuesta del server"+s);
        try {
            JSONObject json = new JSONObject(s);
            boolean success = json.getBoolean("processed");
            if (success) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

}
