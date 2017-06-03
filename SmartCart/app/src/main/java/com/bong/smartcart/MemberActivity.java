package com.bong.smartcart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MemberActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
    }

    public void onClickMember (View v) {
        Intent i = new Intent(MemberActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void onClickNonmember (View v) {
        LoginActivity.islogin = 0;
        Intent i = new Intent(MemberActivity.this, MainActivity.class);
        startActivity(i);
    }
}
