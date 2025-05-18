package com.example.farmerschoice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.Arrays;

public class CropSuggestionActivity extends AppCompatActivity {

    private TFLiteModelHelper tfliteHelper;
    private TextView cropSuggestionText;
    private EditText etN, etP, etK, etTemp, etHumidity, etPh, etRainfall;
    private Button btnSuggest;

    private final String[] cropNames = {"rice", "maize", "chickpea", "kidneybeans", "pigeonpeas", "mothbeans", "mungbean",
            "blackgram", "lentil", "pomegranate", "banana", "mango", "grapes", "watermelon",
            "muskmelon", "apple", "orange", "papaya", "coconut", "cotton", "jute", "coffee"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // UI references
        etN = findViewById(R.id.etN);
        etP = findViewById(R.id.etP);
        etK = findViewById(R.id.etK);
        etTemp = findViewById(R.id.etTemp);
        etHumidity = findViewById(R.id.etHumidity);
        etPh = findViewById(R.id.etPh);
        etRainfall = findViewById(R.id.etRainfall);
        btnSuggest = findViewById(R.id.btnSuggest);
        cropSuggestionText = findViewById(R.id.cropSuggestionText);

        try {
            Log.d("CropSuggestionActivity", "Initializing TFLite model...");
            tfliteHelper = new TFLiteModelHelper(this, "app.tflite");
            Log.d("CropSuggestionActivity", "Model loaded successfully.");
        } catch (IOException e) {
            Log.e("CropSuggestionActivity", "Error loading model", e);
            cropSuggestionText.setText("Error loading model: " + e.getMessage());
            return;
        }

        btnSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    float[] inputData = getUserInputData();

                    if (inputData == null) {
                        cropSuggestionText.setText("Please fill all fields correctly.");
                        return;
                    }

                    Log.d("CropSuggestionActivity", "Running model with input: " + Arrays.toString(inputData));
                    float[] result = tfliteHelper.runModel(inputData);

                    Log.d("CropSuggestionActivity", "Model output: " + Arrays.toString(result));
                    String suggestedCrop = getTopCrop(result);
                    cropSuggestionText.setText("Suggested Crop:\n" + suggestedCrop);

                } catch (Exception e) {
                    Log.e("CropSuggestionActivity", "Error during inference", e);
                    cropSuggestionText.setText("Error: " + e.getMessage());
                }
            }
        });
    }

    private float[] getUserInputData() {
        try {
            float n = Float.parseFloat(etN.getText().toString().trim());
            float p = Float.parseFloat(etP.getText().toString().trim());
            float k = Float.parseFloat(etK.getText().toString().trim());
            float temp = Float.parseFloat(etTemp.getText().toString().trim());
            float humidity = Float.parseFloat(etHumidity.getText().toString().trim());
            float ph = Float.parseFloat(etPh.getText().toString().trim());
            float rainfall = Float.parseFloat(etRainfall.getText().toString().trim());

            return new float[]{n, p, k, temp, humidity, ph, rainfall};
        } catch (Exception e) {
            Log.e("CropSuggestionActivity", "Invalid input", e);
            return null;
        }
    }

    private String getTopCrop(float[] probabilities) {
        int bestIndex = 0;
        float maxProb = probabilities[0];

        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > maxProb) {
                maxProb = probabilities[i];
                bestIndex = i;
            }
        }

        return cropNames[bestIndex] + " (" + String.format("%.2f", maxProb * 100) + "% confidence)";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tfliteHelper != null) {
            tfliteHelper.close();
        }
    }
}
