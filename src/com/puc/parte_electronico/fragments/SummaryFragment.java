package com.puc.parte_electronico.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.bixolon.printer.BixolonPrinter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.globals.Settings;
import com.puc.parte_electronico.model.Picture;
import com.puc.parte_electronico.model.TrafficTicket;
import com.puc.parte_electronico.model.User;
import com.puc.parte_electronico.uploader.FileZipper;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jose on 5/22/14.
 */
public class SummaryFragment extends Fragment {
    public static final String TAG = "SUMMARY_FRAGMENT";
    public static final String MAP_ENABLED_KEY = "MAP_ENABLED_KEY";
    public static final String SUMMARY_KEY = "SUMMARY_KEY";

    private static final String TEMP_FILE = "data.json";
    private static final String MAP_TAG = "MAP_FRAGMENT";
    private static final float DEFAULT_ZOOM = 13;

    private TrafficTicket mTicket;
    private BixolonPrinter mPrinter;
    private User mUser;
    private boolean mMapEnabled;

    private Button mPrintButton;
    private Button mQueueButton;

    private MapFragment mMapFragment;
    private GoogleMap mMap;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "mHandler.handleMessage(" + msg + ")");

            switch (msg.what) {
                case BixolonPrinter.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BixolonPrinter.STATE_CONNECTED:
                            print();
                            break;

                        case BixolonPrinter.STATE_CONNECTING:
                            break;

                        case BixolonPrinter.STATE_NONE:
                            break;
                    }
                    return true;
                case BixolonPrinter.MESSAGE_TOAST:
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), msg.getData().getString(BixolonPrinter.KEY_STRING_TOAST), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET:
                    if (msg.obj == null && getActivity() != null) {
                        Toast.makeText(getActivity(), "No paired device", Toast.LENGTH_SHORT).show();
                    } else {
                        showBluetoothDialog(getActivity(), (Set<BluetoothDevice>) msg.obj);
                    }
                    return true;
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mTicket = arguments.getParcelable(TrafficTicket.TICKET_KEY);
        mMapEnabled = arguments.getBoolean(MAP_ENABLED_KEY, false);

        mPrinter = new BixolonPrinter(getActivity(), mHandler, null);
        mUser = Settings.getSettings().getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        TextView summaryTextView = (TextView)view.findViewById(R.id.text_summary);
        summaryTextView.setText(mTicket.getPrinterStringSummary());

        mPrintButton = (Button) view.findViewById(R.id.button_print);
        mPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrinter.findBluetoothPrinters();
                mQueueButton.setEnabled(true);
            }
        });
        mQueueButton = (Button) view.findViewById(R.id.button_queue);
        mQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queue();
                ISummaryListener listener = (ISummaryListener)getActivity();
                listener.onTicketQueued();
            }
        });

        if (mMapEnabled) {
            ViewGroup containerView = (ViewGroup) view.findViewById(R.id.map_container);
            containerView.setVisibility(View.VISIBLE);

            if (mMapEnabled && getChildFragmentManager().findFragmentByTag(MAP_TAG) == null) {
                mMapFragment = MapFragment.newInstance();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.map_container, mMapFragment, MAP_TAG);

                transaction.commit();
            }
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMapEnabled) {
            configureMap(mMapFragment, mTicket.getLatitude(), mTicket.getLongitude());
            
        }
    }

    private void configureMap(MapFragment mapFragment, double latitude, double longitude) {
        GoogleMap map = mapFragment.getMap();
        if (map != null) {
            LatLng latLng = new LatLng(latitude, longitude);
            map.moveCamera( CameraUpdateFactory.newLatLngZoom(
                    latLng, DEFAULT_ZOOM));

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            map.addMarker(markerOptions);
        }
    }

    private void showBluetoothDialog(Context context, final Set<BluetoothDevice> pairedDevices) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mTicket.dumpJsonToStream(baos);
            Log.i("test", baos.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String[] items = new String[pairedDevices.size()];
        int index = 0;
        for (BluetoothDevice device : pairedDevices) {
            items[index++] = device.getAddress();
        }

        new AlertDialog.Builder(context).setTitle("Paired Bluetooth printers")
                .setItems(items, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        mPrinter.connect(items[which]);

                    }
                }).show();
    }

    private void print() {
        String ticketString = mTicket.getPrinterStringSummary();

        mPrinter.printText(ticketString,
                BixolonPrinter.ALIGNMENT_LEFT,
                BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                BixolonPrinter.TEXT_SIZE_HORIZONTAL1,
                true);
        mPrinter.lineFeed(3, false);
        mPrinter.disconnect();
    }

    private void queue() {
        File file = new File(getActivity().getFilesDir(), TEMP_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            mTicket.dumpJsonToStream(fos);

            List<String> files = new ArrayList<String>();
            files.add(file.getAbsolutePath());
            for (Picture picture : mTicket.getPictures()) {
                files.add(picture.getPath());
            }

            FileZipper zipper = new FileZipper(getActivity(), files);
            zipper.zip();
            file.delete();

            mTicket.setZipPath(zipper.getZippedFilePath());
            mTicket.insert();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
