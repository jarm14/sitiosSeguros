package com.tmet.sitiosseguros;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ListView lstMenu;

    public Connection conexion;
    private String usuario;
    private String telf1;
    private String telf2;
    private String im;
    private String imei;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            conexion = ConexionSQL.ConnectionHelper();


        } catch (Exception e) {

            createAndShowDialog("No se pudo conectar al servidor. Revise su conexión a Internet.", "Error");
        }

        setContentView(R.layout.activity_main);

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new NotificationOpenedHandler())
                .setNotificationReceivedHandler( new NotificationReceivedHandler() )
                .init();
        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Menu");
        spec.setContent(R.id.tab1);

        lstMenu = (ListView) findViewById(R.id.lstMenu);
        usuario="";
        telf1="0";
        telf2="0";
        im="0";
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
        final EditText tel2= (EditText) findViewById(R.id.txtTel2);

        Button btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            imei=obtenerImei();
            verificarUsuario(imei);
            if(im.equals("0"))
            {
                if(name.getText().toString().equals("") || tel.getText().toString().equals("") || tel2.getText().toString().equals(""))
                {
                    Toast.makeText(MyApplication.getContext(),"Debe llenar todos los campos", Toast.LENGTH_LONG).show();
                }else
                {
                    insertarUsuario(name.getText().toString(),tel.getText().toString(),tel2.getText().toString(),imei);
                    Toast.makeText(MyApplication.getContext(),"Datos ingresados correctamente", Toast.LENGTH_LONG).show();
                }
            }else
            {
                if(name.getText().toString().equals("") || tel.getText().toString().equals("") || tel2.getText().toString().equals(""))
                {
                    Toast.makeText(MyApplication.getContext(),"Debe llenar todos los campos", Toast.LENGTH_LONG).show();
                }else
                {
                    actualizarUsuario(name.getText().toString(),tel.getText().toString(),tel2.getText().toString(),imei);
                    Toast.makeText(MyApplication.getContext(),"Datos actualizados correctamente", Toast.LENGTH_LONG).show();
                }
            }
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

    public void verificarUsuario(String imei)
    {
        ResultSet itemSitio;
        //String sqlSitio="use puntosSeguros; select * from sitio where TIPODESASTRE="+type+";";
        String sqlSitio="use puntosSeguros; select * from usuario where IMEI='"+imei+"';";
        try {

            Statement statement = conexion.createStatement();
            itemSitio = statement.executeQuery(sqlSitio);

            while (itemSitio.next())
            {
                usuario=itemSitio.getString("NOMBRE");
                telf1=itemSitio.getString("TEL1");
                telf2=itemSitio.getString("TEL2");
                im=itemSitio.getString("IMEI");
            }

        } catch (SQLException se)
        {
            createAndShowDialog(se,"Error: ");
        }catch (Exception ex)
        {
            createAndShowDialog(ex,"Error");
        }

    }

    public void insertarUsuario(String nombre,String tel1,String tel2,String imei)
    {
        //String sqlSitio="use puntosSeguros; select * from sitio where TIPODESASTRE="+type+";";
        String sqlSitio="use puntosSeguros; insert into usuario values ('"+nombre+"','+593"+tel1+"','+593"+tel2+"','"+imei+"';";
        try {

            PreparedStatement ps = conexion.prepareStatement("insert into usuario values (? , ? , ? , ?);");
            ps.setString(1,nombre);
            ps.setString(2,tel1);
            ps.setString(3,tel2);
            ps.setString(4,imei);
            ps.executeUpdate();
        } catch (SQLException se)
        {
            createAndShowDialog(se,"Error: ");
        }catch (Exception ex)
        {
            createAndShowDialog(ex,"Error");
        }

    }

    public void actualizarUsuario(String nombre,String tel1,String tel2,String imei)
    {
        //String sqlSitio="use puntosSeguros; select * from sitio where TIPODESASTRE="+type+";";
        String sqlSitio="use puntosSeguros; update usuario set NOMBRE='"+nombre+"',TEL1='"+tel1+"' where IMEI='"+imei+"';";
        try {

            PreparedStatement ps = conexion.prepareStatement("update usuario set NOMBRE= ? ,TEL1= ?, TEL2= ? where IMEI= ? ;");
            ps.setString(1,nombre);
            ps.setString(2,tel1);
            ps.setString(3,tel2);
            ps.setString(4,imei);
            ps.executeUpdate();
        } catch (SQLException se)
        {
            createAndShowDialog(se,"Error: ");
        }catch (Exception ex)
        {
            createAndShowDialog(ex,"Error");
        }

    }
    /*Metodos para la creación de cuadros de dialogo*/
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MyApplication.getContext());

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    public String obtenerImei()
    {

        if(Build.VERSION.SDK_INT  < Build.VERSION_CODES.M)
        {
            //Menores a Android 6.0
            String imei= a();
            return imei;
        }
        else
        {
            // Mayores a Android 6.0
            String imei="";
            if (ActivityCompat.checkSelfPermission(MyApplication.getContext(),android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                imei="";
            } else {
                imei= a();
            }

            return imei;

        }
    }

    private String a() {

        TelephonyManager tm = (TelephonyManager)  MyApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String imei =tm.getDeviceId(); // Obtiene el imei  or  "352319065579474";
        return imei;

    }
}



