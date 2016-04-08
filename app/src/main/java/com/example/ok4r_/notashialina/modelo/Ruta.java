package com.example.ok4r_.notashialina.modelo;

/**
 * Created by ok4r- on 30/01/2016.
 */
public class Ruta
{
    public String rutasID;
    public int numeroRuta;
    public String diaSemana;
    public int secuenciaVisita;
    public int codigoClt;

    public Ruta(String rutasID, int numeroRuta,String diaSemana, int secuenciaVisita, int codigoClt)
    {
        this.rutasID = rutasID;
        this.numeroRuta = numeroRuta;
        this.diaSemana = diaSemana;
        this.secuenciaVisita = secuenciaVisita;
        this.codigoClt = codigoClt;
    }
}