package com.puc.parte_electronico.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.globals.Settings;
import com.puc.parte_electronico.model.TrafficTicket;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jose on 5/19/14.
 */
public class TicketDetailsFragment extends Fragment {
    public static final String TAG = "TICKET_DETAILS_FRAGMENT";
    private static final String TICKET_KEY = "TICKET_KEY";
    private final int[] RUT_MULTIPLIER = new int[] { 2, 3, 4, 5, 6, 7 };

    private TrafficTicket mTicket;

    private EditText mEditRut;
    private EditText mEditVerifierDigit;
    private EditText mEditFirstName;
    private EditText mEditLastName;
    private EditText mEditAddress;
    private EditText mEditVehicle;
    private EditText mEditLicensePlate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTicket = arguments.getParcelable(TICKET_KEY);
        }

        if (mTicket == null) {
            mTicket = new TrafficTicket(Settings.getSettings().getCurrentUser());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_details, container, false);

        mEditRut = (EditText)view.findViewById(R.id.edit_rut);
        setEditorListener(mEditRut, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                parseRut(mEditRut);
            }
        });

        mEditVerifierDigit = (EditText)view.findViewById(R.id.edit_verifier);

        mEditFirstName = (EditText)view.findViewById(R.id.edit_first_name);
        setEditorListener(mEditFirstName, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setFirstName(mEditFirstName.getText().toString());
            }
        });

        mEditLastName = (EditText)view.findViewById(R.id.edit_last_name);
        setEditorListener(mEditLastName, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setLastName(mEditLastName.getText().toString());
            }
        });


        mEditAddress = (EditText)view.findViewById(R.id.edit_address);
        setEditorListener(mEditAddress, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setAddress(mEditAddress.getText().toString());
            }
        });

        mEditVehicle = (EditText)view.findViewById(R.id.edit_vehicle);
        setEditorListener(mEditVehicle, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setVehicle(mEditVehicle.getText().toString());
            }
        });

        mEditLicensePlate = (EditText)view.findViewById(R.id.edit_license_plate);
        setEditorListener(mEditLicensePlate, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setLicensePlate(mEditLicensePlate.getText().toString());
            }
        });

        initializeData();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        IFragmentCallbacks callback = (IFragmentCallbacks)getActivity();
        callback.saveState(TAG, getState());
    }

    private void initializeData() {
        Integer rut = mTicket.getRut();
        if (rut != null) {
            mEditRut.setText("" + mTicket.getRut());
            checkRut(mTicket.getRut());
        }

        mEditFirstName.setText(mTicket.getFirstName());
        mEditLastName.setText(mTicket.getLastName());
        mEditAddress.setText(mTicket.getAddress());
        mEditVehicle.setText(mTicket.getVehicle());
        mEditLicensePlate.setText(mTicket.getLicensePlate());
    }

    private Bundle getState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TICKET_KEY, mTicket);
        return bundle;
    }

    private void parseRut(TextView v) {
        String rut = v.getText().toString();
        if (rut.length() == 0) {
            return;
        }

        int value = Integer.parseInt(rut);
        checkRut(value);
        mTicket.setRut(value);
    }

    private void checkRut(int rut) {
        int sum = 0;
        int counter = 0;

        while (rut > 0) {
            int digit = rut % 10;
            sum += digit * RUT_MULTIPLIER[counter];
            rut /= 10;
            counter = (counter + 1) % 6;
        }

        int modulo = sum % 11;
        int verifier = 11 - modulo;
        if (verifier == 11) {
            verifier = 0;
        }

        if (verifier == 10) {
            mEditVerifierDigit.setText("K");
        } else {
            mEditVerifierDigit.setText("" + verifier);
        }

    }

    private void setEditorListener(EditText editText, OnEditorFinishedListener listener) {
        editText.setOnEditorActionListener(listener);
        editText.setOnFocusChangeListener(listener);
    }

    static abstract class OnEditorFinishedListener implements TextView.OnEditorActionListener, View.OnFocusChangeListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                execute();
            }
            return false;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                execute();
            }
        }

        public abstract void execute();
    }


}
