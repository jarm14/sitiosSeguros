package com.tmet.sitiosseguros;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String[] info;
    private double lat;
    private double len;
    private List<String> puntos;
    private PolylineOptions polyop;
    private Polyline polyline;
    private Marker marcador;
    private boolean band;
    /*Listas a utilizar*/
    public ArrayList<Sitio> lstSitios = new ArrayList<>();
    /*Variable para la conexion a SQL*/
    public Connection conexion;

    ArrayList<Sitio> values = new ArrayList<>();

    private GoogleApiClient googleApiClient;
    public Location lu;

    ArrayList<LatLng> MarkerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent myIntent = getIntent(); // gets the previously created intent
        int myposition = myIntent.getIntExtra("position", 0);

        try {
            conexion = ConexionSQL.ConnectionHelper();


        } catch (Exception e) {

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
        MarkerPoints = new ArrayList<>();
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
        mMap.setOnMarkerClickListener(this);
        /*LatLng decc = new LatLng(lat,len);
        mMap.addMarker(new MarkerOptions().position(decc).title("Usuario").snippet("'"+lat+len+"'"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(decc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));*/
        //lu = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

    }

    public void marcadores()
    {
        //LatLng decc = new LatLng(lat,len);
        if (values.size()!=0)
        {
            for (int i=0;i<values.size();i++)
            {
                mMap.addMarker(new MarkerOptions().position(coordenadas(values.get(i).getUbicacion())).title(values.get(i).getNombre()));
            }
        }else
        {
            Toast.makeText(getApplicationContext(),"No hay lugares seguros cerca de su posición", Toast.LENGTH_SHORT).show();
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
        LatLng ps;
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
            lat=lu.getLatitude();
            len=lu.getLongitude();
            LatLng pos=new LatLng(lat,len);
            mMap.addMarker(new MarkerOptions().position(pos).title("Ubicación actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(11));
            Toast.makeText(getApplicationContext(),"Su posición es: ("+lat+","+len+")", Toast.LENGTH_SHORT).show();
            if(lstSitios.size()!=0)
            {
                for(int i=0;i<lstSitios.size();i++)
                {
                    dist= SphericalUtil.computeDistanceBetween(coordenadas(lstSitios.get(i).getUbicacion()),pos);
                    if(dist<=10000)
                    {
                        values.add(lstSitios.get(i));
                    }
                }
                marcadores();
                if(values.size()>0)
                {
                    ps=coordenadas(values.get(0).getUbicacion());
                    for (int i=1;i<values.size();i++)
                    {
                        if(SphericalUtil.computeDistanceBetween(coordenadas(values.get(i).getUbicacion()),pos)<SphericalUtil.computeDistanceBetween(ps,pos))
                        {
                            ps=coordenadas(values.get(i).getUbicacion());
                        }
                    }
                    generarRuta(pos,ps);
                }
            }else
            {
                Toast.makeText(getApplicationContext(),"No hay lugares seguros cerca de su posición", Toast.LENGTH_SHORT).show();
            }



        }
    }

    public void generarRuta(LatLng ini,LatLng fin)
    {
        String url = getUrl(ini, fin);

        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        polyline.remove();
        //Toast.makeText(getApplicationContext(), "Marcador clickeado "+lat+" "+len, Toast.LENGTH_LONG).show();
        generarRuta(new LatLng(lat,len),marker.getPosition());
        return true;
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

    //Rutas desde Google Maps
    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }



    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                polyline=mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}

