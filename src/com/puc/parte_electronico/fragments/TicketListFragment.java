package com.puc.parte_electronico.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.model.TrafficTicket;

/**
 * Created by jose on 5/13/14.
 */
public class TicketListFragment extends ListFragment {
    private long mTimeOfLastUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_list, container);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        resetAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mTimeOfLastUpdate != TrafficTicket.getTimeOfLastInsert()) {
            resetAdapter();
        }
    }

    private void resetAdapter() {
        CursorAdapter adapter = TrafficTicket.getAdapter(getActivity());
        setListAdapter(adapter);
        mTimeOfLastUpdate = TrafficTicket.getTimeOfLastInsert();
    }
}
