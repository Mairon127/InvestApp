package com.example.invest_app;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Exchange extends AppCompatActivity {
    private EditText amountInput;
    private Spinner sourceCurrencySpinner;
    private Spinner targetCurrencySpinner;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        amountInput = findViewById(R.id.amountInput);
        sourceCurrencySpinner = findViewById(R.id.sourceCurrencySpinner);
        targetCurrencySpinner = findViewById(R.id.targetCurrencySpinner);
        Button convertButton = findViewById(R.id.convertButton);
        resultText = findViewById(R.id.resultText);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceCurrencySpinner.setAdapter(adapter);
        targetCurrencySpinner.setAdapter(adapter);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertCurrency();
            }
        });
    }

    private void convertCurrency() {
        String sourceCurrency = sourceCurrencySpinner.getSelectedItem().toString();
        String targetCurrency = targetCurrencySpinner.getSelectedItem().toString();
        String amountText = amountInput.getText().toString();

        if (amountText.isEmpty()) {
            resultText.setText("Wprowadź kwotę do konwersji");
            return;
        }

        double amount = Double.parseDouble(amountText);

        String apiUrl = "URL API";
        new CurrencyConversionTask().execute(apiUrl, sourceCurrency, targetCurrency, String.valueOf(amount));
    }

    @SuppressLint("StaticFieldLeak")
    private class CurrencyConversionTask extends AsyncTask<String, Void, Double> {
        @Override
        protected Double doInBackground(String... params) {
            try {
                String apiUrl = params[0];
                String sourceCurrency = params[1];
                String targetCurrency = params[2];
                double amount = Double.parseDouble(params[3]);

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                String json = buffer.toString();
                JSONObject jsonObject = new JSONObject(json);
                JSONObject rates = jsonObject.getJSONObject("rates");
                double sourceRate = rates.getDouble(sourceCurrency);
                double targetRate = rates.getDouble(targetCurrency);

                double convertedAmount = (amount / sourceRate) * targetRate;

                return convertedAmount;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Double result) {
            if (result != null) {
                resultText.setText(String.format("%.2f", result));
            } else {
                resultText.setText("Źle przekonwertowano");
            }
        }
    }
}
