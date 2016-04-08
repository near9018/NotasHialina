package com.example.ok4r_.notashialina.modelo;

/**
 * Created by ok4r- on 11/01/2016.
 */
public class Cliente
{
    public String idCliente;
    public String nombreClt;
    public String direccionClt;
    public String telefonoClt;
    public String contactoClt;
    public String emailClt;
    public String rfcClt;
    public double descuento;

    public Cliente(String idCliente, String nombreClt, String direccionClt, String telefonoClt,String contactoClt, String emailClt, String rfcClt, double descuento)
    {
        this.idCliente=idCliente;
        this.nombreClt=nombreClt;
        this.direccionClt=direccionClt;
        this.telefonoClt=telefonoClt;
        this.contactoClt=contactoClt;
        this.emailClt=emailClt;
        this.rfcClt=rfcClt;
        this.descuento =descuento;
    }
}
