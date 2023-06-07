import 'dart:async';
import 'dart:developer';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'constants.dart';

/// ImageStreamWidget handles and displays streaming of image bytes
class ImageStreamWidget extends StatefulWidget {
  /// Initialize ImageStreamWidget with [key].
  const ImageStreamWidget({Key? key}) : super(key: key);

  @override
  ImageStreamWidgetState createState() => ImageStreamWidgetState();
}

class ImageStreamWidgetState extends State<ImageStreamWidget> {
  final imageBytes = <int>[];
  int? imageSize;
  StreamSubscription<dynamic>? imageSubscription;
  bool streamComplete = false;

  final eventChannel = const EventChannel('platform_channel_events/image');

  @override
  Widget build(BuildContext context) {
    final imageStreamUnbegun = imageSize == null && !streamComplete;
    final imageStreamInProgress = imageSize != null && !streamComplete;
    final imageStreamEnded = imageSize != null && streamComplete;

    return Container(
      padding: const EdgeInsets.all(30),
      width: double.infinity,
      alignment: Alignment.center,
      child: Builder(builder: (c) {
        if (imageStreamUnbegun) {
          return ElevatedButton(
            onPressed: startImageStream,
            child: const Text('Stream Image'),
          );
        }

        if (imageStreamInProgress) {
          double progress;
          if (imageBytes.isEmpty || imageSize == 0) {
            progress = 0.0;
          } else {
            progress = (imageBytes.length / imageSize!);
          }

          return SizedBox(
            width: 100,
            height: 100,
            child: CircularProgressIndicator(
              value: progress,
              strokeWidth: 3.0,
            ),
          );
        }

        if (imageStreamEnded) {
          return Image.memory(Uint8List.fromList(imageBytes));
        }

        return const SizedBox();
      }),
    );
  }

  @override
  void dispose() {
    imageSubscription?.cancel();
    super.dispose();
  }

  void startImageStream() {
    imageSubscription = eventChannel.receiveBroadcastStream([
      {'quality': 0.9, 'chunkSize': 100}
    ]).listen(onReceiveImageByte);
  }

  void onReceiveImageByte(dynamic event) {
    // Check if this is the first event. The first event is the file size
    if (imageSize == null && event is int && event != Constants.eof) {
      setState(() => imageSize = event);
      return;
    }

    // Check if this is the end-of-file event.
    // End-of-file event denotes the end of the stream
    if (event == Constants.eof) {
      imageSubscription?.cancel();
      setState(() => streamComplete = true);
      return;
    }

    // Receive and concatenate the image bytes
    final byteArray = (event as List<dynamic>).cast<int>();
    setState(() => imageBytes.addAll(byteArray));
  }
}
