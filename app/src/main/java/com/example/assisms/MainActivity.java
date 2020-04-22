package com.example.assisms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    final int RECEIVE_SMS_PERMISSION_REQUEST_CODE = 1;

    Button btnSend;
    EditText tvMessage;
    EditText tvNumber;
    IntentFilter intentFilter;

    private BroadcastReceiver intentReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView inTxt = findViewById(R.id.textMsg);
            inTxt.setText(intent.getExtras().getString("message"));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        btnSend = findViewById(R.id.btnSend);
        tvMessage = findViewById(R.id.tvMessage);
        tvNumber = findViewById(R.id.tvNumber);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myMsg = tvMessage.getText().toString();
                String theNumber = tvNumber.getText().toString();
                sendMsg(theNumber,myMsg);
            }
        });

        btnSend.setEnabled(false);
        if(checkPermission(Manifest.permission.SEND_SMS)){
            btnSend.setEnabled(true);
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_PERMISSION_REQUEST_CODE);

        }

    }

    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    private void sendMsg(String theNumber, String myMsg) {

        String SENT = "Message Sent";
        String DELIVERED = "Message Delivered";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT),0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this,0,new Intent(DELIVERED),0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(theNumber, null, myMsg,sentPI,deliveredPI);
    }

    @Override
    protected void onResume() {
        registerReceiver(intentReciever, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause(){
        unregisterReceiver(intentReciever);
        super.onPause();
    }

}
