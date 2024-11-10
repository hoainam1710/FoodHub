package com.example.foodapp.events;

public class CheckboxPaymentMessageEvent {
    private boolean checked;

    public CheckboxPaymentMessageEvent() {
    }

    public CheckboxPaymentMessageEvent(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
