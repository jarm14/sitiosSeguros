package com.tmet.sitiosseguros;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by joel on 5/15/17.
 */

public class ConexionSQL {
    public static Connection ConnectionHelper() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:jtds:sqlserver://tmet.database.windows.net:1433/puntosSeguros; ssl=require","Administrador@tmet","Admin112358.");

        } catch (SQLException se) {
            Log.e("ERROR", se.getMessage());

        } catch (ClassNotFoundException e) {
            Log.e("ERROR", "No se pudo conectar a la base de datos. Revise su conexión a internet.");

        } catch (Exception e) {
            Log.e("ERROR", "No se pudo conectar.");
        }

        return connection;
    }
}
