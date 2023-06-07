package com.example.flutter_event_channel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import io.flutter.plugin.common.EventChannel;

public class ChargingStreamHandler implements EventChannel.StreamHandler {
    private final Context context;

    ChargingStreamHandler(Context context) {
        this.context = context;
    }

    private BroadcastReceiver receiver;

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        receiver = initReceiver(events);
        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onCancel(Object arguments) {
        context.unregisterReceiver(receiver);
        receiver = null;
    }

    private BroadcastReceiver initReceiver(EventChannel.EventSink events){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                switch (status){
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        events.success("Charging");
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        events.success("Full");
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        events.success("Not Charging");
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        events.success("Discharging");
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
