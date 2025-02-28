package com.example.invest_app.managers;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.invest_app.model.BlockModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

class BlockChainDataManager {
    private static final String BLOCKCHAIN_PREFS = "blockchain_prefs";
    private static final String BLOCKS_KEY = "blocks_key";
    private final SharedPreferences sharedPreferences;

    public BlockChainDataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(BLOCKCHAIN_PREFS, Context.MODE_PRIVATE);
    }

    public void saveBlockChain(List<BlockModel> blocks) {
        Gson gson = new Gson();
        String json = gson.toJson(blocks);
        sharedPreferences.edit().putString(BLOCKS_KEY, json).apply();
    }
    public List<BlockModel> loadBlockChain() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(BLOCKS_KEY, null);
        Type type = new TypeToken<List<BlockModel>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
