package com.example.invest_app;


public class PlanItem {
    private String plan;
    private boolean completed;

    public PlanItem(String plan, boolean completed) {
        this.plan = plan;
        this.completed = completed;
    }

    public String getPlan() {
        return plan;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
