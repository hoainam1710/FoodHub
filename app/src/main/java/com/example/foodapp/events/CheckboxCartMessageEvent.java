package com.example.foodapp.events;

public class CheckboxCartMessageEvent {
    private boolean checked;

    public CheckboxCartMessageEvent() {
    }

    public CheckboxCartMessageEvent(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
