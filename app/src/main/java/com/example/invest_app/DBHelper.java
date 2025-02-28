package com.example.invest_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "portfolio_history.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_COIN_HISTORY = "coin_history";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_UID = "user_uid";
    public static final String COLUMN_COINS = "coins";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String TABLE_PLANS = "plans";
    public static final String COLUMN_PLAN_ID = "_id";
    public static final String COLUMN_PLAN_USER_UID = "user_uid";
    public static final String COLUMN_PLAN = "plan_text";
    public static final String COLUMN_COMPLETED = "completed";

    private static final String DATABASE_CREATE = "create table " +
            TABLE_COIN_HISTORY + "(" + COLUMN_ID +
            " integer primary key autoincrement, " +
            COLUMN_USER_UID + " text not null, " +
            COLUMN_COINS + " integer not null, " +
            COLUMN_TIMESTAMP + " integer not null);";
    private static final String CREATE_PLANS_TABLE = "CREATE TABLE " +
            TABLE_PLANS + "(" +
            COLUMN_PLAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PLAN_USER_UID + " TEXT NOT NULL, " +
            COLUMN_PLAN + " TEXT NOT NULL, " +
            COLUMN_COMPLETED + " INTEGER NOT NULL);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(CREATE_PLANS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void updatePlanCompletionInDatabase(String userUid, String plan, boolean completed) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_COMPLETED, completed ? 1 : 0);

        String selection = DBHelper.COLUMN_PLAN_USER_UID + " = ? AND " + DBHelper.COLUMN_PLAN + " = ?";
        String[] selectionArgs = {userUid, plan};

        database.update(DBHelper.TABLE_PLANS, values, selection, selectionArgs);

        close();
    }
}