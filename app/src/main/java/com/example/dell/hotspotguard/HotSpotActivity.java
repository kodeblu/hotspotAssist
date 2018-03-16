package com.example.dell.hotspotguard;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class HotSpotActivity extends AppCompatActivity {

    private EditText yourName;

    public  Button startBtn;
    public  Button stopBtn;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    DatabaseHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_spot);
        yourName = (EditText) findViewById(R.id.name);
        yourName.setHint("Wifi Hotspot ID of choice");
        yourName.setHintTextColor(Color.WHITE);
        startBtn = (Button) findViewById(R.id.start_button);
        stopBtn = (Button) findViewById(R.id.stop_button);
        myDb = new DatabaseHelper(this);
        setupProp();
    }

    public void startAlarm(View view){

       if(!beginAction()){
            manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            //int interval = 86400000;//24hrs
            //int interval = 7200000;
            int interval = 5000;
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
           setupProp();
            Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(View view){
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        myDb.SsidDelete();
        setupProp();
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();


    }
    public void setupProp(){
        int ssidCount = myDb.SsidCount();
        if(ssidCount > 0){
            yourName.setVisibility(View.GONE);
            startBtn.setVisibility(View.GONE);
            stopBtn.setVisibility(View.VISIBLE);
        }else{
            yourName.setVisibility(View.VISIBLE);
            startBtn.setVisibility(View.VISIBLE);
            stopBtn.setVisibility(View.GONE);
        }
    }
    private boolean beginAction() {
        yourName.setError(null);
        String wifi_id = yourName.getText().toString().trim();
        boolean cancel = false;
        View focusView = null;
        if (wifi_id.isEmpty()) {
            yourName.setError(getString(R.string.error_field_required));
            focusView = yourName;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            myDb.SsidDelete();
            Boolean response = myDb.addSsidAcct(wifi_id);
            if(response){
                Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            }else{
                    Log.d("msg","insert failure");
            }
        }

        return cancel;
    }



}

