package com.example.ok4r_.notashialina.modelo;

/**
 * Created by ok4r- on 21/01/2016.
 */
public class Producto
{
    public String idProducto;
    public String nombrePrd;
    public double precioPrd;
    public double IVA;

    public Producto (String idProducto, String nombrePrd, double precioPrd, double IVA)
    {
        this.idProducto=idProducto;
        this.nombrePrd=nombrePrd;
        this.precioPrd=precioPrd;
        this.IVA=IVA;
    }
}
