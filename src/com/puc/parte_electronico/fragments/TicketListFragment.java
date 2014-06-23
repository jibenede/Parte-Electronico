package com.puc.parte_electronico.fragments;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.TicketActivity;
import com.puc.parte_electronico.adapters.TicketListAdapter;
import com.puc.parte_electronico.model.TrafficTicket;

/**
 * Created by jose on 5/13/14.
 */
public class TicketListFragment extends ListFragment {
    public static final String TAG = "FRAGMENT_TICKET_LIST";
    private long mTimeOfLastUpdate;
    private TicketListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_list, container);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        resetAdapter();
        ListView list = getListView();
        if (list != null) {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TrafficTicket ticket = mAdapter.getTicket(position);
                    Intent intent = TicketActivity.getIntent(getActivity());
                    intent.putExtra(TicketActivity.EDITABLE_KEY, false);
                    intent.putExtra(TrafficTicket.TICKET_KEY, ticket);
                    getActivity().startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mTimeOfLastUpdate != TrafficTicket.getTimeOfLastInsert()) {
            resetAdapter();
        }
    }

    private void resetAdapter() {
        mAdapter = TrafficTicket.getAdapter(getActivity());
        setListAdapter(mAdapter);
        mTimeOfLastUpdate = TrafficTicket.getTimeOfLastInsert();
    }
}
