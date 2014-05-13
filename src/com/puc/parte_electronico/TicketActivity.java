package com.puc.parte_electronico;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.puc.ParteElectronico.R;
import com.puc.parte_electronico.fragments.TicketDetailsFragment;
import com.puc.parte_electronico.fragments.TicketPicturesFragment;

/**
 * Created by jose on 5/13/14.
 */
public class TicketActivity extends Activity {
    private enum FocusedFragment { DETAILS, PICTURES }

    private FocusedFragment mFocusedFragment;

    private TicketDetailsFragment mDetailsFragment;
    private TicketPicturesFragment mPicturesFragment;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, TicketActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);

        mDetailsFragment = new TicketDetailsFragment();
        mPicturesFragment = new TicketPicturesFragment();

        mFocusedFragment = FocusedFragment.DETAILS;
        setFragment(mFocusedFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        if (mFocusedFragment == FocusedFragment.DETAILS) {
            menu.add("Fotos");
        }

        if (mFocusedFragment == FocusedFragment.PICTURES) {
            menu.add("Detalles");
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (mFocusedFragment == FocusedFragment.DETAILS) {
            setFragment(FocusedFragment.PICTURES);
            mFocusedFragment = FocusedFragment.PICTURES;
        } else if (mFocusedFragment == FocusedFragment.PICTURES) {
            setFragment(FocusedFragment.DETAILS);
            mFocusedFragment = FocusedFragment.DETAILS;
        }

        invalidateOptionsMenu();
        return true;
    }

    private void setFragment(FocusedFragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment newFragment;

        if (fragment == FocusedFragment.DETAILS) {
            newFragment = mDetailsFragment;
        } else {
            newFragment = mPicturesFragment;
        }
        transaction.replace(R.id.top_view, newFragment);
        transaction.commit();

    }
}
