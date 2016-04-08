package com.example.ok4r_.notashialina.modelo;

/**
 * Created by ok4r- on 16/01/2016.
 */
public class SpinnerItem
{
    private String ID;
    private String nombre;
    private String direccion;

    public SpinnerItem(String ID, String nombre, String direccion)
    {
        this.setID(ID);
        this.setNombre(nombre);
        this.setDireccion(direccion);
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getDireccion()
    {
        return direccion;
    }

    public void setDireccion(String direccion)
    {
        this.direccion = direccion;
    }
}