package com.example.flutter_event_channel;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import androidx.annotation.NonNull;

import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {

    static final String NETWORK_EVENT_CHANNEL = "platform_channel_events/connectivity";
    static final String BATTERY_CHANNEL = "platform_channel_events/battery";
    static final String IMAGE_EVENT_CHANNEL = "platform_channel_events/image";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        BinaryMessenger messenger = flutterEngine.getDartExecutor().getBinaryMessenger();
        EventChannel network_event = new EventChannel(messenger, NETWORK_EVENT_CHANNEL);
        network_event.setStreamHandler(new NetworkStreamHandler(this));

        MethodChannel battery_channel = new MethodChannel(messenger, BATTERY_CHANNEL);
        battery_channel.setMethodCallHandler((call, result) -> {
            if(call.method.equals("getBatteryLevel")){
                Map<String, String> arguments = call.arguments();
                assert arguments != null;
                String name = arguments.get("name");

                int batteryLevel = getBatteryLevel();
                result.success(name + " says: " + batteryLevel + "%");
            }else{
                result.notImplemented();
            }
        });

        EventChannel image_event = new EventChannel(messenger, IMAGE_EVENT_CHANNEL);
        image_event.setStreamHandler(new ImageStreamHandler(this));
    }

    private int getBatteryLevel() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, filter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100;
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return level / scale;
    }
}
