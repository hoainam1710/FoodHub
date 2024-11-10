package com.example.foodapp.events;

public class CheckboxAddressMessageEvent {
    private boolean checked;

    public CheckboxAddressMessageEvent() {
    }

    public CheckboxAddressMessageEvent(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
