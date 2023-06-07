import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

/// GetBatterStatus streams the charging status of the device.
class GetBatteryStatus extends StatefulWidget {
  const GetBatteryStatus({super.key});

  @override
  State<GetBatteryStatus> createState() => _GetBatteryStatusState();
}

class _GetBatteryStatusState extends State<GetBatteryStatus> {
  late StreamSubscription _streamSubscription;

  static const chargingChannel =
      EventChannel("platform_channel_events/charging");

  String batteryStatus = "Listening...";

  @override
  void initState() {
    super.initState();

    onStreamBattery();
  }

  void onStreamBattery() {
    _streamSubscription = chargingChannel.receiveBroadcastStream().listen(
      (event) {
        setState(() => batteryStatus = "$event");
      },
    );
  }

  @override
  void dispose() {
    _streamSubscription.cancel();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const SizedBox(height: 16),
        RichText(
          text: TextSpan(
            text: "Battery Status: ",
            style: const TextStyle(
              fontSize: 16,
              color: Colors.blueAccent,
            ),
            children: [
              TextSpan(
                text: batteryStatus,
                style: const TextStyle(
                  fontSize: 16,
                  color: Colors.blueAccent,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
