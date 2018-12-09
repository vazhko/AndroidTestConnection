package com.example.vlad.mytestlink;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import android.widget.ProgressBar;
//import android.os.Handler;
import java.net.Socket;
import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity {

    Button btnSend;
    TextView txtAdress, txtPort, txtResult;
    final String LOG_TAG = "myLogs";
    ProgressBar pbCount;
    final int max = 100;
    //Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------
        btnSend = findViewById(R.id.btnSend);
        txtAdress = findViewById(R.id.txtAdress);
        txtPort = findViewById(R.id.txtPort);
        txtResult = findViewById(R.id.txtResult);

        pbCount = findViewById(R.id.pbCount);
        pbCount.setMax(max);
        pbCount.setProgress(0);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnSend:                        //start task
                        MyTask t = new MyTask();
                        t.execute();
                        break;

                }
            }
        };

        btnSend.setOnClickListener(onClickListener);

    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        Socket sk;
        InetSocketAddress ia;
        Exception err;
        //выполняется перед doInBackground, имеет доступ к UI
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(LOG_TAG, getString(R.string.start_str));
            txtResult.setText(R.string.start_str);
            btnSend.setEnabled(false);

            try {
                String strAdress = txtAdress.getText().toString();
                int iPort = Integer.parseInt(txtPort.getText().toString());
                if (strAdress.length() == 0) {
                    throw new IllegalArgumentException(getString(R.string.bad_IP_adress_str));
                }
                if ((iPort == 0) || (iPort > (0xffff - 1))) {
                    throw new IllegalArgumentException(getString(R.string.bad_port_str));
                }
                ia = new InetSocketAddress(strAdress, iPort);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, e.getMessage());
                txtResult.setText(e.getMessage());
                btnSend.setEnabled(true);
                cancel(false);
            }
        }

        //doInBackground – будет выполнен в новом потоке, здесь решаем все свои тяжелые задачи. Т.к. поток не основной - не имеет доступа к UI.
        @Override
        protected Void doInBackground(Void... voids){
            try {
                //sk = new Socket("192.168.1.110", 22222);
                sk = new Socket();
                sk.connect(ia, 5000);
                while (sk.isConnected() == false) ;

                Log.d(LOG_TAG, getString(R.string.connected_str));
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, e.getMessage());
                err = e;
            }
            return null;
        }

        //выполняется после doInBackground (не срабатывает в случае, если AsyncTask был отменен - об этом в следующих уроках), имеет доступ к UI
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //Log.d(LOG_TAG, "stop: " + ((sk.isConnected() )? "Connected": "Not connected"));
            //txtResult.setText("stop: "+ ((sk.isConnected())? "Connected": "Not connected"));
            if (err != null){
                Log.d(LOG_TAG, err.getMessage());
                txtResult.setText(err.getMessage());
            }

            btnSend.setEnabled(true);

            if (sk.isConnected()) {
                Log.d(LOG_TAG, getString(R.string.connected_str));
                txtResult.setText(R.string.connected_str);
                try {
                    sk.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, e.getMessage());
                }
            }

        }
    }
}
