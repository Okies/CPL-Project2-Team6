package com.bong.smartcart;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {

    EditText text_id;
    EditText text_password;
    EditText text_check;
    String id;
    String pw;
    String check;
    int isSuccess = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void onClickSuccess (View v) {
        text_id = (EditText)findViewById(R.id.text_signup_id);
        text_password = (EditText)findViewById(R.id.text_signup_password);
        text_check = (EditText)findViewById(R.id.text_signup_check);
        id = text_id.getText().toString();
        pw = text_password.getText().toString();
        check = text_check.getText().toString();
        Log.v("입력값", id + ", " + pw + ", " + check);
        if(pw.equals(check)) {
            sendData("http://27.35.110.82:3000/join_service");
            //sendData("http://222.104.202.90:3000/join_service");
        }
        else {
            Toast.makeText(getApplicationContext(), "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
        }
        //Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        //startActivity(i);
    }

    public void sendData(String url)
    {
        isSuccess = 0;

        class sendDataJSON extends AsyncTask<String, Void, String>
        {
            @Override // URL 연결이 구현될 부분
            protected String doInBackground(String... params)
            {
                String uri = params[0];
                String response = null;
                HttpURLConnection conn = null;
                try
                {
                    URL url = new URL(uri);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    bw.write("id="+ id + "&pw=" + pw);
                    bw.flush();
                    bw.close();
                    os.close();

                    conn.connect();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); //캐릭터셋 설정

                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if(sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append(line);
                    }
                    String req = sb.toString();
                    if(req.equals("회원가입 완료"))
                        isSuccess = 1;
                    System.out.println("response:" + req);
                    return req;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(conn != null)
                        conn.disconnect();
                }
                return response;
            }

            @Override // UI 업데이트가 구현될 부분
            protected void onPostExecute(String result)
            {
                if(isSuccess == 1) {
                    finish();
                }
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        }
        sendDataJSON g = new sendDataJSON();
        g.execute(url);
    }
}
