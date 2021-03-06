package com.puc.parte_electronico;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.puc.parte_electronico.uploader.Uploader;

/**
 * Created by jose on 5/13/14.
 */
public class NavigationActivity extends Activity {
    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, NavigationActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: uncomment when server becomes available

        Intent intent = Uploader.getIntent(this);
        startService(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == 0) {
            Intent intent = TicketActivity.getIntent(this);
            intent.putExtra(TicketActivity.EDITABLE_KEY, true);
            startActivity(intent);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
