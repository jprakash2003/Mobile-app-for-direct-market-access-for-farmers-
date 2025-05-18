package com.example.farmerschoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class InputActivity extends AppCompatActivity {

    EditText etN, etP, etK, etTemp, etHumidity, etPh, etRainfall;
    Button btnSuggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        etN = findViewById(R.id.etN);
        etP = findViewById(R.id.etP);
        etK = findViewById(R.id.etK);
        etTemp = findViewById(R.id.etTemp);
        etHumidity = findViewById(R.id.etHumidity);
        etPh = findViewById(R.id.etPh);
        etRainfall = findViewById(R.id.etRainfall);
        btnSuggest = findViewById(R.id.btnSuggest);

        btnSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputActivity.this, CropSuggestionActivity.class);
                intent.putExtra("N", Float.parseFloat(etN.getText().toString()));
                intent.putExtra("P", Float.parseFloat(etP.getText().toString()));
                intent.putExtra("K", Float.parseFloat(etK.getText().toString()));
                intent.putExtra("Temp", Float.parseFloat(etTemp.getText().toString()));
                intent.putExtra("Humidity", Float.parseFloat(etHumidity.getText().toString()));
                intent.putExtra("Ph", Float.parseFloat(etPh.getText().toString()));
                intent.putExtra("Rainfall", Float.parseFloat(etRainfall.getText().toString()));
                startActivity(intent);
            }
        });
    }
}
