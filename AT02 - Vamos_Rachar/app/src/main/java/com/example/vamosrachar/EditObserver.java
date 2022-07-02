package com.example.vamosrachar;

import android.text.Editable;
import android.text.TextWatcher;

public class EditObserver implements TextWatcher {

    MainActivity mainActivity;
    public EditObserver(MainActivity pMainActivity) {
        this.mainActivity = pMainActivity;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mainActivity.calculate();
    }
}
