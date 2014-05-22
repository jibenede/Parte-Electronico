package com.puc.parte_electronico.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
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
import android.widget.Toast;
import com.bixolon.printer.BixolonPrinter;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.globals.Settings;
import com.puc.parte_electronico.model.TrafficTicket;
import com.puc.parte_electronico.model.User;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Created by jose on 5/22/14.
 */
public class SummaryFragment extends Fragment {
    public static final String TAG = "SUMMARY_FRAGMENT";
    public static final String SUMMARY_KEY = "SUMMARY_KEY";

    private TrafficTicket mTicket;
    private BixolonPrinter mPrinter;
    private User mUser;

    private Button mPrintButton;
    private Button mQueueButton;

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
                    Toast.makeText(getActivity(), msg.getData().getString(BixolonPrinter.KEY_STRING_TOAST), Toast.LENGTH_SHORT).show();
                    return true;
                case BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET:
                    if (msg.obj == null) {
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

        mPrinter = new BixolonPrinter(getActivity(), mHandler, null);
        mUser = Settings.getSettings().getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        mPrintButton = (Button) view.findViewById(R.id.button_print);
        mPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrinter.findBluetoothPrinters();
            }
        });
        mQueueButton = (Button) view.findViewById(R.id.button_queue);

        return view;
    }

    private void showBluetoothDialog(Context context, final Set<BluetoothDevice> pairedDevices) {
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

        mPrinter.printText(mTicket.getPrinterStringSummary(),
                BixolonPrinter.ALIGNMENT_LEFT,
                BixolonPrinter.TEXT_ATTRIBUTE_FONT_A,
                BixolonPrinter.TEXT_SIZE_HORIZONTAL1,
                true);
        mPrinter.lineFeed(3, false);
        mPrinter.disconnect();
    }
}
