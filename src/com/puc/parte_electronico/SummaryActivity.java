package com.puc.parte_electronico;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.puc.parte_electronico.fragments.SummaryFragment;
import com.puc.parte_electronico.model.TrafficTicket;

/**
 * Created by jose on 5/22/14.
 */
public class SummaryActivity extends Activity {
    private TrafficTicket mTicket;



    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SummaryActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Intent intent = getIntent();
        mTicket = intent.getParcelableExtra(TrafficTicket.TICKET_KEY);

        Bundle arguments = new Bundle();
        arguments.putParcelable(TrafficTicket.TICKET_KEY, mTicket);

        SummaryFragment fragment = new SummaryFragment();
        fragment.setArguments(arguments);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment);
        ft.commit();
    }
}
