package com.example.invest_app.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class CoinManager {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String LAST_COIN_ADDITION_TIME = "LastCoinAdditionTime";
    private static final int COINS_TO_ADD_AFTER_24_HOURS = 5000;


    public static void addCoinsIfTimeElapsed(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            String userUid = firebaseAuth.getCurrentUser().getUid();
            long lastCoinAdditionTime = sharedPreferences.getLong(LAST_COIN_ADDITION_TIME + userUid, 0);

            Calendar currentTime = Calendar.getInstance();
            long currentTimeMillis = currentTime.getTimeInMillis();
            if (currentTimeMillis - lastCoinAdditionTime >= 24 * 60 * 60 * 1000) {
                getCurrentCoinsFromFirebase(userUid, new CoinsCallback() {
                    @Override
                    public void onCallback(int currentCoinsFirebase) {
                        int updatedCoinsFirebase = currentCoinsFirebase + COINS_TO_ADD_AFTER_24_HOURS;
                        updateCoinsOnFirebase(userUid, updatedCoinsFirebase);
                        setCoins(context, userUid, updatedCoinsFirebase);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong(LAST_COIN_ADDITION_TIME + userUid, currentTimeMillis);
                        editor.apply();
                    }
                });
            }
        }
    }
    public static void getCurrentCoinsFromFirebase(String userUid, final CoinsCallback callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userUid)
                .child("coins");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int currentCoins = snapshot.getValue(Integer.class);
                    callback.onCallback(currentCoins);
                } else {
                    callback.onCallback(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(0);
            }
        });
    }


    private static void updateCoinsOnFirebase(String userUid, int coins) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userUid)
                .child("coins");
        reference.setValue(coins);
    }

    private static void setCoins(Context context, String userUid, int coins) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("coins_" + userUid, coins);
        editor.apply();
    }
    public static void subtractCoins(Context context, int amount) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            String userUid = firebaseAuth.getCurrentUser().getUid();

            DatabaseReference coinsRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(userUid)
                    .child("coins");

            coinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int currentCoins = dataSnapshot.getValue(Integer.class);
                        int newCoins = currentCoins - amount;
                        if (newCoins >= 0) {
                            coinsRef.setValue(newCoins);
                            setCoins(context, userUid, newCoins); // Poprawione wywołanie setCoins
                        } else {
                            Toast.makeText(context, "Brak wystarczających środków na koncie.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public interface CoinsCallback {
        void onCallback(int currentCoinsFirebase);
    }
}