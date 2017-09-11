package uk.co.frips.photobooth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    public static final String EVENT_COUNTDOWN = "countDown";
    public static final String EVENT_PICTURE = "picture";
    public static final String EVENT_RESET = "reset";
    public static final String EVENT_SEND_TAKE_PICTURE = "start-takePictures";
    private static final String TAG = MainActivity.class.getSimpleName();

    private Socket mSocket;

    @BindView(R.id.main_photo) ImageView mPhotoView;
    @BindView(R.id.main_countdown) TextView mCountdownTextView;
    @BindView(R.id.main_lottie) LottieAnimationView mLottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSocket = ((PhotoboothApplication) getApplication()).getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(EVENT_COUNTDOWN, onCountdown);
        mSocket.on(EVENT_PICTURE, onPicture);
        mSocket.on(EVENT_RESET, onReset);
        mSocket.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off(EVENT_COUNTDOWN, onCountdown);
        mSocket.off(EVENT_PICTURE, onPicture);
        mSocket.off(EVENT_RESET, onReset);
    }

    private void showCountdown(Integer countdown) {
        if (countdown == 2) {
            hideImage();
            mLottie.setVisibility(View.VISIBLE);
            mLottie.playAnimation();
        }
    }

    private void showImage(String data) {
        mLottie.setVisibility(GONE);
        Picasso.with(this).load(data).into(mPhotoView);
        mPhotoView.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPhotoView.setVisibility(GONE);
            }
        }, TimeUnit.SECONDS.toMillis(5));
    }

    private void hideImage() {
        mPhotoView.setVisibility(GONE);
    }

    private void showDefaultState() {
        mPhotoView.setVisibility(GONE);
    }

    private void showEditUrlDialog() {
        final EditText editText = new EditText(this);
        editText.setText(((PhotoboothApplication) getApplication()).getServerUrl());
        new AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = editText.getText().toString();
                        ((PhotoboothApplication) getApplication()).setServerUrl(text);
                        restartActivity();
                    }
                })
                .create()
                .show();
    }

    private void restartActivity() {
        this.finishAffinity();
        this.startActivity(new Intent(this, MainActivity.class));
    }

    //Event Management

    @OnClick(R.id.main_photo)
    public void onPhotoClicked() {
        takePicture();
    }

    @OnClick(R.id.main_hidden_button)
    public void onHiddenButtonClicked() {
        showEditUrlDialog();
    }

    private void takePicture() {
        mSocket.emit(EVENT_SEND_TAKE_PICTURE);
    }

    private Emitter.Listener onCountdown = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Integer countdown = (Integer) args[0];
                    log("onCountdown " + countdown);
                    showCountdown(countdown);
                }
            });
        }
    };

    private Emitter.Listener onPicture = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];
                    String serverUrl = ((PhotoboothApplication) getApplication()).getServerUrl();
                    log("onPicture " + data + " server Url" + serverUrl);
                    showImage(serverUrl + "/" + data);
                }
            });
        }
    };

    private Emitter.Listener onReset = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    log("onReset " + data);
                    showDefaultState();
                }
            });
        }
    };

    //Connection Management

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    log("onConnect");
                    toast("onConnect");
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    log("onDisconnect");
                    toast("onDisconnect");
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    log("onConnectError");
                    toast("onConnectError");
                }
            });
        }
    };

    //utils

    private void log(String message) {
        Log.d(TAG, message);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
