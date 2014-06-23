package com.puc.parte_electronico.uploader;

import android.content.Context;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A file compression utility class using the Zip algorithm.
 * 
 * It can compress single files or folders with no sub-folders.
 * 
 * @author Jose Ignacio Benedetto (jibenedettoc@gmail.com)
 */
public class FileZipper {
    private String mZippedFileName;
    private Context mContext;
    private String[] mFiles;

    public FileZipper(Context context, List<String> files) {
        this(context, files.toArray(new String[files.size()]));
    }

    public FileZipper(Context context, String[] files) {
        mContext = context;
        mZippedFileName = String.format("%s.zip", UUID.randomUUID().toString());
        mFiles = files;

        File directory = new File(mContext.getFilesDir().getAbsolutePath()
                + "/background_uploader/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

    }

    /**
     * Gets a default path in the Android file system where the compressed zip
     * file will be saved.
     * 
     * @return The absolute path of the zipped file.
     */
    public String getZippedFilePath() {
        return mContext.getExternalFilesDir("Temp").getAbsolutePath()
                + "/background_uploader/" + mZippedFileName;
    }

    /**
     * Compresses the file or folder and stores it in the path returned by
     * {@link #getZippedFilePath}.
     * 
     * Note that this method is non recursive, if the given file is a directory,
     * it will skip all its sub-folders.
     * 
     * @return {@code true} it the compression was successful, {@code false}
     *         otherwise.
     */
    public boolean zip() {
        boolean success = false;
        File zippedFile = new File(getZippedFilePath());
        zippedFile.getParentFile().mkdirs();

        ZipOutputStream zos = null;
        try {
        	FileOutputStream fos =  new FileOutputStream(zippedFile);

            zos = new ZipOutputStream(new BufferedOutputStream(fos));
            byte[] buffer = new byte[1024];
            for (int i = 0; i < mFiles.length; ++i) {
                File file = new File(mFiles[i]);
                String filename = file.getName();
                ZipEntry entry = new ZipEntry(filename);
                zos.putNextEntry(entry);


                FileInputStream fis = new FileInputStream(file);
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, length);
                }
                fis.close();

                zos.closeEntry();
            }
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            zippedFile.delete();
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }
    


}
