package com.tmet.sitiosseguros;

import android.*;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by diego on 04/08/2017.
 */

public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    public Location lu;
    private double latA;
    private double lenA;
    private String lat="",len="",mg="";
    public Connection conexion;
    private String usuario;
    private String telf1;
    private String telf2;
    private String im;
    private String imei;
    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.
        try {
            conexion = ConexionSQL.ConnectionHelper();


        } catch (Exception e) {

            createAndShowDialog("No se pudo conectar al servidor. Revise su conexión a Internet.", "Error");
        }
        usuario="";
        telf1="0";
        telf2="0";
        im="0";
        boolean band=false;
        for(int i=10;i<data.toString().length();i++)
        {
            if(!band)
            {
                if(data.toString().charAt(i)=='"')
                {
                    i++;
                    while(data.toString().charAt(i)!='"' )
                    {
                        if((Character.isDigit(data.toString().charAt(i)) || data.toString().charAt(i)=='.' || data.toString().charAt(i)=='-'))
                        {
                            lat+=data.toString().charAt(i);
                        }
                        i++;
                    }
                    band=true;
                }
            }else
            {
                if(data.toString().charAt(i)=='"')
                {
                    i++;
                    while(data.toString().charAt(i)!='"')
                    {
                        if((Character.isDigit(data.toString().charAt(i)) || data.toString().charAt(i)=='.' || data.toString().charAt(i)=='-'))
                        {
                            len+=data.toString().charAt(i);
                        }
                        i++;
                    }
                    i++;
                    while(data.toString().charAt(i)!='"')
                    {
                        i++;
                    }
                    i++;
                    while(data.toString().charAt(i)!='"')
                    {
                        if(Character.isDigit(data.toString().charAt(i)) || data.toString().charAt(i)=='.')
                        {
                            mg+=data.toString().charAt(i);
                        }
                        i++;
                    }
                    break;
                }
            }
        }
        //Toast.makeText(MyApplication.getContext(),lat+";"+len+";"+mg, Toast.LENGTH_LONG).show();

        buildGoogleApiClient();
        /*

        <marker lat="1.3132" lng="-79.6798" mg="4" z="28.84045029" fecha="2017/08/02 19:48:51" estado="manual" eventoid="igepn2017parj" localizacion="38.50km Esmeraldas,Esmeraldas"
         */

        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
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

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(MyApplication.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(MyApplication.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lu = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        double dist=0;
        if (lu != null)
        {
            latA=lu.getLatitude();
            lenA=lu.getLongitude();
            LatLng pos=new LatLng(latA,lenA);
            dist= SphericalUtil.computeDistanceBetween(pos,new LatLng(Double.parseDouble(lat),Double.parseDouble(len)));
            if(dist<=10000 && Double.parseDouble(mg)>5)
            {
                Intent intent = new Intent(MyApplication.getContext(), MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getContext().startActivity(intent);
                Toast.makeText(MyApplication.getContext(),"Se encuentra en área de peligro, acerquese al sitio seguro más cercano. ", Toast.LENGTH_LONG).show();
                imei=obtenerImei();
                verificarUsuario(imei);
                if(!im.equals("0"))
                {
                    String message="Hola, " + usuario + " se encuentra en una zona de riesgo. "+pos.latitude+","+pos.longitude;
                    if(!telf1.equals("0"))
                    {
                        sendSMS(telf1,message);
                    }
                    if(!telf2.equals("0"))
                    {
                        sendSMS(telf2,message);
                    }
                }
            }else
            {
                Toast.makeText(MyApplication.getContext(),"No se encuentra en área de peligro", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
