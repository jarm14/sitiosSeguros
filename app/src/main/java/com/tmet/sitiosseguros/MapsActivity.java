package com.tmet.sitiosseguros;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private String[] info;
    private double lat;
    private double len;
    private List<String> puntos;
    private PolylineOptions polyop;
    private Polyline polyline;
    /*Listas a utilizar*/
    public ArrayList<Sitio> lstSitios = new ArrayList<>();
    /*Variable para la conexion a SQL*/
    public Connection conexion;

    ArrayList<Sitio> values=new ArrayList<>();

    private GoogleApiClient googleApiClient;
    Location lu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent myIntent = getIntent(); // gets the previously created intent
        int myposition = myIntent.getIntExtra("position",0);

        try
        {
            conexion = ConexionSQL.ConnectionHelper();


        }catch(Exception e)
        {

            createAndShowDialog("No se pudo conectar al servidor. Revise su conexión a Internet.", "Error");
        }

        crearListaSitios();



        //for(int i=0;i<lstSitios.size();i++){
        /*if(myposition==0){
            //values.clear();
            for(int i=0;i<5;i++){
                values.add(lstSitios.get(i));
            }
        }else if(myposition==1){
            //values.clear();
            for(int i=5;i<10;i++){
                values.add(lstSitios.get(i));
            }
        }else if(myposition==2){
            //values.clear();
            for(int i=10;i<15;i++){
                values.add(lstSitios.get(i));
            }
        }
        double dist=0;
        for (int i=0;i<lstSitios.size();i++)
        {

        }*/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();*/
       buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
        googleApiClient.connect();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(true);

        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(true);

        }

        /*LatLng decc = new LatLng(lat,len);
        mMap.addMarker(new MarkerOptions().position(decc).title("Usuario").snippet("'"+lat+len+"'"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(decc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));*/

        marcadores();
    }

    public void marcadores()
    {
        //LatLng decc = new LatLng(lat,len);
        for (int i=0;i<values.size();i++)
        {
            mMap.addMarker(new MarkerOptions().position(coordenadas(values.get(i).getUbicacion())).title(values.get(i).getNombre()));
        }
    }

    private LatLng coordenadas(String aux) {
        LatLng coor;
        String[] latlen;
        latlen = aux.split(",");
        coor=new LatLng(Double.parseDouble(latlen[0]),Double.parseDouble(latlen[1]));
        return coor;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        double dist=0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (lu != null) {
            //generarRuta(new LatLng(lu.getLatitude(), lu.getLongitude()), new LatLng(lat, len));
            lat=lu.getLatitude();
            len=lu.getLongitude();
            LatLng pos=new LatLng(lat,len);
            for(int i=0;i<lstSitios.size();i++)
            {
                dist= SphericalUtil.computeDistanceBetween(coordenadas(values.get(i).getUbicacion()),pos);
                if(dist<=50000)
                {
                    values.add(lstSitios.get(i));
                }
            }
            mMap.addMarker(new MarkerOptions().position(pos).title("Ubicación actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(11));
            marcadores();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //if (googleApiClient != null) {
        googleApiClient.connect();
        //}

    }

    /*Metodo para la creacion de lista de datos recuperados de la tabla Sitio*/
    public void crearListaSitios()
    {
        ResultSet itemSitio;
        //String sqlSitio="use puntosSeguros; select * from sitio where TIPODESASTRE="+type+";";
        String sqlSitio="use puntosSeguros; select * from sitio;";
        try {

            Statement statement = conexion.createStatement();
            itemSitio = statement.executeQuery(sqlSitio);

            while (itemSitio.next())
            {
                // navegar por nuestro ResultSet en cada registro, siempre y cuando exista un prox.
                Sitio tempSitio = new Sitio();

                tempSitio.setId(Integer.parseInt(itemSitio.getString("ID")));
                tempSitio.setNombre(itemSitio.getString("NOMBRE"));
                tempSitio.setDetalle(itemSitio.getString("DETALLE"));
                tempSitio.setUbicacion(itemSitio.getString("UBICACION"));
                tempSitio.setTipoDesastre(itemSitio.getString("TIPODESASTRE"));

                lstSitios.add(tempSitio);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
}
