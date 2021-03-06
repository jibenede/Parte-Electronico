package com.puc.parte_electronico.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.puc.parte_electronico.NavigationActivity;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.globals.Settings;
import com.puc.parte_electronico.model.Database;
import com.puc.parte_electronico.model.Parameter;
import com.puc.parte_electronico.model.User;
import com.puc.parte_electronico.network.Connector;

/**
 * Created by jose on 5/13/14.
 */
public class LoginFragment extends Fragment {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;
    private String mPin;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPinView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container);

        mEmailView = (EditText) view.findViewById(R.id.email);
        mPasswordView = (EditText) view.findViewById(R.id.password);
        mPinView = (EditText) view.findViewById(R.id.pin);

        mLoginFormView = view.findViewById(R.id.login_form);
        mLoginStatusView = view.findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) view.findViewById(R.id.login_status_message);

        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = mPinView.getText().toString();
                if (openDataBase(pin)) {
                    attemptLogin();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.wrong_pin_error), Toast.LENGTH_LONG).show();
                }

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private boolean openDataBase(String pin) {

        // Temporary dummy login logic for testing purposes. Delete when done.
        try {
            Settings settings = Settings.getSettings();
            if (settings.getDatabase() == null) {
                Database database = new Database(getActivity(), pin);
                settings.setDatabase(database);
            }
            return true;
        } catch (Database.WrongEncryptionPasswordException e) {
            return false;
        }
    }

    /**
     * Attempts to login the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        mPin = mPinView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid pin.
        if (TextUtils.isEmpty(mPin)) {
            mPinView.setError(getString(R.string.error_field_required));
            focusView = mPinView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        /*
        else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private boolean mError;
        private String mErrorDescription;
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            // Try to validate from the local Database.
            Settings settings = Settings.getSettings();
            Database database = settings.getDatabase();

            // First we check if the user is stored locally
            User user = User.getUser(database, mEmail, mPassword);

            // If it is not, we try to login to the server
            if (user == null) {
                Connector.LoginResponse loginResponse = Connector.sendLoginCredentials(mEmail, mPassword);
                if (loginResponse != null) {
                    if (loginResponse.isAuthenticated()) {
                        // We get an updated list of users and update the database
                        User[] users = Connector.sendUsersRequest(loginResponse.getAccessToken());
                        if (users == null) {
                            mError = true;
                            mErrorDescription = getActivity().getString(R.string.login_connection_error);
                        } else {
                            User.deleteUsers(database);
                            for (User u : users) {
                                u.insert(database);
                            }

                            user = User.getUser(database, mEmail, mPassword);

                            Parameter accessToken = Parameter.getParameter(database, Parameter.PARAMETER_ACCESS_TOKEN);
                            if (accessToken == null) {
                                accessToken = new Parameter(Parameter.PARAMETER_ACCESS_TOKEN, loginResponse.getAccessToken());
                                accessToken.insert(database);
                            } else {
                                accessToken.setValue(loginResponse.getAccessToken());
                                accessToken.update(database);
                            }
                            settings.setAccessToken(accessToken.getValue());
                        }
                    } else {
                        mError = true;
                        mErrorDescription = getActivity().getString(R.string.login_wrong_username_password);
                    }
                } else {
                    mError = true;
                    mErrorDescription = getActivity().getString(R.string.login_connection_error);
                }

            }

            if (user != null) {
                settings.setCurrentUser(user);
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (!mError) {
                startActivity(NavigationActivity.getIntent(getActivity()));
            } else {
                Toast.makeText(getActivity(), mErrorDescription, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
