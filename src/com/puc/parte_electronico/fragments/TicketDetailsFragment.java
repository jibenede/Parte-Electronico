package com.puc.parte_electronico.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.TicketActivity;
import com.puc.parte_electronico.model.TrafficTicket;
import com.puc.parte_electronico.model.Validator;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jose on 5/19/14.
 */
public class TicketDetailsFragment extends Fragment implements ITicketFragment {
    public static final String TAG = "TICKET_DETAILS_FRAGMENT";
    private final int[] RUT_MULTIPLIER = new int[] { 2, 3, 4, 5, 6, 7 };

    private TrafficTicket mTicket;
    private boolean mEditable;

    private EditText mEditRut;
    private EditText mEditVerifierDigit;
    private EditText mEditFirstName;
    private EditText mEditLastName;
    private EditText mEditAddress;
    private EditText mEditVehicle;
    private EditText mEditLicensePlate;
    private EditText mEditDescription;
    private EditText mEditLocation;
    private EditText mEditMail;

    private TextView mLabelTicketType;
    private ImageButton mSwitchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTicket = arguments.getParcelable(TrafficTicket.TICKET_KEY);
            mEditable = arguments.getBoolean(TicketActivity.EDITABLE_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_details, container, false);

        mEditRut = (EditText)view.findViewById(R.id.edit_rut);
        configureEditor(mEditRut, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                parseRut(mEditRut);
            }
        });

        mEditVerifierDigit = (EditText)view.findViewById(R.id.edit_verifier);

        mEditFirstName = (EditText)view.findViewById(R.id.edit_first_name);
        configureEditor(mEditFirstName, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setFirstName(mEditFirstName.getText().toString());
            }
        });

        mEditLastName = (EditText)view.findViewById(R.id.edit_last_name);
        configureEditor(mEditLastName, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setLastName(mEditLastName.getText().toString());
            }
        });


        mEditAddress = (EditText)view.findViewById(R.id.edit_address);
        configureEditor(mEditAddress, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setAddress(mEditAddress.getText().toString());
            }
        });

        mEditVehicle = (EditText)view.findViewById(R.id.edit_vehicle);
        configureEditor(mEditVehicle, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setVehicle(mEditVehicle.getText().toString());
            }
        });

        mEditLicensePlate = (EditText)view.findViewById(R.id.edit_license_plate);
        configureEditor(mEditLicensePlate, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setLicensePlate(mEditLicensePlate.getText().toString());

                Validator<TrafficTicket> validator = TrafficTicket.sValidators.get("License Plate");
                if (!validator.validate(mTicket)) {
                    Toast.makeText(getActivity(), validator.getErrorMessage(getActivity()), Toast.LENGTH_LONG).show();
                    mTicket.setLicensePlate("");
                    mEditLicensePlate.setText("");
                }
            }
        });

        mEditDescription = (EditText)view.findViewById(R.id.edit_description);
        mEditDescription.setInputType(InputType.TYPE_CLASS_TEXT);
        mEditDescription.setSingleLine(true);
        mEditDescription.setLines(6);
        mEditDescription.setHorizontallyScrolling(false);
        mEditDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        configureEditor(mEditDescription, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setDescription(mEditDescription.getText().toString());
            }
        });

        mEditLocation = (EditText)view.findViewById(R.id.edit_location);
        configureEditor(mEditLocation, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setLocation(mEditLocation.getText().toString());
            }
        });

        mEditMail = (EditText)view.findViewById(R.id.edit_email);
        configureEditor(mEditMail, new OnEditorFinishedListener() {
            @Override
            public void execute() {
                mTicket.setEmail(mEditMail.getText().toString());
            }
        });

        mLabelTicketType = (TextView)view.findViewById(R.id.label_ticket_type);

        mSwitchButton = (ImageButton)view.findViewById(R.id.button_switch_type);
        if (!mEditable) {
            mSwitchButton.setVisibility(View.INVISIBLE);
        }
        mSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTicketType();
            }
        });

        initializeData();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        IFragmentCallbacks callback = (IFragmentCallbacks)getActivity();
        callback.updateTicket(mTicket);
    }

    @Override
    public TrafficTicket getTicket() {
        return mTicket;
    }

    private void initializeData() {
        if (mTicket.getType() == TrafficTicket.TicketType.IDENTIFICADO) {

            Integer rut = mTicket.getRut();
            if (rut != null) {
                mEditRut.setText("" + mTicket.getRut());
                checkRut(mTicket.getRut());
            }

            mEditFirstName.setText(mTicket.getFirstName());
            mEditLastName.setText(mTicket.getLastName());
            mEditAddress.setText(mTicket.getAddress());
            mEditMail.setText(mTicket.getEmail());
            mLabelTicketType.setText(getString(R.string.ticket_identificado));

            if (mEditable) {
                mEditRut.setEnabled(true);
                mEditFirstName.setEnabled(true);
                mEditLastName.setEnabled(true);
                mEditAddress.setEnabled(true);
                mEditMail.setEnabled(true);
            }
        } else {
            mEditRut.setText("");
            mEditRut.setEnabled(false);
            mEditFirstName.setText("");
            mEditFirstName.setEnabled(false);
            mEditLastName.setText("");
            mEditLastName.setEnabled(false);
            mEditAddress.setText("");
            mEditAddress.setEnabled(false);
            mEditMail.setText("");
            mEditMail.setEnabled(false);
            mEditVerifierDigit.setText("");
            mLabelTicketType.setText(getString(R.string.ticket_empadronado));
        }


        mEditVehicle.setText(mTicket.getVehicle());
        mEditLicensePlate.setText(mTicket.getLicensePlate());
        mEditLocation.setText(mTicket.getLocation());
        mEditDescription.setText(mTicket.getDescription());
    }

    private void switchTicketType() {
        if (mTicket.getType() == TrafficTicket.TicketType.IDENTIFICADO) {
            mTicket.setType(TrafficTicket.TicketType.EMPADRONADO);
        } else {
            mTicket.setType(TrafficTicket.TicketType.IDENTIFICADO);
        }
        initializeData();
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

    private void configureEditor(EditText editText, OnEditorFinishedListener listener) {
        editText.setEnabled(mEditable);
        if (!mEditable) {
            editText.setTextColor(Color.WHITE);
        }
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
