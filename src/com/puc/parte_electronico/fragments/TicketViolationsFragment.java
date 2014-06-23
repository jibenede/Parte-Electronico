package com.puc.parte_electronico.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.model.TrafficTicket;
import com.puc.parte_electronico.model.TrafficViolation;

/**
 * Created by jose on 5/13/14.
 */
public class TicketViolationsFragment extends Fragment implements ITicketFragment {
    public static final String TAG = "TICKET_VIOLATIONS_FRAGMENT";
    public static final String VIOLATIONS_KEY = "VIOLATIONS_KEY";
    public static final String EDITABLE_KEY = "EDITABLE_KEY";
    private String[] mTrafficViolationList;

    private ViewGroup mContainerView;

    /**
     * A list of the traffic violations defined by this ticket.
     */
    private TrafficTicket mTicket;

    /**
     * Flag that signals if the data contained within this form is editable or read only. Should be set to true when
     * creating new tickets and to false when reviewing them.
     */
    private boolean mEditable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mEditable = getArguments().getBoolean(EDITABLE_KEY, false);
            mTicket = arguments.getParcelable(TrafficTicket.TICKET_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_violations, container, false);

        mContainerView = (ViewGroup) view.findViewById(R.id.traffic_violation_container);
        mTrafficViolationList = getActivity().getResources().getStringArray(R.array.traffic_violations);

        for (TrafficViolation violation : mTicket.getViolations()) {
            addTrafficViolationView(view, inflater, violation);
        }

        Button addViolationButton = (Button) view.findViewById(R.id.button_add_traffic_violation);
        addViolationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrafficViolation violation = new TrafficViolation();
                mTicket.addTrafficViolation(violation);
                addTrafficViolationView(getView(), getActivity().getLayoutInflater(), violation);
            }
        });
        if (!mEditable) {
            addViolationButton.setVisibility(View.GONE);
        }

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

    private AlertDialog getTrafficViolationSelectionDialog(final View view, final TrafficViolation violation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_traffic_violation))
                .setItems(mTrafficViolationList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setViolationValues(violation, mTrafficViolationList[which], 100000, view);
                    }
                });
        return builder.create();
    }

    private void setViolationValues(TrafficViolation violation, String type, int cost, View container) {
        violation.setType(type);
        violation.setValue(cost);

        EditText editText = (EditText)container.findViewById(R.id.edit_traffic_violation);
        editText.setText(type);
    }

    private void addTrafficViolationView(View rootView, LayoutInflater inflater, TrafficViolation violation) {
        View trafficViolationItem = inflater.inflate(R.layout.item_traffic_violation, null);
        configureTrafficViolationItem(trafficViolationItem, violation);

        mContainerView.addView(trafficViolationItem);
    }

    // static int id = 10000000;

    private void configureTrafficViolationItem(final View view, final TrafficViolation violation) {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)view.findViewById(
                R.id.edit_traffic_violation);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.autocomplete_traffic_violation, R.id.suggestion, mTrafficViolationList);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setEnabled(mEditable);

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = v.getText().toString();
                    if (isTrafficViolationValid(text)) {
                        violation.setType(text);
                    } else {
                        v.setError(getString(R.string.invalid_violation));
                    }

                }
                return false;
            }
        });
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String text = ((EditText)v).getText().toString();
                    if (isTrafficViolationValid(text)) {
                        violation.setType(text);
                    } else {
                        ((EditText)v).setError(getString(R.string.invalid_violation));
                    }
                }
            }
        });

        if (violation.getType() != null) {
            autoCompleteTextView.setText(violation.getType());
        }

        ImageButton button = (ImageButton) view.findViewById(R.id.button_select_violation);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = getTrafficViolationSelectionDialog(view, violation);
                dialog.show();
            }
        });
        button.setEnabled(mEditable);

        Button deleteViolationButton = (Button) view.findViewById(R.id.button_delete_violation);
        deleteViolationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTicket.getViolations().size() > 1) {
                    mTicket.getViolations().remove(violation);
                    mContainerView.removeView(view);
                }
            }
        });
        deleteViolationButton.setEnabled(mEditable);

    }

    private boolean isTrafficViolationValid(String text) {
        for (String s : mTrafficViolationList) {
            if (text.equals(s)) {
                return true;
            }
        }
        return false;
    }


}
