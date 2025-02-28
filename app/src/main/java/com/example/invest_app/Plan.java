package com.example.invest_app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.invest_app.adapters.PlanAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Plan extends AppCompatActivity {
    private EditText planEditText;
    private Button saveButton;
    private ListView plansListView;

    private ArrayList<PlanItem> plans;
    private ArrayAdapter<PlanItem> adapter;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_plan);

        planEditText = findViewById(R.id.planEditText);
        saveButton = findViewById(R.id.saveButton);
        plansListView = findViewById(R.id.plansListView);

        plans = new ArrayList<>();
        adapter = new PlanAdapter(this, R.layout.item_plan, plans);
        plansListView.setAdapter(adapter);

        dbHelper = new DBHelper(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPlan = planEditText.getText().toString();
                if (!newPlan.isEmpty()) {
                    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    savePlanToDatabase(userUid, newPlan, false);
                    plans.clear();
                    plans.addAll(getPlansFromDatabase(userUid));
                    adapter.notifyDataSetChanged();
                    planEditText.setText("");
                }
            }
        });

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        plans.addAll(getPlansFromDatabase(userUid));
        adapter.notifyDataSetChanged();
    }

    private void savePlanToDatabase(String userUid, String plan, boolean completed) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_USER_UID, userUid);
        values.put(DBHelper.COLUMN_PLAN, plan);
        values.put(DBHelper.COLUMN_COMPLETED, completed ? 1 : 0);

        database.insert(DBHelper.TABLE_PLANS, null, values);

        dbHelper.close();
    }

    private ArrayList<PlanItem> getPlansFromDatabase(String userUid) {
        ArrayList<PlanItem> planItems = new ArrayList<>();

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] projection = {
                DBHelper.COLUMN_PLAN,
                DBHelper.COLUMN_COMPLETED
        };

        String selection = DBHelper.COLUMN_USER_UID + " = ?";
        String[] selectionArgs = {userUid};

        Cursor cursor = database.query(
                DBHelper.TABLE_PLANS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String plan = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAN));
            boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMPLETED)) == 1;

            PlanItem planItem = new PlanItem(plan, completed);
            planItems.add(planItem);
        }

        cursor.close();
        dbHelper.close();

        return planItems;
    }
}
