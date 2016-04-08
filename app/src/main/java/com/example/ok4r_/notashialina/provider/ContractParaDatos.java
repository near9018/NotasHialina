package com.example.ok4r_.notashialina.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract Class entre el provider y las aplicaciones
 */
public class ContractParaDatos {
    /**
     * Autoridad del Content Provider
     */
    public final static String AUTHORITY
            = "com.example.ok4r_.notashialina";
    /**
     * Representación de la tabla a consultar
     */
    public static final String CLIENTE = "cliente";
    public static final String PRODUCTO = "producto";
    public static final String RUTA = "ruta";
    public static final String BITACORA = "bitacora";
    public static final String NOTA_REMISION = "nota";
    public static final String NOTA_CLIENTE_PRODUCTO = "nota_cliente_producto";
    public static final String RUTA_CLIENTE = "ruta_cliente";
    public static final String NUMERO_RUTA = "numero_rutas";
    public static final String DIA_RUTA = "dia_ruta";

    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME_CLIENTE = "vnd.android.cursor.item/vnd." + AUTHORITY + CLIENTE;
    public final static String SINGLE_MIME_PRODUCTO = "vnd.android.cursor.item/vnd." + AUTHORITY + PRODUCTO;
    public final static String SINGLE_MIME_RUTA = "vnd.android.cursor.item/vnd." + AUTHORITY + RUTA;
    public final static String SINGLE_MIME_BITACORA = "vnd.android.cursor.item/vnd." + AUTHORITY + BITACORA;
    public final static String SINGLE_MIME_NOTAREMISION = "vnd.android.cursor.item/vnd." + AUTHORITY + NOTA_REMISION;
    public final static String SINGLE_MIME_NUMERORUTA = "vnd.android.cursor.item/vnd." + AUTHORITY + NUMERO_RUTA;
    public final static String SINGLE_MIME_DIARUTA = "vnd.android.cursor.item/vnd." + AUTHORITY + DIA_RUTA;
    /**
     * Tipo MIME que retorna la consulta de multiples filas
     */
    public final static String MULTIPLE_MIME_CLIENTE = "vnd.android.cursor.dir/vnd." + AUTHORITY + CLIENTE;
    public final static String MULTIPLE_MIME_PRODUCTO = "vnd.android.cursor.dir/vnd." + AUTHORITY + PRODUCTO;
    public final static String MULTIPLE_MIME_RUTA = "vnd.android.cursor.dir/vnd." + AUTHORITY + RUTA;
    public final static String MULTIPLE_MIME_BITACORA = "vnd.android.cursor.dir/vnd." + AUTHORITY + BITACORA;
    public final static String MULTIPLE_MIME_NOTAREMISION = "vnd.android.cursor.dir/vnd." + AUTHORITY + NOTA_REMISION;
    public final static String MULTIPLE_MIME_NUMERORUTA = "vnd.android.cursor.dir/vnd." + AUTHORITY + NUMERO_RUTA;
    public final static String MULTIPLE_MIME_DIARUTA = "vnd.android.cursor.dir/vnd." + AUTHORITY + DIA_RUTA;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI_CLIENTE = Uri.parse("content://" + AUTHORITY + "/" + CLIENTE);
    public final static Uri CONTENT_URI_PRODUCTO = Uri.parse("content://" + AUTHORITY + "/" + PRODUCTO);
    public final static Uri CONTENT_URI_RUTA = Uri.parse("content://" + AUTHORITY + "/" + RUTA);
    public final static Uri CONTENT_URI_BITACORA = Uri.parse("content://" + AUTHORITY + "/" + BITACORA);
    public final static Uri CONTENT_URI_NOTAREMISION = Uri.parse("content://" + AUTHORITY + "/" + NOTA_REMISION);
    public final static Uri CONTENT_URI_RUTA_CLIENTE = Uri.parse("content://" + AUTHORITY + "/" + RUTA_CLIENTE);
    public final static Uri CONTENT_URI_NOTA_CLIENTE_PRODUCTO = Uri.parse("content://" + AUTHORITY + "/" + NOTA_CLIENTE_PRODUCTO);
    public final static Uri CONTENT_URI_NUMERORUTA = Uri.parse("content://" + AUTHORITY + "/" + NUMERO_RUTA);
    public final static Uri CONTENT_URI_DIARUTA = Uri.parse("content://" + AUTHORITY + "/" + DIA_RUTA);
    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int CLIENTES = 100;
    public static final int PRODUCTOS = 200;
    public static final int RUTAS = 300;
    public static final int BITACORAS = 400;
    public static final int  NOTAS_REMISION = 500;
    public static final int NUMERO_RUTAS = 600;
    public static final int DIA_RUTAS = 700;
    /**
     * Código para URIS de un solo registro
     */
    public static final int CLIENTES_ID = 101;
    public static final int PRODUCTOS_ID = 201;
    public static final int RUTAS_ID = 301;
    public static final int BITACORAS_ID = 401;
    public static final int NOTASREMISION_ID = 501;
    public static final int NUMERORUTAS_ID = 601;
    public static final int DIARUTAS_ID = 701;

    public static final int RUTAS_CLIENTES = 302;
    public static final int NOTAS_CLIENTES_PRODUCTOS = 502;
    
    // Asignación de URIs
    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CLIENTE, CLIENTES);
        uriMatcher.addURI(AUTHORITY, CLIENTE + "/#", CLIENTES_ID);

        uriMatcher.addURI(AUTHORITY, PRODUCTO, PRODUCTOS);
        uriMatcher.addURI(AUTHORITY, PRODUCTO + "/#", PRODUCTOS_ID);

        uriMatcher.addURI(AUTHORITY, RUTA, RUTAS);
        uriMatcher.addURI(AUTHORITY, RUTA + "/#", RUTAS_ID);
        uriMatcher.addURI(AUTHORITY, RUTA_CLIENTE , RUTAS_CLIENTES);

        uriMatcher.addURI(AUTHORITY, BITACORA, BITACORAS);
        uriMatcher.addURI(AUTHORITY, BITACORA + "/#", BITACORAS_ID);

        uriMatcher.addURI(AUTHORITY, NOTA_REMISION, NOTAS_REMISION);
        uriMatcher.addURI(AUTHORITY, NOTA_REMISION + "/#", NOTASREMISION_ID);
        uriMatcher.addURI(AUTHORITY, NOTA_CLIENTE_PRODUCTO, NOTAS_CLIENTES_PRODUCTOS);

        uriMatcher.addURI(AUTHORITY, NUMERO_RUTA, NUMERO_RUTAS);
        uriMatcher.addURI(AUTHORITY, NUMERO_RUTA +"/#", NUMERORUTAS_ID);

        uriMatcher.addURI(AUTHORITY,DIA_RUTA, DIA_RUTAS);
        uriMatcher.addURI(AUTHORITY, DIA_RUTA +"/*", DIARUTAS_ID);
    }

    // Valores para la columna ESTADO
    public static final int ESTADO_OK = 0;
    public static final int ESTADO_SYNC = 1;


    /**
     * Estructura de las tablas
     */
    public static class ColumnasCliente
    {
        private ColumnasCliente()
        {
            // Sin instancias
        }
        //Tabla Clientes
        public final static String ID_CLIENTES =BaseColumns._ID;
        public final static String NOMBRE = "nombre";
        public final static String DIRECCION = "direccion";
        public final static String TELEFONO = "telefono";
        public final static String CONTACTO = "contacto";
        public final static String EMAIL = "email";
        public final static String RFC = "rfc";
        public final static String DESCUENTO = "descuento";

        //Campos comunes
        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";
    }

    public static class ColumnasProducto
    {
        private ColumnasProducto()
        {
            // Sin instancias
        }
        //tabla Productos
        public final static String ID_PRODUCTOS = BaseColumns._ID;
        public final static String PRODUCTO = "producto";
        public final static String PRECIO = "precio";
        public final static String IVA = "iva";

        //Campos comunes
        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";
    }

    public static class ColumnasRuta
    {
        private ColumnasRuta()
        {
            // Sin instancias
        }
        //Tabla Clientes
        public final static String ID_RUTAS =BaseColumns._ID;
        public final static String NUMERO_RUTA = "numeroRuta";
        public final static String DIA = "dia";
        public final static String SECUENCIA = "secuenciaVisita";
        public final static String CLIENTE = "clienteId";

        //Campos comunes
        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";
    }

    public static class ColumnasBitacora
    {
        private ColumnasBitacora()
        {
            // Sin instancias
        }
        //Tabla Clientes
        public final static String ID_BITACORA =BaseColumns._ID;
        public final static String NUMERO_RUTA = "rutaId";
        public final static String FECHAHORA = "fechaHora";
        public final static String CLIENTE_ID = "clienteId";
        public final static String LATITUD = "latitud";
        public final static String LONGITUD = "longitud";
        public final static String CONCEPTO = "conceptoRegistro";
        public final static String INCIDENCIA = "incidencia";

        //Campos comunes
        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";
    }

    public static class ColumnasNotaRemision
    {
        private ColumnasNotaRemision()
        {
            // Sin instancias
        }
        //Tabla Clientes
        public final static String ID_NOTA = BaseColumns._ID;
        public final static String RUTA = "ruta";
        public final static String VENDEDOR = "vendedor";
        public final static String FECHA_OPERACION = "fechaOperacion";
        public final static String FECHA_REGISTRO = "fechaRegistro";
        public final static String CODIGO_CLIENTE = "codigoClt";
        public final static String CODIGO_PRODUCTO = "codigoPrd";
        public final static String PRECIO = "precio";
        public final static String IVA = "IVA";
        public final static String CANTIDAD = "cantidad";
        public final static String TOTAL_NOTA = "totalNota";
        public final static String ACTIVA = "notaActiva";

        //Campos comunes
        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";
    }

    public static class ColumnasNumeroRutas
    {
        private ColumnasNumeroRutas()
        {
        }
        //Tabla de todas las rutas
        public final static String ID_NUMERORUTA = BaseColumns._ID;

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";
    }

    public static class ColumnasDiaRutas
    {
        private ColumnasDiaRutas()
        {
        }
        //Tabla de todas las rutas
        public final static String ID_DIARUTA = BaseColumns._ID;

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";
    }
}
