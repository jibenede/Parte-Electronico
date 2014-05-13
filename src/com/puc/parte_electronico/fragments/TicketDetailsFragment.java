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

    private List<TrafficViolation> mViolations;

    public TicketDetailsFragment() {
        mViolations = new ArrayList<TrafficViolation>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_details, container, false);


        View trafficViolationItem = view.findViewById(R.id.item_traffic_violation);
        configureTrafficViolationItem(trafficViolationItem, 0);
        mViolations.add(new TrafficViolation());

        Button addViolationButton = (Button)view.findViewById(R.id.button_add_traffic_violation);
        addViolationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTrafficViolation();
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
        Button button = (Button) view.findViewById(R.id.button_select_traffic_violation);
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

    private void addTrafficViolation() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View trafficViolationItem = inflater.inflate(R.layout.item_traffic_violation, null);
        configureTrafficViolationItem(trafficViolationItem, mViolations.size());
        mViolations.add(new TrafficViolation());

        LinearLayout layout = (LinearLayout)getView().findViewById(R.id.traffic_violation_container);
        layout.addView(trafficViolationItem);


    }
}
