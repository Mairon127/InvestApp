package com.example.invest_app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.invest_app.DBHelper;
import com.example.invest_app.PlanItem;
import com.example.invest_app.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PlanAdapter extends ArrayAdapter<PlanItem> {
    private Context context;
    private int resource;

    public PlanAdapter(Context context, int resource, ArrayList<PlanItem> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        PlanItem planItem = getItem(position);

        if (planItem != null) {
            TextView planTextView = convertView.findViewById(R.id.planTextView);
            CheckBox completedCheckBox = convertView.findViewById(R.id.completedCheckBox);

            if (planTextView != null) {
                planTextView.setText(planItem.getPlan());
                if (planItem.isCompleted()) {
                    planTextView.setTextColor(Color.GREEN);
                } else {
                    planTextView.setTextColor(Color.BLACK);
                }
            }

            if (completedCheckBox != null) {
                completedCheckBox.setChecked(planItem.isCompleted());
                completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        planItem.setCompleted(true);
                        planTextView.setTextColor(Color.GREEN);
                        completedCheckBox.setEnabled(false);

                        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DBHelper dbHelper = new DBHelper(context);
                        dbHelper.updatePlanCompletionInDatabase(userUid, planItem.getPlan(), true);
                        dbHelper.close();
                    }
                });
            }

        }

        return convertView;
    }

}
