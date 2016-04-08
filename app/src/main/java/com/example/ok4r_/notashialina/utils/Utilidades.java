package com.example.ok4r_.notashialina.utils;

import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import com.example.ok4r_.notashialina.provider.ContractParaDatos;

import org.json.JSONException;
import org.json.JSONObject;

public class Utilidades {
    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_RUTA = 2;
    public static final int COLUMNA_VENDEDOR = 3;
    public static final int COLUMNA_FECHAOPERACION = 4;
    public static final int COLUMNA_FECHAREGISTRO = 5;
    public static final int COLUMNA_CODIGOCLIENTE = 6;
    public static final int COLUMNA_CODIGOPRODUCTO = 7;
    public static final int COLUMNA_PRECIO = 8;
    public static final int COLUMNA_IVA = 9;
    public static final int COLUMNA_CANTIDAD = 10;
    public static final int COLUMNA_TOTALNOTA = 11;


    /**
     * Determina si la aplicación corre en versiones superiores o iguales
     * a Android LOLLIPOP
     *
     * @return booleano de confirmación
     */
    public static boolean materialDesign() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Copia los datos de una nota almacenados en un cursor hacia un
     * JSONObject
     *
     * @param c cursor
     * @return objeto jason
     */
    public static JSONObject deCursorAJSONObjectNotas(Cursor c) {
        JSONObject jObject = new JSONObject();
        String ruta;
        String vendedor;
        String fechaOperacion;
        String fechaRegistro;
        String codigoClt;
        String codigoPrd;
        String precio;
        String IVA;
        String cantidad;
        String totalNota;

        ruta = c.getString(COLUMNA_RUTA);
        vendedor = c.getString(COLUMNA_VENDEDOR);
        fechaOperacion = c.getString(COLUMNA_FECHAOPERACION);
        fechaRegistro = c.getString(COLUMNA_FECHAREGISTRO);
        codigoClt = c.getString(COLUMNA_CODIGOCLIENTE);
        codigoPrd = c.getString(COLUMNA_CODIGOPRODUCTO);
        precio = c.getString(COLUMNA_PRECIO);
        IVA = c.getString(COLUMNA_IVA);
        cantidad = c.getString(COLUMNA_CANTIDAD);
        totalNota = c.getString(COLUMNA_TOTALNOTA);


        try {
            jObject.put(ContractParaDatos.ColumnasNotaRemision.RUTA, ruta);
            jObject.put(ContractParaDatos.ColumnasNotaRemision.VENDEDOR, vendedor);
            jObject.put(ContractParaDatos.ColumnasNotaRemision.FECHA_OPERACION, fechaOperacion);
            jObject.put(ContractParaDatos.ColumnasNotaRemision.FECHA_REGISTRO, fechaRegistro);
            jObject.put(ContractParaDatos.ColumnasNotaRemision.CODIGO_CLIENTE, codigoClt);
            jObject.put(ContractParaDatos.ColumnasNotaRemision.CODIGO_PRODUCTO, codigoPrd);
            jObject.put(ContractParaDatos.ColumnasNotaRemision.PRECIO, precio);
            jObject.put(ContractParaDatos.ColumnasNotaRemision.IVA, IVA);
            jObject.put(ContractParaDatos.ColumnasNotaRemision.CANTIDAD, cantidad);
            jObject.put(ContractParaDatos.ColumnasNotaRemision.TOTAL_NOTA, totalNota);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Cursor a JSONObject", String.valueOf(jObject));

        return jObject;
    }

    public static JSONObject deCursorAJSONObjectBitacora(Cursor c) {
        JSONObject jObject = new JSONObject();
        String ruta;
        String fechaHora;
        String clienteId;
        String latitud;
        String longitud;
        String conceptoRegistro;

        ruta = c.getString(COLUMNA_RUTA);
        fechaHora = c.getString(COLUMNA_VENDEDOR);
        clienteId = c.getString(COLUMNA_FECHAOPERACION);
        latitud = c.getString(COLUMNA_FECHAREGISTRO);
        longitud = c.getString(COLUMNA_CODIGOCLIENTE);
        conceptoRegistro = c.getString(COLUMNA_CODIGOPRODUCTO);

        try {
            jObject.put(ContractParaDatos.ColumnasBitacora.NUMERO_RUTA, ruta);
            jObject.put(ContractParaDatos.ColumnasBitacora.FECHAHORA, fechaHora);
            jObject.put(ContractParaDatos.ColumnasBitacora.CLIENTE_ID, clienteId);
            jObject.put(ContractParaDatos.ColumnasBitacora.LATITUD, latitud);
            jObject.put(ContractParaDatos.ColumnasBitacora.LONGITUD, longitud);
            jObject.put(ContractParaDatos.ColumnasBitacora.CONCEPTO, conceptoRegistro);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Cursor a JSONObject", String.valueOf(jObject));

        return jObject;
    }
}
