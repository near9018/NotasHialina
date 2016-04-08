package com.example.ok4r_.notashialina.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ok4r_.notashialina.modelo.DiasRuta;
import com.example.ok4r_.notashialina.modelo.NumeroRuta;
import com.example.ok4r_.notashialina.modelo.Ruta;
import com.google.gson.Gson;
import com.example.ok4r_.notashialina.R;
import com.example.ok4r_.notashialina.provider.ContractParaDatos;
import com.example.ok4r_.notashialina.utils.Constantes;
import com.example.ok4r_.notashialina.utils.Utilidades;
import com.example.ok4r_.notashialina.modelo.Cliente;
import com.example.ok4r_.notashialina.modelo.Producto;
import com.example.ok4r_.notashialina.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maneja la transferencia de datos entre el servidor y el cliente
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();
    Context context;
    ContentResolver resolver;
    private Gson gson = new Gson();

    /**
     * Proyección para las consultas
     */
    private static final String[] PROJECTION_CLIENTES = new String[]{
            ContractParaDatos.ColumnasCliente.ID_CLIENTES,
            ContractParaDatos.ColumnasCliente.ID_REMOTA,
            ContractParaDatos.ColumnasCliente.NOMBRE,
            ContractParaDatos.ColumnasCliente.DIRECCION,
            ContractParaDatos.ColumnasCliente.TELEFONO,
            ContractParaDatos.ColumnasCliente.CONTACTO,
            ContractParaDatos.ColumnasCliente.EMAIL,
            ContractParaDatos.ColumnasCliente.RFC,
            ContractParaDatos.ColumnasCliente.DESCUENTO
    };
    private static final String[] PROJECTION_PRODUCTOS = new String[]{
            ContractParaDatos.ColumnasProducto.ID_PRODUCTOS,
            ContractParaDatos.ColumnasProducto.ID_REMOTA,
            ContractParaDatos.ColumnasProducto.PRODUCTO,
            ContractParaDatos.ColumnasProducto.PRECIO,
            ContractParaDatos.ColumnasProducto.IVA
    };
    private static final String[] PROJECTION_RUTAS = new String[]{
            ContractParaDatos.ColumnasRuta.ID_RUTAS,
            ContractParaDatos.ColumnasRuta.ID_REMOTA,
            ContractParaDatos.ColumnasRuta.NUMERO_RUTA,
            ContractParaDatos.ColumnasRuta.DIA,
            ContractParaDatos.ColumnasRuta.SECUENCIA,
            ContractParaDatos.ColumnasRuta.CLIENTE
    };

    private static final String[] PROJECTION_NOTAS = new String[]{
            ContractParaDatos.ColumnasNotaRemision.ID_NOTA,
            ContractParaDatos.ColumnasNotaRemision.ID_REMOTA,
            ContractParaDatos.ColumnasNotaRemision.RUTA,
            ContractParaDatos.ColumnasNotaRemision.VENDEDOR,
            ContractParaDatos.ColumnasNotaRemision.FECHA_OPERACION,
            ContractParaDatos.ColumnasNotaRemision.FECHA_REGISTRO,
            ContractParaDatos.ColumnasNotaRemision.CODIGO_CLIENTE,
            ContractParaDatos.ColumnasNotaRemision.CODIGO_PRODUCTO,
            ContractParaDatos.ColumnasNotaRemision.PRECIO,
            ContractParaDatos.ColumnasNotaRemision.IVA,
            ContractParaDatos.ColumnasNotaRemision.CANTIDAD,
            ContractParaDatos.ColumnasNotaRemision.TOTAL_NOTA
    };

    private static final String[] PROJECTION_BITACORA = new String[]{
            ContractParaDatos.ColumnasBitacora.ID_BITACORA,
            ContractParaDatos.ColumnasBitacora.ID_REMOTA,
            ContractParaDatos.ColumnasBitacora.NUMERO_RUTA,
            ContractParaDatos.ColumnasBitacora.FECHAHORA,
            ContractParaDatos.ColumnasBitacora.CLIENTE_ID,
            ContractParaDatos.ColumnasBitacora.LATITUD,
            ContractParaDatos.ColumnasBitacora.LONGITUD,
            ContractParaDatos.ColumnasBitacora.CONCEPTO,

    };
    private static final String[] PROJECTION_NUMERORUTAS = new String[]{
            ContractParaDatos.ColumnasNumeroRutas.ID_NUMERORUTA,
            ContractParaDatos.ColumnasNumeroRutas.ID_REMOTA,
    };

    //Indices comunes
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 1;

    // Indices para las columnas de la tabla clientes indicadas en la proyección
    public static final int COLUMNA_NOMBRE = 2;
    public static final int COLUMNA_DIRECCION = 3;
    public static final int COLUMNA_TELEFONO = 4;
    public static final int COLUMNA_CONTACTO = 5;
    public static final int COLUMNA_EMAIL = 6;
    public static final int COLUMNA_RFC = 7;
    public static final int COLUMNA_DESCUENTO = 8;

    // Indices para las columnas de la tabla productos  indicadas en la proyección
    public static final int COLUMNA_PRODUCTO = 2;
    public static final int COLUMNA_PRECIO = 3;
    public static final int COLUMNA_IVA = 4;

    //Indices para las columnas de la tabla rutas indicadas en la proyeccion
    public static final int COLUMNA_No_RUTA = 2;
    public static final int COLUMNA_DIA = 3;
    public static final int COLUMNA_SECUENCIA = 4;
    public static final int COLUMNA_CLIENTE = 5;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        resolver = context.getContentResolver();
    }

    /**
     * Constructor para mantener compatibilidad en versiones inferiores a 3.0
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        resolver = context.getContentResolver();
    }

    public static Account inicializarSyncAdapter(Context context) {
        return obtenerCuentaASincronizar(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, final SyncResult syncResult)
    {
        Log.i(TAG, "onPerformSync()...");
        boolean soloSubida = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        boolean numeroRutas = extras.getBoolean(ContractParaDatos.NUMERO_RUTA, false);
        boolean diaRutas = extras.getBoolean(ContractParaDatos.DIA_RUTA, false);
        boolean ruta = extras.getBoolean(ContractParaDatos.RUTA, false);
        if (numeroRutas)
            realizarSincronizacionLocalNumerosRuta(syncResult);
        else if (diaRutas)
            realizarSincronizacionLocalDiasRuta(syncResult, (String) extras.get("RUTA"));
        else if (ruta) realizarSincronizacionLocalRutas(syncResult, (String) extras.get("RUTA"), (String) extras.get("DIA"));
        else realizarSincronizacionRemotaNotas();
    }
    private void realizarSincronizacionLocalRutas(final SyncResult syncResult, String ruta, String dia)
    {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("numeroRuta", ruta);
            jObject.put("diaSemana", dia);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.i("Datos a JSONObject", String.valueOf(jObject));
        Log.i(TAG, "Actualizando ruta");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.GET_RUTAS_URL,
                        jObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.i(TAG, response.toString());
                                procesarRespuestaGet(response, syncResult, Constantes.RUTA);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }
    private void realizarSincronizacionLocalClientes(final SyncResult syncResult, Cursor cL)
    {
        JSONObject jObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        int x=0;
        while(cL.moveToNext())
        {
            jsonArray.put( cL.getInt(0) );
        }
        try
        {
            jObject.put("idCliente",jsonArray);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        //Log.i("Datos a JSONObject", String.valueOf(jObject));
        Log.i(TAG, "Actualizando clientes");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.GET_CLIENTES_URL,
                        jObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.i(TAG, response.toString());
                                procesarRespuestaGet(response, syncResult, Constantes.CLIENTES);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }

    private void realizarSincronizacionLocalProductos(final SyncResult syncResult)
    {
        Log.i(TAG, "Actualizando productos");
        JsonObjectRequest requestProductos = new JsonObjectRequest(
                Request.Method.GET,
                Constantes.GET_PRODUCTOS_URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        procesarRespuestaGet(response, syncResult, Constantes.PRODUCTOS);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d(TAG, error.networkResponse.toString());
                        Log.d(TAG, "ERROR");
                    }
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(requestProductos);
    }

    private void realizarSincronizacionLocalNumerosRuta(final SyncResult syncResult)
    {
        Log.i(TAG, "Actualizando los numeros de rutas");
        JsonObjectRequest requestProductos = new JsonObjectRequest(
                Request.Method.GET,
                Constantes.GET_RUTAS_URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        procesarRespuestaGet(response, syncResult, "numerosRutas");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d(TAG, error.networkResponse.toString());
                        Log.d(TAG, "ERROR");
                    }
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(requestProductos);
    }
    private void realizarSincronizacionLocalDiasRuta(final SyncResult syncResult, String ruta)
    {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("numeroRuta", ruta);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.i("Datos a JSONObject", String.valueOf(jObject));
        Log.i(TAG, "Actualizando los dias de la ruta");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.GET_DIASRUTA_URL,
                        jObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.i(TAG, response.toString());
                                procesarRespuestaGet(response, syncResult, "diasRuta");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }
    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los clientes.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGet(JSONObject response, SyncResult syncResult, String tabla)
    {
        try
        {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);
            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    if (tabla.equals(Constantes.CLIENTES))
                        actualizarDatosLocalesClientes(response, syncResult);
                    else if (tabla.equals(Constantes.PRODUCTOS))
                        actualizarDatosLocalesProductos(response, syncResult);
                    else if (tabla.equals(Constantes.RUTA))
                        actualizarDatosLocalesRutas(response, syncResult);
                    else if (tabla.equals("numerosRutas"))
                        actualizarDatosLocalesNumerosRutas(response, syncResult);
                    else if (tabla.equals("diasRuta"))
                        actualizarDatosLocalesDiasRuta(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void realizarSincronizacionRemotaNotas() {
        Log.i(TAG, "Actualizando las notas en el servidor...");

        iniciarActualizacionNotas();

        Cursor c = obtenerRegistrosSuciosNotas();

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros sucios.");

        if (c.getCount() > 0) {
            while (c.moveToNext())
            {
                final int idLocal = c.getInt(COLUMNA_ID);
                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_NOTA_URL,
                                Utilidades.deCursorAJSONObjectNotas(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        System.out.println(response.toString());
                                        procesarRespuestaInsertNotas(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        System.out.println(error.toString());
                                    }
                                }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }
        } else
        {
            Log.i(TAG, "No se requiere sincronización");
        }
        c.close();
        realizarSincronizacionRemotaBitacora();
    }
    private void realizarSincronizacionRemotaBitacora() {
        Log.i(TAG, "Actualizando la Bitacora en el servidor...");

        iniciarActualizacionBitacora();

        Cursor c = obtenerRegistrosSuciosBitacora();

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros sucios.");

        if (c.getCount() > 0) {
            while (c.moveToNext())
            {
                final int idLocal = c.getInt(COLUMNA_ID);
                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_BITACORA_URL,
                                Utilidades.deCursorAJSONObjectBitacora(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        System.out.println(response.toString());
                                        procesarRespuestaInsertBitacora(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        System.out.println(error.toString());
                                    }
                                }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }
        }
        else
        {
            Log.i(TAG, "No se requiere sincronización");
        }
        c.close();
    }
    /**
     * Obtiene el registro que se acaba de marcar como "pendiente por sincronizar" y
     * con "estado de sincronización"
     *
     * @return Cursor con el registro.
     */
    private Cursor obtenerRegistrosSuciosNotas() {
        Uri uri = ContractParaDatos.CONTENT_URI_NOTAREMISION;
        String selection = ContractParaDatos.ColumnasNotaRemision.PENDIENTE_INSERCION + " =? AND "
                + ContractParaDatos.ColumnasNotaRemision.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1", ContractParaDatos.ESTADO_SYNC + ""};

        return resolver.query(uri, PROJECTION_NOTAS, selection, selectionArgs, null);
    }
    private Cursor obtenerRegistrosSuciosBitacora() {
        Uri uri = ContractParaDatos.CONTENT_URI_BITACORA;
        String selection = ContractParaDatos.ColumnasBitacora.PENDIENTE_INSERCION + " =? AND "
                + ContractParaDatos.ColumnasBitacora.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1", ContractParaDatos.ESTADO_SYNC + ""};

        return resolver.query(uri, PROJECTION_BITACORA, selection, selectionArgs, null);
    }
    /**
     * Cambia a estado "de sincronización" el registro que se acaba de insertar localmente
     */
    private void iniciarActualizacionNotas() {
        Uri uri = ContractParaDatos.CONTENT_URI_NOTAREMISION;
        String selection = ContractParaDatos.ColumnasNotaRemision.PENDIENTE_INSERCION + " =? AND "
                + ContractParaDatos.ColumnasNotaRemision.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1", ContractParaDatos.ESTADO_OK + ""};

        ContentValues v = new ContentValues();
        v.put(ContractParaDatos.ColumnasNotaRemision.ESTADO, ContractParaDatos.ESTADO_SYNC);

        int results = resolver.update(uri, v, selection, selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción(Notas):" + results);
    }
    private void iniciarActualizacionBitacora() {
        Uri uri = ContractParaDatos.CONTENT_URI_BITACORA;
        String selection = ContractParaDatos.ColumnasBitacora.PENDIENTE_INSERCION + " =? AND "
                + ContractParaDatos.ColumnasBitacora.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1", ContractParaDatos.ESTADO_OK + ""};

        ContentValues v = new ContentValues();
        v.put(ContractParaDatos.ColumnasBitacora.ESTADO, ContractParaDatos.ESTADO_SYNC);

        int results = resolver.update(uri, v, selection, selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción(Bitacora):" + results);
    }

    /**
     * Limpia el registro que se sincronizó y le asigna la nueva id remota proveida
     * por el servidor
     *
     * @param idRemota id remota
     */
    private void finalizarActualizacionNotas(String idRemota, int idLocal) {
        Uri uri = ContractParaDatos.CONTENT_URI_NOTAREMISION;
        String selection = ContractParaDatos.ColumnasNotaRemision.ID_NOTA + "=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContractParaDatos.ColumnasNotaRemision.PENDIENTE_INSERCION, "0");
        v.put(ContractParaDatos.ColumnasNotaRemision.ESTADO, ContractParaDatos.ESTADO_OK);
        v.put(ContractParaDatos.ColumnasNotaRemision.ID_REMOTA, idRemota);

        resolver.update(uri, v, selection, selectionArgs);
    }
    private void finalizarActualizacionBitacora(String idRemota, int idLocal) {
        Uri uri = ContractParaDatos.CONTENT_URI_BITACORA;
        String selection = ContractParaDatos.ColumnasBitacora.ID_BITACORA + "=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContractParaDatos.ColumnasBitacora.PENDIENTE_INSERCION, "0");
        v.put(ContractParaDatos.ColumnasBitacora.ESTADO, ContractParaDatos.ESTADO_OK);
        v.put(ContractParaDatos.ColumnasBitacora.ID_REMOTA, idRemota);

        resolver.update(uri, v, selection, selectionArgs);
    }
    /**
     * Procesa los diferentes tipos de respuesta obtenidos del servidor
     *
     * @param response Respuesta en formato Json
     */
    public void procesarRespuestaInsertNotas(JSONObject response, int idLocal) {

        try {
            // Obtener estado
            String estado = response.getString(Constantes.ESTADO);
            // Obtener mensaje
            String mensaje = response.getString(Constantes.MENSAJE);


            switch (estado) {
                case Constantes.SUCCESS:
                    Log.i(TAG, mensaje);
                    // Obtener identificador del nuevo registro creado en el servidor
                    String idRemota = response.getString(Constantes.ID_NOTA);
                    finalizarActualizacionNotas(idRemota, idLocal);
                    break;

                case Constantes.FAILED:
                    Log.i(TAG, mensaje);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void procesarRespuestaInsertBitacora(JSONObject response, int idLocal) {

        try {
            // Obtener estado
            String estado = response.getString(Constantes.ESTADO);
            // Obtener mensaje
            String mensaje = response.getString(Constantes.MENSAJE);


            switch (estado) {
                case Constantes.SUCCESS:
                    Log.i(TAG, mensaje);
                    // Obtener identificador del nuevo registro creado en el servidor
                    String idRemota = response.getString(Constantes.ID_BITACORA);
                    finalizarActualizacionBitacora(idRemota, idLocal);
                    break;

                case Constantes.FAILED:
                    Log.i(TAG, mensaje);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    private void actualizarDatosLocalesProductos(JSONObject response, SyncResult syncResult)
    {
        JSONArray productos = null;
        try
        {
            // Obtener array "productos"
            productos = response.getJSONArray(Constantes.PRODUCTOS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        Producto[] res = gson.fromJson(productos != null ? productos.toString() : null, Producto[].class);
        List<Producto> data = Arrays.asList(res);
        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, Producto> expenseMap = new HashMap<String, Producto>();
        for (Producto e : data) {
            expenseMap.put(e.idProducto, e);
        }

        // Consultar registros remotos actuales
        Uri uri = ContractParaDatos.CONTENT_URI_PRODUCTO;
        String select = ContractParaDatos.ColumnasProducto.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION_PRODUCTOS, select, null, null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");
        // Encontrar datos obsoletos
        String id;
        String nombre;
        Double precio;
        Double iva;

        while (c.moveToNext())
        {
            syncResult.stats.numEntries++;
            id = c.getString(COLUMNA_ID_REMOTA);
            nombre = c.getString(COLUMNA_PRODUCTO);
            precio = c.getDouble(COLUMNA_PRECIO);
            iva = c.getDouble(COLUMNA_IVA);
            Producto match = expenseMap.get(id);
            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(id);
                Uri existingUri = ContractParaDatos.CONTENT_URI_PRODUCTO.buildUpon().appendPath(id).build();
                // Comprobar si el producto necesita ser actualizado
                boolean b = match.nombrePrd != null && !match.nombrePrd.equals(nombre);
                boolean b1 = match.precioPrd != precio;
                boolean b2 = match.IVA != iva;

                if (b || b1 || b2)
                {
                    Log.i(TAG, "Programando actualización de: " + existingUri);
                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaDatos.ColumnasProducto.PRODUCTO, match.nombrePrd)
                            .withValue(ContractParaDatos.ColumnasProducto.PRECIO, match.precioPrd)
                            .withValue(ContractParaDatos.ColumnasProducto.IVA, match.IVA)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaDatos.CONTENT_URI_PRODUCTO.buildUpon()
                        .appendPath(id).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();
        // Insertar items resultantes
        for (Producto e : expenseMap.values())
        {
            Log.i(TAG, "Programando inserción de: " + e.idProducto);
            ops.add(ContentProviderOperation.newInsert(ContractParaDatos.CONTENT_URI_PRODUCTO)
                    .withValue(ContractParaDatos.ColumnasProducto.ID_REMOTA, e.idProducto)
                    .withValue(ContractParaDatos.ColumnasProducto.PRODUCTO, e.nombrePrd)
                    .withValue(ContractParaDatos.ColumnasProducto.PRECIO, e.precioPrd)
                    .withValue(ContractParaDatos.ColumnasProducto.IVA, e.IVA)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try
            {
                resolver.applyBatch(ContractParaDatos.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaDatos.CONTENT_URI_PRODUCTO,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }
    }

    private void actualizarDatosLocalesNumerosRutas(JSONObject response, SyncResult syncResult)
    {
        JSONArray numeroRutas = null;
        try
        {
            // Obtener array "productos"
            numeroRutas = response.getJSONArray(Constantes.RUTA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        NumeroRuta[] res = gson.fromJson(numeroRutas != null ? numeroRutas.toString() : null, NumeroRuta[].class);
        List<NumeroRuta> data = Arrays.asList(res);
        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, NumeroRuta> expenseMap = new HashMap<String, NumeroRuta>();
        for (NumeroRuta e : data) {
            expenseMap.put(e.numeroRuta, e);
        }

        // Consultar registros remotos actuales
        Uri uri = ContractParaDatos.CONTENT_URI_NUMERORUTA;
        String select = ContractParaDatos.ColumnasNumeroRutas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION_NUMERORUTAS, select, null, null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");
        // Encontrar datos obsoletos
        String id;

        while (c.moveToNext())
        {
            syncResult.stats.numEntries++;
            id = c.getString(COLUMNA_ID_REMOTA);

            NumeroRuta match = expenseMap.get(id);
             if (match==null)
            {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaDatos.CONTENT_URI_NUMERORUTA.buildUpon()
                        .appendPath(id).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }else
             {
                 expenseMap.remove(id);
             }

        }
        c.close();
        // Insertar items resultantes
        for (NumeroRuta e : expenseMap.values())
        {
            Log.i(TAG, "Programando inserción de: " + e.numeroRuta);
            ops.add(ContentProviderOperation.newInsert(ContractParaDatos.CONTENT_URI_NUMERORUTA)
                    .withValue(ContractParaDatos.ColumnasProducto.ID_REMOTA, e.numeroRuta)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try
            {
                resolver.applyBatch(ContractParaDatos.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaDatos.CONTENT_URI_NUMERORUTA,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }
    }
    private void actualizarDatosLocalesDiasRuta(JSONObject response, SyncResult syncResult)
    {
        JSONArray diasRuta = null;
        try
        {
            // Obtener array "productos"
            diasRuta = response.getJSONArray(Constantes.RUTA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        DiasRuta[] res = gson.fromJson(diasRuta != null ? diasRuta.toString() : null, DiasRuta[].class);
        List<DiasRuta> data = Arrays.asList(res);
        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, DiasRuta> expenseMap = new HashMap<String, DiasRuta>();
        for (DiasRuta e : data) {
            expenseMap.put(e.diaSemana, e);
        }

        Uri deleteUri = ContractParaDatos.CONTENT_URI_DIARUTA;
        Log.i(TAG, "Programando eliminación de: " + deleteUri);
        ops.add(ContentProviderOperation.newDelete(deleteUri).build());
        syncResult.stats.numDeletes++;
        //c.close();
        // Insertar items resultantes
        for (DiasRuta e : expenseMap.values())
        {
            Log.i(TAG, "Programando inserción de: " + e.diaSemana);
            ops.add(ContentProviderOperation.newInsert(ContractParaDatos.CONTENT_URI_DIARUTA)
                    .withValue(ContractParaDatos.ColumnasDiaRutas.ID_REMOTA, e.diaSemana)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try
            {
                resolver.applyBatch(ContractParaDatos.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaDatos.CONTENT_URI_DIARUTA,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }
    }
    private void actualizarDatosLocalesRutas(JSONObject response, SyncResult syncResult)
    {
        JSONArray ruta = null;
        try
        {
            // Obtener array "ruta"
            ruta = response.getJSONArray(Constantes.RUTA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        Ruta[] res = gson.fromJson(ruta != null ? ruta.toString() : null, Ruta[].class);
        List<Ruta> data = Arrays.asList(res);
        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, Ruta> expenseMap = new HashMap<String, Ruta>();
        for (Ruta e : data) {
            expenseMap.put(e.rutasID, e);
        }

        // Consultar registros remotos actuales
        Uri uri = ContractParaDatos.CONTENT_URI_RUTA;
        String select = ContractParaDatos.ColumnasRuta.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION_RUTAS, select, null, null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");
        // Encontrar datos obsoletos
        String id;
        int No_ruta;
        String dia;
        int secuencia;
        int cliente;

        while (c.moveToNext())
        {
            syncResult.stats.numEntries++;
            id = c.getString(COLUMNA_ID_REMOTA);
            No_ruta = c.getInt(COLUMNA_No_RUTA);
            dia = c.getString(COLUMNA_DIA);
            secuencia = c.getInt(COLUMNA_SECUENCIA);
            cliente = c.getInt(COLUMNA_CLIENTE);
            Ruta match = expenseMap.get(id);
            if (match != null)
            {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(id);
                Uri existingUri = ContractParaDatos.CONTENT_URI_RUTA.buildUpon().appendPath(id).build();
                // Comprobar si el producto necesita ser actualizado
                boolean b1 = match.numeroRuta != No_ruta;
                boolean b = match.diaSemana != null && !match.diaSemana.equals(dia);
                boolean b2 = match.secuenciaVisita != secuencia;
                boolean b3 = match.codigoClt != cliente;

                if (b || b1 || b2 || b3)
                {
                    Log.i(TAG, "Programando actualización de: " + existingUri);
                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaDatos.ColumnasRuta.NUMERO_RUTA, match.numeroRuta)
                            .withValue(ContractParaDatos.ColumnasRuta.DIA, match.diaSemana)
                            .withValue(ContractParaDatos.ColumnasRuta.SECUENCIA, match.secuenciaVisita)
                            .withValue(ContractParaDatos.ColumnasRuta.CLIENTE, match.codigoClt)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaDatos.CONTENT_URI_RUTA.buildUpon()
                        .appendPath(id).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();
        // Insertar items resultantes
        for (Ruta e : expenseMap.values())
        {
            Log.i(TAG, "Programando inserción de: " + e.rutasID);
            ops.add(ContentProviderOperation.newInsert(ContractParaDatos.CONTENT_URI_RUTA)
                    .withValue(ContractParaDatos.ColumnasRuta.ID_REMOTA, e.rutasID)
                    .withValue(ContractParaDatos.ColumnasRuta.NUMERO_RUTA, e.numeroRuta)
                    .withValue(ContractParaDatos.ColumnasRuta.DIA, e.diaSemana)
                    .withValue(ContractParaDatos.ColumnasRuta.SECUENCIA, e.secuenciaVisita)
                    .withValue(ContractParaDatos.ColumnasRuta.CLIENTE, e.codigoClt)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try
            {
                resolver.applyBatch(ContractParaDatos.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(ContractParaDatos.CONTENT_URI_RUTA, null, false);
            Log.i(TAG, "Sincronización finalizada.");
        } else {
            Log.i(TAG, "No se requiere sincronización");
        }
        String [] clientes= new String[]{ContractParaDatos.ColumnasRuta.CLIENTE};
        Cursor cL = resolver.query(ContractParaDatos.CONTENT_URI_RUTA, clientes, null, null, null);
        realizarSincronizacionLocalClientes(syncResult, cL);
    }
    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    private void actualizarDatosLocalesClientes(JSONObject response, SyncResult syncResult)
    {
        JSONArray clientes = null;
        try {
            // Obtener array "clientes"
            clientes = response.getJSONArray(Constantes.CLIENTES);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        Cliente[] res = gson.fromJson(clientes != null ? clientes.toString() : null, Cliente[].class);
        List<Cliente> data = Arrays.asList(res);
        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, Cliente> expenseMap = new HashMap<String, Cliente>();
        for (Cliente e : data) {
            expenseMap.put(e.idCliente, e);
        }

        // Consultar registros remotos actuales
        Uri uri = ContractParaDatos.CONTENT_URI_CLIENTE;
        String select = ContractParaDatos.ColumnasCliente.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION_CLIENTES, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String id;
        String nombre;
        String direccion;
        String telefono;
        String contacto;
        String email;
        String rfc;
        Double descuento;
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_ID_REMOTA);
            nombre = c.getString(COLUMNA_NOMBRE);
            direccion = c.getString(COLUMNA_DIRECCION);
            telefono = c.getString(COLUMNA_TELEFONO);
            contacto = c.getString(COLUMNA_CONTACTO);
            email = c.getString(COLUMNA_EMAIL);
            rfc = c.getString(COLUMNA_RFC);
            descuento = c.getDouble(COLUMNA_DESCUENTO);
            Cliente match = expenseMap.get(id);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(id);

                Uri existingUri = ContractParaDatos.CONTENT_URI_CLIENTE.buildUpon()
                        .appendPath(id).build();

                // Comprobar si el gasto necesita ser actualizado
                boolean b = match.nombreClt != null && !match.nombreClt.equals(nombre);
                boolean b1 = match.direccionClt != null && !match.direccionClt.equals(direccion);
                boolean b2 = match.telefonoClt != null && !match.telefonoClt.equals(telefono);
                boolean b3 = match.contactoClt != null && !match.contactoClt.equals(contacto);
                boolean b4 = match.emailClt != null && !match.emailClt.equals(email);
                boolean b5 = match.rfcClt != null && !match.rfcClt.equals(rfc);
                boolean b6 = match.descuento != descuento;

                if (b || b1 || b2 || b3 || b4 || b5 || b6) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractParaDatos.ColumnasCliente.NOMBRE, match.nombreClt)
                            .withValue(ContractParaDatos.ColumnasCliente.DIRECCION, match.direccionClt)
                            .withValue(ContractParaDatos.ColumnasCliente.TELEFONO, match.telefonoClt)
                            .withValue(ContractParaDatos.ColumnasCliente.CONTACTO, match.contactoClt)
                            .withValue(ContractParaDatos.ColumnasCliente.EMAIL, match.emailClt)
                            .withValue(ContractParaDatos.ColumnasCliente.RFC, match.rfcClt)
                            .withValue(ContractParaDatos.ColumnasCliente.DESCUENTO, match.descuento)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractParaDatos.CONTENT_URI_CLIENTE.buildUpon()
                        .appendPath(id).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (Cliente e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de: " + e.idCliente);
            ops.add(ContentProviderOperation.newInsert(ContractParaDatos.CONTENT_URI_CLIENTE)
                    .withValue(ContractParaDatos.ColumnasCliente.ID_REMOTA, e.idCliente)
                    .withValue(ContractParaDatos.ColumnasCliente.NOMBRE, e.nombreClt)
                    .withValue(ContractParaDatos.ColumnasCliente.DIRECCION, e.direccionClt)
                    .withValue(ContractParaDatos.ColumnasCliente.TELEFONO, e.telefonoClt)
                    .withValue(ContractParaDatos.ColumnasCliente.CONTACTO, e.contactoClt)
                    .withValue(ContractParaDatos.ColumnasCliente.EMAIL, e.emailClt)
                    .withValue(ContractParaDatos.ColumnasCliente.RFC, e.rfcClt)
                    .withValue(ContractParaDatos.ColumnasCliente.DESCUENTO, e.descuento)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(ContractParaDatos.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractParaDatos.CONTENT_URI_CLIENTE,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }
        realizarSincronizacionLocalProductos(syncResult);
    }
    /**
     * Inicia manualmente la sincronización
     *
     * @param context    Contexto para crear la petición de sincronización
     */
    public static void sincronizarAhora(Context context, String ruta, String dia)
    {
        Log.i(TAG, "Realizando petición de sincronización manual.");
        Bundle bundle = new Bundle();
        bundle.putString("DIA", dia);
        bundle.putString("RUTA", ruta);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
        bundle.putBoolean(ContractParaDatos.RUTA, true);
        ContentResolver.requestSync(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_authority), bundle);
    }

    public static void sincronizarAhora(Context context, boolean onlyUpload, boolean rutas)
    {
        Log.i(TAG, "Realizando petición de sincronización manual.");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContractParaDatos.NUMERO_RUTA, rutas);
        if (onlyUpload)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        ContentResolver.requestSync(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_authority), bundle);
    }
    public static void sincronizacionAutomatica(Context context)
    {
        Log.i(TAG, "Realizando petición de sincronización automatica.");
        Bundle bundle = new Bundle();
        //bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        //bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        //bundle.putBoolean(ContractParaDatos.NUMERO_RUTA, false);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        //ContentResolver.addPeriodicSync(obtenerCuentaASincronizar(context),
        //       context.getString(R.string.provider_authority), bundle, 60);
        ContentResolver.setSyncAutomatically(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_authority),true);
        boolean syncAutomatic = ContentResolver.getSyncAutomatically(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_authority));
        Log.i("Sync Aut: ",""+syncAutomatic);
    }
    public static void sincronizarAhora(Context context, boolean onlyUpload, String ruta)
    {
        Log.i(TAG, "Realizando petición de sincronización manual.");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString("RUTA", ruta);
        bundle.putBoolean(ContractParaDatos.DIA_RUTA, true);
        if (onlyUpload)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        ContentResolver.requestSync(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_authority), bundle);
    }
    /**
     * Crea u obtiene una cuenta existente
     *
     * @param context Contexto para acceder al administrador de cuentas
     * @return cuenta auxiliar.
     */
    public static Account obtenerCuentaASincronizar(Context context) {
        // Obtener instancia del administrador de cuentas
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        // Crear cuenta por defecto
        Account newAccount = new Account(context.getString(R.string.app_name), Constantes.ACCOUNT_TYPE);
        // Comprobar existencia de la cuenta
        if (null == accountManager.getPassword(newAccount))
        {
            // Añadir la cuenta al account manager sin password y sin datos de usuario
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;
        }
        Log.i(TAG, "Cuenta de usuario obtenida.");
        return newAccount;
    }

}