import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class GetBatteryLevel extends StatefulWidget {
  const GetBatteryLevel({super.key});

  @override
  State<GetBatteryLevel> createState() => _GetBatteryLevelState();
}

class _GetBatteryLevelState extends State<GetBatteryLevel> {
  static const batteryChannel =
      MethodChannel("platform_channel_events/battery");

  String batteryLevel = "Tap button to fetch battery level";

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(height: 16),
        Text(
          batteryLevel,
          textAlign: TextAlign.center,
          style: TextStyle(
            color: Colors.deepPurple,
            fontSize: 30,
          ),
        ),
        SizedBox(height: 16),
        ElevatedButton(
          onPressed: getBatteryLevel,
          child: const Text("Get Battery Level"),
        ),
      ],
    );
  }

  Future<void> getBatteryLevel() async {
    Map<String, String> arguments = {"name": "Android"};
    String newBatteryLevel =
        await batteryChannel.invokeMethod("getBatteryLevel", arguments);

    setState(() => batteryLevel = newBatteryLevel);
  }
}
