package com.example.vlad.mytestlink;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    Button btnSend;
    TextView txtAdress, txtPort, txtResult;
    final String LOG_TAG = "myLogs";
    Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------
        btnSend = findViewById(R.id.btnSend);
        txtAdress = findViewById(R.id.txtAdress);
        txtPort = findViewById(R.id.txtPort);
        txtResult = findViewById(R.id.txtResult);

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // обновляем TextView
                txtResult.setText("time: " + msg.what);
                if (msg.what == 9) btnSend.setEnabled(true);
            };
        };

        final Runnable check = new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    // долгий процесс
                    h.sendEmptyMessage(i);
                    // пишем лог
                    Log.d(LOG_TAG, "i = " + i);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnSend:
                        //editText.setText(getString(R.string.Pressed_1));
                        //start task
                        Log.d(LOG_TAG, "start");
                        btnSend.setEnabled(false);
                        Thread check_thead = new Thread(check);
                        check_thead.start();
                        break;

                }
            }
        };

        btnSend.setOnClickListener(onClickListener);


    }
}
