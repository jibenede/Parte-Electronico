package com.puc.parte_electronico.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.puc.ParteElectronico.R;
import com.puc.parte_electronico.NavigationActivity;
import com.puc.parte_electronico.globals.Settings;
import com.puc.parte_electronico.model.Database;
import com.puc.parte_electronico.model.User;

/**
 * Created by jose on 5/13/14.
 */
public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container);
        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginTouched(v);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void onLoginTouched(View v) {
        // TODO: add login logic

        // Temporary dummy login logic for testing purposes. Delete when done.
        try {
            Settings settings = Settings.getSettings();
            Database database = new Database(getActivity(), "1234");
            User dummyUser = User.getUser(database, "admin", "1234");
            settings.setDatabase(database);
            settings.setCurrentUser(dummyUser);
            startActivity(NavigationActivity.getIntent(getActivity()));
        } catch (Database.WrongEncryptionPasswordException e) {
            // TODO: handle error appropriately
        }




    }
}
