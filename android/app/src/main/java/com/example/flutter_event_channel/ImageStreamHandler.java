package com.example.flutter_event_channel;

import android.app.Activity;

import io.flutter.plugin.common.EventChannel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ImageStreamHandler implements EventChannel.StreamHandler {
    private Activity activity;
    private final Handler mainHandler;


    ImageStreamHandler(Activity activity) {
        this.activity = activity;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    private EventChannel.EventSink eventSink;

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        eventSink = events;
//        List<Map<String, Double>> arg = (List<Map<String, Double>>) arguments;
        double quality = 0.9;
        int chunkSize = 100;
        dispatchImageEvents(quality, chunkSize);
    }

    @Override
    public void onCancel(Object arguments) {
        eventSink = null;
        activity = null;
    }

    private void dispatchImageEvents(double quality, int chunkSize) {
        if (activity == null) return;

        Runnable extractImageRunnable = new Runnable() {
            @Override
            public void run() {
                // Decode the drawable
                Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.dart);
                // Compress the drawable using the quality passed from Flutter
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, (int) (quality * 100), stream);
                // Convert the compressed image stream to byte array
                byte[] byteArray = stream.toByteArray();
                    // Dispatch the first event (which is the size of the array/image)
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            eventSink.success(byteArray.length);
                        }
                    });

                    // Split the array into chunks using the chunkSize passed from Flutter
                    int parts = byteArray.length / chunkSize;
                    List<byte[]> chunks = splitArrayIntoChunks(byteArray, parts);

                    // Loop through the chunks and dispatch each chuck to Flutter
                    for (byte[] chunk : chunks) {
                        // Mimic buffering with a 50 mills delay
//                        delay(50);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                eventSink.success(chunk);
                            }
                        });
                    }

                    //sending end of file
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            eventSink.success(Constants.eof);
                        }
                    });

                }
            };

        // Start a new thread to execute the extractImageRunnable
        new Thread(extractImageRunnable).start();

    }

    // Helper method to split byte array into chunks
    private List<byte[]> splitArrayIntoChunks(byte[] array, int chunkSize) {
        List<byte[]> chunks = new ArrayList<>();
        int index = 0;
        while (index < array.length) {
            int endIndex = Math.min(index + chunkSize, array.length);
            byte[] chunk = Arrays.copyOfRange(array, index, endIndex);
            chunks.add(chunk);
            index += chunkSize;
        }
        return chunks;
    }

}
