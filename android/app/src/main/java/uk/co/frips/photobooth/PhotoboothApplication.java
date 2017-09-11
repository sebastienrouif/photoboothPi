package uk.co.frips.photobooth;

import android.app.Application;
import android.content.SharedPreferences;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class PhotoboothApplication extends Application {

    public static final String SHARED_PREFS_NAME = "photobooth";
    public static final String KEY_SERVER_URL = "serverUrl";
    public static final String DEFAULT_SERVER_URL = "http://192.168.42.10:3000";
    private Socket mSocket;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        createSocket();
    }

    private void createSocket() {
        try {
            mSocket = IO.socket(getServerUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public String getServerUrl() {
        return sharedPreferences.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL);
    }

    public void setServerUrl(String url) {
        sharedPreferences.edit().putString(KEY_SERVER_URL,url).commit();
        mSocket.close();
        createSocket();
    }
}
