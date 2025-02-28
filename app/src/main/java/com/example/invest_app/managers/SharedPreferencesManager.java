package com.example.invest_app.managers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesManager {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String PREFERENCES_DATA = "example.blockchain";
    private static final String ENCRYPTION_STATUS = "encryption_status";
    private static final String PROOF_OF_WORK = "proof_of_work";
    public static final int DEFAULT_PROOF_OF_WORK = 2;
    private static final String COIN_HISTORY = "coin_history";

    public SharedPreferencesManager(Context context){
        sharedPreferences = context.getSharedPreferences(PREFERENCES_DATA, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public int getPowValue() {
        return sharedPreferences.getInt(PROOF_OF_WORK, DEFAULT_PROOF_OF_WORK);
    }

    public void saveCoinHistory(ArrayList<Long> coinHistory) {
        Set<String> coinHistorySet = new HashSet<>();
        for (Long coin : coinHistory) {
            coinHistorySet.add(String.valueOf(coin));
        }
        editor.putStringSet(COIN_HISTORY, coinHistorySet);
        editor.apply();
    }

    public ArrayList<Long> getCoinHistory() {
        ArrayList<Long> coinHistory = new ArrayList<>();
        Set<String> coinHistorySet = sharedPreferences.getStringSet(COIN_HISTORY, null);
        if (coinHistorySet != null) {
            for (String coin : coinHistorySet) {
                coinHistory.add(Long.parseLong(coin));
            }
        }
        return coinHistory;
    }

}