package com.example.ok4r_.notashialina.utils;

/**
 * Constantes
 */
public class Constantes
{
    /**
     * Constantes para {@link Localizacion}
     */
    public static final String LOCALIZACION = "com.example.ok4r_.notashialina.action.LOCALIZACION";
    public static final String ACTION_MEMORY_EXIT = "com.example.o4kr_.notashialina.action.MEMORY_EXIT";

    public static final String LATITUD = "com.example.o4kr_.notashialina.extra.LATITUD";
    public static final String LONGITUD = "com.example.o4kr_.notashialina.extra.LONGITUD";
    /**
     * Puerto que se utiliza para la conexión.
     * Dejalo en blanco si no has configurado esta característica.
     */
    private static final String PUERTO_HOST = ":63343";
    /**
     * Dirección IP
     */
    private static final String IP = "http://basededatoselpolo.esy.es";
    /**
     * URLs del Web Service
     */
    public static final String GET_RUTAS_URL  = IP + "/connect123/obtenerRutas.php";
    public static final String GET_CLIENTES_URL  = IP + "/connect123/obtenerClientes.php";
    public static final String GET_PRODUCTOS_URL = IP + "/connect123/obtenerProductos.php";
    public static final String GET_DIASRUTA_URL = IP + "/connect123/obtenerDiasRuta.php";
    public static final String INSERT_NOTA_URL = IP + "/connect123/insertarNota.php";
    public static final String INSERT_BITACORA_URL = IP + "/connect123/insertarBitacora.php";
    /**
     * Campos de las respuestas Json
     */
    public static final String ID_NOTA = "folio";
    public static final String ID_BITACORA = "registroBitacoraId";
    public static final String ESTADO = "estado";
    public static final String CLIENTES = "clientes";
    public static final String PRODUCTOS = "productos";
    public static final String RUTA = "rutas";
    public static final String MENSAJE = "mensaje";

    /**
     * Códigos del campo {ESTADO}
     */
    public static final String SUCCESS = "1";
    public static final String FAILED = "2";

    /**
     * Tipo de cuenta para la sincronización
     */
    public static final String ACCOUNT_TYPE = "com.example.ok4r_.notashialina.account";


}
