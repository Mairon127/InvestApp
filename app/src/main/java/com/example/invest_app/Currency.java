package com.example.invest_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.invest_app.adapters.CurrencyAdapter;
import com.example.invest_app.model.CurrencyModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Currency extends AppCompatActivity {
    private ArrayList<CurrencyModel> currencyModalArrayList;
    private CurrencyAdapter currencyRVAdapter;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        EditText searchEdt = findViewById(R.id.idEdtCurrency);
        loadingPB = findViewById(R.id.idPBLoading);
        RecyclerView currencyRV = findViewById(R.id.idRVcurrency);
        currencyModalArrayList = new ArrayList<>();
        currencyRVAdapter = new CurrencyAdapter(currencyModalArrayList, this);
        currencyRV.setLayoutManager(new LinearLayoutManager(this));
        currencyRV.setAdapter(currencyRVAdapter);
        getData();
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String filter) {
        ArrayList<CurrencyModel> filteredlist = new ArrayList<>();
        for (CurrencyModel item : currencyModalArrayList) {
            if (item.getName().toLowerCase().contains(filter.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No currency found..", Toast.LENGTH_SHORT).show();
        } else {
            currencyRVAdapter.filterList(filteredlist);
        }
    }

    private void getData() {
        String url = "API COINMARKET";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String symbol = dataObj.getString("symbol");
                        String name = dataObj.getString("name");
                        JSONObject quote = dataObj.getJSONObject("quote");
                        JSONObject USD = quote.getJSONObject("USD");
                        double price = USD.getDouble("price");
                        currencyModalArrayList.add(new CurrencyModel(name, symbol, price));
                    }
                    currencyRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Currency.this, "Coś poszło nie tak. Spróbuj ponownie później", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Currency.this, "Coś poszło nie tak. Spróbuj ponownie później", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("API KEY", "API KEY");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }
}


