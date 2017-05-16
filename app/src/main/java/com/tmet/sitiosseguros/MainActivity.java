package com.tmet.sitiosseguros;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    /*Listas a utilizar*/
    ArrayList<Sitio> lstSitios = new ArrayList<>();

    /*Variable para la conexion a SQL*/
    Connection conexion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            conexion = ConexionSQL.ConnectionHelper();

        }catch(Exception e)
        {

            createAndShowDialog("No se pudo conectar al servidor. Revise su conexión a Internet.", "Error");
        }

        crearListaSitios("");
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

    /*Metodo para la creacion de lista de datos recuperados de la tabla Sitio*/
    public void crearListaSitios(String tipoDesastre)
    {
        ResultSet itemSitio;
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
}



