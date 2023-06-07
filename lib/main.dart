import 'package:flutter/material.dart';

import 'get_battery_level.dart';
import 'get_battery_status.dart';
import 'image_stream_widget.dart';
import 'network_stream_widget.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: const _MyHomePage(title: 'Platform Channels Demo'),
    );
  }
}

class _MyHomePage extends StatelessWidget {
  final String title;

  const _MyHomePage({Key? key, required this.title}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: Text(title),
      ),
      body: const Column(
        children: [
          NetworkStreamWidget(),
          GetBatteryLevel(),
          GetBatteryStatus(),
          Expanded(child: ImageStreamWidget()),
        ],
      ),
    );
  }
}
