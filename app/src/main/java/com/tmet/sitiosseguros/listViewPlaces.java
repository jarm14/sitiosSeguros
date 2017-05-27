package com.tmet.sitiosseguros;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 27/05/2017.
 */

public class listViewPlaces extends AppCompatActivity {
    ListView listView ;
    //ListView lstViewSitios;
    /*Listas a utilizar*/
    public ArrayList<Sitio> lstSitios = new ArrayList<>();
    /*Variable para la conexion a SQL*/
    public Connection conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_places);

        try
        {
            conexion = ConexionSQL.ConnectionHelper();


        }catch(Exception e)
        {

            createAndShowDialog("No se pudo conectar al servidor. Revise su conexión a Internet.", "Error");
        }

        crearListaSitios();
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.lstPlaces);

        // Defined Array values to show in ListView
        ArrayList<String> values=new ArrayList<>();

        for(int i=0;i<lstSitios.size();i++){
            values.add(lstSitios.get(i).getNombre());
        }





        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue+"Position: "+lstSitios.get(itemPosition).getUbicacion() , Toast.LENGTH_LONG)
                        .show();

            }

        });
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
