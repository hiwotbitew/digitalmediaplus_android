package com.hiwot.digitalmediaplus.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hiwot.digitalmediaplus.databinding.DialogStatusReportBinding;

public class Constants {
    public static final String KEY_USERNAME="username";
    public static final String KEY_EMAIL="email";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_FULL_NAME="full_name";
    public static final String KEY_BALANCE="balance";
    public static final String KEY_USER_ID="user_id";
    public static final String KEY_PHONE="phone";
    public static final String KEY_AVATAR="avatar";

    public static void showDialog(Context context,boolean isSuccess, String message){
        new Handler(Looper.getMainLooper()).post(()->{
            DialogStatusReportBinding b=DialogStatusReportBinding.inflate(LayoutInflater.from(context));
            AlertDialog dialog=new AlertDialog.Builder(context)
                    .setView(b.getRoot())
                    .setCancelable(false)
                    .create();
            if(isSuccess){
                b.animSuccess.setVisibility(View.VISIBLE);
                b.animSuccess.playAnimation();
            }else{
                b.animError.setVisibility(View.VISIBLE);
                b.animError.playAnimation();
            }
            if(message!=null){
                b.tvMessage.setText(message);
                b.tvMessage.setVisibility(View.VISIBLE);
            }else{
                b.tvMessage.setVisibility(View.GONE);
            }
            new Handler(Looper.getMainLooper()).postDelayed(dialog::cancel,4000);
            dialog.show();

        });
    }
    public static void hideKeypad(Context context,EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            }
        });

    }
}
