package com.example.invest_app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.invest_app.managers.SharedPreferencesManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Wallet extends AppCompatActivity {

    private TextView walletBalance;
    private LineChart coinChart;
    private ArrayList<Entry> coinEntries;
    private LineDataSet dataSet;
    private LineData lineData;
    private SharedPreferencesManager sharedPreferencesManager;
    private DBHelper dbHelper;

    private float lastDisplayedCoins = -1;
    private int lastDisplayedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfel);

        walletBalance = findViewById(R.id.walletBalance);
        coinChart = findViewById(R.id.coinChart);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        dbHelper = new DBHelper(this);
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ArrayList<Long> coinHistory = getCoinHistoryForUserFromDatabase(userUid);
        coinEntries = convertCoinHistoryToEntries(coinHistory);
        dataSet = new LineDataSet(coinEntries, "Historia Monet");
        lineData = new LineData(dataSet);

        coinChart.setData(lineData);
        coinChart.getDescription().setText("Historia ilości monet");
        coinChart.invalidate();

        lastDisplayedIndex = coinEntries.size() - 1;
        if (!coinEntries.isEmpty()) {
            lastDisplayedCoins = coinEntries.get(lastDisplayedIndex).getY();
        }

        listenForCoinBalanceChanges();
    }

    private void updateWalletBalance(float saldo) {
        walletBalance.setText("Stan portfela: " + saldo + " monet");
    }

    private void listenForCoinBalanceChanges() {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(userUid);

        userReference.child("coins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long coins = dataSnapshot.getValue(Long.class);
                    updateWalletBalance(coins);
                    updateCoinChartEntry(coins);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private ArrayList<Long> getCoinHistoryForUserFromDatabase(String userUid) {
        ArrayList<Long> coinHistory = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String[] projection = {DBHelper.COLUMN_COINS};
        String selection = DBHelper.COLUMN_USER_UID + " = ?";
        String[] selectionArgs = {userUid};
        Cursor cursor = database.query(DBHelper.TABLE_COIN_HISTORY, projection,
                selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            long coins = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COINS));
            coinHistory.add(coins);
        }
        cursor.close();
        dbHelper.close();

        return coinHistory;
    }

    private void updateCoinChartEntry(float coins) {
        if (lastDisplayedCoins != coins) {
            if (lastDisplayedIndex < 0 || lastDisplayedCoins != coins) {
                coinEntries.clear();
                Entry newEntry = new Entry(lastDisplayedIndex, coins);
                coinEntries.add(newEntry);
                dataSet.addEntry(newEntry);
                lineData.notifyDataChanged();
                coinChart.notifyDataSetChanged();
                coinChart.invalidate();
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                saveCoinToDatabase(userUid, coins);
                lastDisplayedCoins = coins;
                lastDisplayedIndex = coinEntries.size() - 1;
            }
        }
    }


    private ArrayList<Entry> convertCoinHistoryToEntries(ArrayList<Long> coinHistory) {
        ArrayList<Entry> entries = new ArrayList<>();

        long lastCoins = -1;
        int entryIndex = 0;

        for (int i = 0; i < coinHistory.size(); i++) {
            long coins = coinHistory.get(i);
            if (coins != lastCoins) {
                entries.add(new Entry(entryIndex, coins));
                lastCoins = coins;
                entryIndex++;
            }
        }

        return entries;
    }

    private void saveCoinToDatabase(String userUid, float coins) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_USER_UID, userUid);
        values.put(DBHelper.COLUMN_COINS, coins);
        values.put(DBHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());

        database.insert(DBHelper.TABLE_COIN_HISTORY, null, values);
        dbHelper.close();
    }
}