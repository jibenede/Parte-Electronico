package com.puc.parte_electronico.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.model.TrafficTicket;

/**
 * Created by jose on 5/14/14.
 */
public class TicketListAdapter extends CursorAdapter {
    private final String FORMAT;


    public TicketListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        FORMAT = context.getString(R.string.traffic_ticket_description);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_traffic_ticket, parent, false);
        bindView(view, context, cursor);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView label = (TextView) view.findViewById(R.id.text);
        TrafficTicket ticket = new TrafficTicket(cursor);
        label.setText(String.format(FORMAT, ticket.getId(), ticket.getDate()));
    }
}
