package com.tmet.sitiosseguros;

/**
 * Created by joel on 5/15/17.
 */

public class Sitio
{
    private int id;
    private String nombre;
    private String detalle;
    private String ubicacion;
    private String tipoDesastre;

    public Sitio()
    {
        this.id=0;
        this.nombre="";
        this.detalle="";
        this.ubicacion="";
        this.tipoDesastre="";
    }

    public Sitio(int id, String nombre, String detalle, String ubicacion, String tipoDesastre)
    {
        this.id=id;
        this.nombre=nombre;
        this.detalle=detalle;
        this.ubicacion=ubicacion;
        this.tipoDesastre=tipoDesastre;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipoDesastre() {
        return tipoDesastre;
    }

    public void setTipoDesastre(String tipoDesastre) {
        this.tipoDesastre = tipoDesastre;
    }
}
