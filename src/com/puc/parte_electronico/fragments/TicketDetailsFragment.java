package com.puc.parte_electronico.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.puc.ParteElectronico.R;
import com.puc.parte_electronico.model.TrafficViolation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 5/13/14.
 */
public class TicketDetailsFragment extends Fragment {
    private String[] mTrafficViolationList;

    /**
     * A list of the traffic violations defined by this ticket.
     */
    private List<TrafficViolation> mViolations;

    /**
     * Flag that signals if the data contained within this form is editable or read only. Should be set to true when
     * creating new tickets and to false when reviewing them.
     */
    private boolean mEditable;

    public TicketDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_details, container, false);

        mEditable = getArguments().getBoolean("editable", false);


        if (mViolations == null) {
            mViolations = new ArrayList<TrafficViolation>();

            mViolations.add(new TrafficViolation());
            addTrafficViolationView(view, inflater, 0);
        } else {
            for (int i = 0; i < mViolations.size(); i++) {
                addTrafficViolationView(view, inflater, i);
            }
        }


        Button addViolationButton = (Button)view.findViewById(R.id.button_add_traffic_violation);
        addViolationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViolations.add(new TrafficViolation());
                addTrafficViolationView(getView(), getActivity().getLayoutInflater(), mViolations.size() - 1);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTrafficViolationList = getActivity().getResources().getStringArray(R.array.traffic_violations);
    }


    private void configureTrafficViolationItem(final View view, final int index) {
        TrafficViolation violation = mViolations.get(index);

        Button button = (Button) view.findViewById(R.id.button_select_traffic_violation);
        TextView costLabel = (TextView) view.findViewById(R.id.label_price);
        if (violation.getType() != null) {
            button.setText(violation.getType());
            costLabel.setText("" + violation.getValue());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = getTrafficViolationSelectionDialog(view, index);
                dialog.show();
            }
        });


    }

    private AlertDialog getTrafficViolationSelectionDialog(final View view, final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("placeholder")
                .setItems(mTrafficViolationList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setViolationValues(mViolations.get(index), mTrafficViolationList[which], 100000, view);
                    }
                });
        return builder.create();
    }

    private void setViolationValues(TrafficViolation violation, String type, int cost, View container) {
        violation.setType(type);
        violation.setValue(cost);

        Button button = (Button)container.findViewById(R.id.button_select_traffic_violation);
        button.setText(type);

        TextView label = (TextView)container.findViewById(R.id.label_price);
        label.setText("" + cost);
    }

    private void addTrafficViolationView(View rootView, LayoutInflater inflater, int index) {
        View trafficViolationItem = inflater.inflate(R.layout.item_traffic_violation, null);
        configureTrafficViolationItem(trafficViolationItem, index);

        LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.traffic_violation_container);
        layout.addView(trafficViolationItem);


    }
}
