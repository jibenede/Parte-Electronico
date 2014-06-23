package com.puc.parte_electronico.network;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puc.parte_electronico.globals.CryptoUtilities;
import com.puc.parte_electronico.model.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

/**
 * Created by jose on 6/23/14.
 */
public class Connector {
    public static LoginResponse sendLoginCredentials(String username, String password) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost("http://partes-electronicos.herokuapp.com/api/login");
            postRequest.addHeader(new BasicHeader("Content-Type", "application/json"));

            postRequest.setEntity(new StringEntity(
                    "{\"username\":\"" + username + "\", \"password_digest\":\"" + CryptoUtilities.hash(password) + "\"}"));
            HttpResponse response = client.execute(postRequest);
            String body = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            LoginResponse loginResponse = mapper.readValue(body, LoginResponse.class);
            return loginResponse;

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Exception, most likely due to connection error
        return null;
    }

    public static User[] sendUsersRequest(String accessToken) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet("http://partes-electronicos.herokuapp.com/api/users/" + accessToken);

            HttpResponse response = client.execute(getRequest);
            if (response.getStatusLine().getStatusCode() / 100 == 2) {
                String body = EntityUtils.toString(response.getEntity());

                ObjectMapper mapper = new ObjectMapper();
                User[] users = mapper.readValue(body, User[].class);

                return users;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class LoginResponse {
        @JsonProperty("authenticated")
        private boolean mAuthenticated;
        @JsonProperty("access_token")
        private String mAccessToken;
        @JsonProperty("error")
        private boolean mError;
        @JsonProperty("message")
        private String mMessage;

        private LoginResponse() {}

        public LoginResponse(boolean success, String accessToken) {
            mAuthenticated = success;
            mAccessToken = accessToken;
        }

        public boolean isAuthenticated() {
            return mAuthenticated;
        }

        private void setAuthenticated(boolean authenticated) {
            mAuthenticated = authenticated;
        }

        public String getAccessToken() {
            return mAccessToken;
        }

        private void setAccessToken(String accessToken) {
            mAccessToken = accessToken;
        }

        public boolean isError() {
            return mError;
        }

        private void setError(boolean error) {
            mError = error;
        }

        public String getMessage() {
            return mMessage;
        }

        private void setMessage(String message) {
            mMessage = message;
        }

    }
}
