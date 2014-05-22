package com.puc.parte_electronico;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.puc.parte_electronico.fragments.IFragmentCallbacks;
import com.puc.parte_electronico.fragments.TicketDetailsFragment;
import com.puc.parte_electronico.fragments.TicketPicturesFragment;
import com.puc.parte_electronico.fragments.TicketViolationsFragment;

/**
 * Created by jose on 5/13/14.
 */
public class TicketActivity extends Activity implements IFragmentCallbacks {
    private static final String FOCUSED_FRAGMENT_KEY = "FOCUSED_FRAGMENT_KEY";
    private static final String FRAGMENT_STATES_KEY = "FRAGMENT_STATES";

    private int mFocusedTabPosition;
    private Bundle mFragmentStates;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, TicketActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);


        if (savedInstanceState == null) {
            mFragmentStates = new Bundle();
            mFocusedTabPosition = 0;
        } else {
            mFragmentStates = savedInstanceState.getBundle(FRAGMENT_STATES_KEY);
            mFocusedTabPosition = savedInstanceState.getInt(FOCUSED_FRAGMENT_KEY, 0);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FOCUSED_FRAGMENT_KEY, getActionBar().getSelectedTab().getPosition());
        outState.putBundle(FRAGMENT_STATES_KEY, mFragmentStates);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK &&
                (requestCode == TicketPicturesFragment.BACKGROUND_CAMERA_CODE ||
                requestCode == TicketPicturesFragment.EVIDENCE_CAMERA_CODE)) {
            TicketPicturesFragment fragment = (TicketPicturesFragment)getFragmentManager().findFragmentByTag(
                    TicketPicturesFragment.TAG);
            fragment.handlePicture(requestCode);
        }
    }

    @Override
    public void saveState(String tag, Bundle data) {
        mFragmentStates.putBundle(tag, data);
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
            hideSoftKeyboard();

            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                Bundle arguments = mActivity.mFragmentStates.getBundle(mTag);
                if (arguments != null) {
                    mFragment.setArguments(arguments);
                }

                ft.replace(R.id.top_view, mFragment, mTag);
            } else {
                ft.replace(R.id.top_view, mFragment, mTag);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        private void hideSoftKeyboard() {
            InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);

            //check if no view has focus:
            View v = mActivity.getCurrentFocus();
            if(v == null)
                return;

            inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
