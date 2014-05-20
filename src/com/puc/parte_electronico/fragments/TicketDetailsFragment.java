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
        mEditRut.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    parseRut(v);
                    return true;
                }
                return false;
            }
        });
        mEditRut.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    parseRut((TextView)v);
                }
            }
        });

        mEditVerifierDigit = (EditText)view.findViewById(R.id.edit_verifier);
        mEditFirstName = (EditText)view.findViewById(R.id.edit_first_name);
        mEditLastName = (EditText)view.findViewById(R.id.edit_last_name);
        mEditAddress = (EditText)view.findViewById(R.id.edit_address);
        mEditVehicle = (EditText)view.findViewById(R.id.edit_vehicle);
        mEditLicensePlate = (EditText)view.findViewById(R.id.edit_license_plate);

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
        if (modulo == 10) {
            mEditVerifierDigit.setText("K");
        } else {
            mEditVerifierDigit.setText("" + modulo);
        }

    }
}
