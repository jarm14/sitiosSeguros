package com.tmet.sitiosseguros;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.onesignal.OneSignal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ListView lstMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        setContentView(R.layout.activity_main);

        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Menu");
        spec.setContent(R.id.tab1);

        lstMenu = (ListView) findViewById(R.id.lstMenu);

        // Defined Array values to show in ListView
        String[] values = new String[]{"TERREMOTO",
                "TSUNAMI",
                "ERUPCIÓN VOLCÁNICA"
        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);


        // Assign adapter to ListView
        lstMenu.setAdapter(adapter);

        // ListView Item Click Listener
        lstMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;


                // ListView Clicked item value
                String itemValue = (String) lstMenu.getItemAtPosition(position);

                // Show Alert
//                Toast.makeText(getApplicationContext(),
//                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
//                        .show();


                Intent myIntent = new Intent(view.getContext(), MapsActivity.class);
                myIntent.putExtra("position", position);

                startActivity(myIntent);
            }
        });

        spec.setIndicator("Menu");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Mensaje");
        spec.setContent(R.id.tab2);

        //setContentView(R.layout.message_config);
        final EditText name = (EditText) findViewById(R.id.txtName);
        final EditText tel = (EditText) findViewById(R.id.txtTel);


        Button btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = "Hola, " + name.getText().toString() + " se encuentra en una zona de riesgo del terremoto";
                String phone = tel.getText().toString();
                sendSMS(phone, message);
           /*here i can send message to emulator 5556. In Real device
            *you can change number*/
            }
        });

//0998390853

        spec.setIndicator("Mensaje");
        host.addTab(spec);

        //Tab 3
        //spec = host.newTabSpec("Comunícate");
        //spec.setContent(R.id.tab3);
        //spec.setIndicator("Comunícate");
        //host.addTab(spec);
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}



