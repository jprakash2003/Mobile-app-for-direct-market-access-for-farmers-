package com.example.farmerschoice;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class TFLiteModelHelper {
    private Interpreter tflite;

    // Constructor to load TensorFlow Lite model
    public TFLiteModelHelper(Context context, String modelFileName) throws IOException {
        Log.d("TFLiteModelHelper", "Loading model: " + modelFileName);
        tflite = new Interpreter(loadModelFile(context, modelFileName));
        Log.d("TFLiteModelHelper", "Model loaded successfully.");
    }

    // Runs model inference
    public float[] runModel(float[] inputData) {
        if (tflite == null) {
            throw new IllegalStateException("Model not loaded");
        }

        // ✅ Input shape: [1, 7]
        float[][] inputArray = new float[1][7];
        System.arraycopy(inputData, 0, inputArray[0], 0, inputData.length);

        // ✅ Output shape: [1, 3] for Rice, Wheat, Sugarcane
        float[][] outputArray = new float[1][22];

        try {
            Log.d("TFLiteModelHelper", "Running inference...");
            tflite.run(inputArray, outputArray);
            Log.d("TFLiteModelHelper", "Inference completed.");
        } catch (Exception e) {
            Log.e("TFLiteModelHelper", "Error during inference", e);
            throw e;
        }

        // Log raw output
        Log.d("TFLiteModelHelper", "Raw Model Output: " + Arrays.toString(outputArray[0]));

        // Apply softmax to normalize probabilities
        float[] probabilities = softmax(outputArray[0]);
        Log.d("TFLiteModelHelper", "Softmax Output: " + Arrays.toString(probabilities));

        return probabilities;
    }

    // Apply softmax normalization to ensure probabilities sum to 1
    private float[] softmax(float[] values) {
        float sum = 0;
        float[] expValues = new float[values.length];

        for (int i = 0; i < values.length; i++) {
            expValues[i] = (float) Math.exp(values[i]);
            sum += expValues[i];
        }

        for (int i = 0; i < values.length; i++) {
            expValues[i] /= sum;
        }

        return expValues;
    }

    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
            Log.d("TFLiteModelHelper", "Model closed.");
        }
    }

    // Loads the TensorFlow Lite model from assets
    private MappedByteBuffer loadModelFile(Context context, String modelFileName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelFileName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
