package com.puc.parte_electronico;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.puc.parte_electronico.fragments.IFragmentCallbacks;
import com.puc.parte_electronico.fragments.TicketDetailsFragment;
import com.puc.parte_electronico.fragments.TicketPicturesFragment;
import com.puc.parte_electronico.fragments.TicketViolationsFragment;
import com.puc.parte_electronico.globals.Settings;
import com.puc.parte_electronico.location.LocationResolver;
import com.puc.parte_electronico.model.TrafficTicket;
import com.puc.parte_electronico.model.TrafficViolation;

/**
 * Created by jose on 5/13/14.
 */
public class TicketActivity extends Activity implements IFragmentCallbacks {
    public static final String EDITABLE_KEY = "EDITABLE_KEY";
    private static final String FOCUSED_FRAGMENT_KEY = "FOCUSED_FRAGMENT_KEY";
    private static final String STATE_KEY = "STATE_KEY";

    private String mCurrentFragmentTag;
    private int mFocusedTabPosition;
    private TrafficTicket mTrafficTicket;
    private boolean mEditable;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, TicketActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);

        LocationResolver.startLocator(this);



        if (savedInstanceState == null) {
            TrafficTicket ticket = getIntent().getParcelableExtra(TrafficTicket.TICKET_KEY);
            if (ticket == null) {
                mTrafficTicket = new TrafficTicket(Settings.getSettings().getCurrentUser());
                mTrafficTicket.addTrafficViolation(new TrafficViolation());
            } else {
                mTrafficTicket = ticket;
            }
            mFocusedTabPosition = 0;
            mEditable = getIntent().getBooleanExtra(EDITABLE_KEY, false);
        } else {
            mTrafficTicket = savedInstanceState.getParcelable(TrafficTicket.TICKET_KEY);
            mFocusedTabPosition = savedInstanceState.getInt(FOCUSED_FRAGMENT_KEY, 0);
            mEditable = savedInstanceState.getBoolean(EDITABLE_KEY);
        }

        if (!mEditable) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = actionBar.newTab()
                .setText(R.string.ticket_option_details)
                .setTabListener(new TabListener<TicketDetailsFragment>(
                        this, TicketDetailsFragment.TAG, TicketDetailsFragment.class));
        actionBar.addTab(tab, 0, 0 == mFocusedTabPosition);

        tab = actionBar.newTab()
                .setText(R.string.ticket_option_violations)
                .setTabListener(new TabListener<TicketViolationsFragment>(
                        this, TicketViolationsFragment.TAG, TicketViolationsFragment.class));
        actionBar.addTab(tab, 1, 1 == mFocusedTabPosition);

        tab = actionBar.newTab()
                .setText(R.string.ticket_option_pictures)
                .setTabListener(new TabListener<TicketPicturesFragment>(
                        this, TicketPicturesFragment.TAG, TicketPicturesFragment.class));
        actionBar.addTab(tab, 2, 2 == mFocusedTabPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mEditable) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_details, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                ((EditText)v).clearFocus();
            }

            String errorMessage = mTrafficTicket.isValid(this);
            if (errorMessage == null) {
                Intent intent = SummaryActivity.getIntent(this);
                intent.putExtra(TrafficTicket.TICKET_KEY, mTrafficTicket);
                this.startActivityForResult(intent, SummaryActivity.REQUEST_CODE);
            } else {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }

            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FOCUSED_FRAGMENT_KEY, getActionBar().getSelectedTab().getPosition());
        outState.putParcelable(TrafficTicket.TICKET_KEY, mTrafficTicket);
        outState.putBoolean(EDITABLE_KEY, mEditable);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TicketPicturesFragment.BACKGROUND_CAMERA_CODE ||
                    requestCode == TicketPicturesFragment.EVIDENCE_CAMERA_CODE) {
                TicketPicturesFragment fragment = (TicketPicturesFragment)getFragmentManager().findFragmentByTag(
                        TicketPicturesFragment.TAG);
                fragment.handlePicture(requestCode);
            } else if (requestCode == SummaryActivity.REQUEST_CODE) {
                finish();
            }
        }
    }

    @Override
    public void updateTicket(TrafficTicket ticket) {
        mTrafficTicket = ticket;
    }

    @Override
    public TrafficTicket getTicket() {
        return mTrafficTicket;
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        //check if no view has focus:
        View v = getCurrentFocus();
        if(v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private TicketActivity mActivity;
        private String mTag;
        private Class<T> mClass;

        public TabListener(TicketActivity activity, String tag, Class<T> clazz) {
            mActivity = activity;
            mTag = tag;
            mClass = clazz;
            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            mActivity.hideSoftKeyboard();

            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                Bundle arguments = new Bundle();
                arguments.putBoolean(EDITABLE_KEY, mActivity.mEditable);
                arguments.putParcelable(TrafficTicket.TICKET_KEY, mActivity.mTrafficTicket);
                mFragment.setArguments(arguments);


                ft.replace(R.id.top_view, mFragment, mTag);
            } else {
                ft.replace(R.id.top_view, mFragment, mTag);
            }
            mActivity.mCurrentFragmentTag = mTag;
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }


    }
}
