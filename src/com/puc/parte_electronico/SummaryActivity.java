package com.puc.parte_electronico;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.maps.MapFragment;
import com.puc.parte_electronico.fragments.ISummaryListener;
import com.puc.parte_electronico.fragments.SummaryFragment;
import com.puc.parte_electronico.location.LocationResolver;
import com.puc.parte_electronico.model.TrafficTicket;

/**
 * Created by jose on 5/22/14.
 */
public class SummaryActivity extends Activity implements ISummaryListener {
    public static final int REQUEST_CODE = 1008;

    private TrafficTicket mTicket;
    private MapFragment mMapFragment;

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

        Location location = LocationResolver.getLocation();
        if (location != null && mTicket.getLatitude() == null && mTicket.getLongitude() == null) {
            mTicket.setLatitude(location.getLatitude());
            mTicket.setLongitude(location.getLongitude());
            arguments.putBoolean(SummaryFragment.MAP_ENABLED_KEY, true);
        }

        arguments.putParcelable(TrafficTicket.TICKET_KEY, mTicket);
        SummaryFragment summaryFragment = new SummaryFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, summaryFragment);
        ft.commit();

        summaryFragment.setArguments(arguments);
    }



    @Override
    public void onTicketQueued() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}
