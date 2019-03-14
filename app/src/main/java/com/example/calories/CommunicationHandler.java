package com.example.calories;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

public class CommunicationHandler {
    private static final String TAG = "CommunicationHandler";
    private static final String IP_ADDRESS = "http://192.168.0.67:8080";
    private static CommunicationHandler instance;
    private final Activity activity;
    private DataHandler dataHandler;
    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String receivedData;
                    try {
                        receivedData = data.getString("data");
                        dataHandler.onDataReceived(new Data(receivedData));
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(IP_ADDRESS);
        } catch (URISyntaxException e) {
        }
    }

    public CommunicationHandler(Activity activity, DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.activity = activity;
        mSocket.connect();
        mSocket.on("imageInfo", onNewMessage);
        mSocket.emit("message", "connected");
    }

    public static CommunicationHandler getInstance(Activity activity, DataHandler dataHandler) {
        if (instance == null)
            instance = new CommunicationHandler(activity, dataHandler);
        return instance;
    }

    public void sendImage(Bitmap bitmap) {
        Log.d(TAG, "sendImage() called with: bitmap = [" + bitmap + "]");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        String encImage = Base64.encodeToString(data, Base64.DEFAULT);

        Log.i(TAG, "sendImage: image = " + encImage);
        Log.i(TAG, "sendImage: is Socket connected = " + mSocket.connected());
        char[] charArray = encImage.toCharArray();
        mSocket.emit("message", encImage.length());
        mSocket.emit("image", encImage);
        Log.i(TAG, "sendImage: sent");
    }
}
