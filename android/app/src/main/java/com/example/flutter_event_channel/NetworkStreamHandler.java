package com.example.flutter_event_channel;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;

import io.flutter.plugin.common.EventChannel;

class NetworkStreamHandler implements EventChannel.StreamHandler {
    private Activity activity;

    NetworkStreamHandler(Activity activity) {
        this.activity = activity;
    }

    private EventChannel.EventSink eventSink;

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        eventSink = events;
        startListeningNetworkChanges();
    }

    @Override
    public void onCancel(Object arguments) {
        stopListeningNetworkChanges();
        eventSink = null;
        activity = null;
    }

    private final ConnectivityManager.NetworkCallback networkCallback =
            new ConnectivityManager.NetworkCallback() {
                // Override methods of ConnectivityManager.NetworkCallback here
                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    // Notify Flutter that the network is disconnected
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (eventSink != null) {
                                    eventSink.success(Constants.disconnected);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    // Pick the supported network states and notify Flutter of this new state
                    int status;
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        status = Constants.wifi;
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        status = Constants.cellular;
                    } else {
                        status = Constants.unknown;
                    }
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (eventSink != null) {
                                    eventSink.success(status);
                                }
                            }
                        });
                    }
                }
            };

    private void startListeningNetworkChanges() {
         ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            manager.registerNetworkCallback(request, networkCallback);
        }
    }

    private void stopListeningNetworkChanges() {
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            manager.unregisterNetworkCallback(networkCallback);
        }
    }

}
