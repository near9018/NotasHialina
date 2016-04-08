package com.example.ok4r_.notashialina.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * Content Provider personalizado para los gastos
 */
public class ProviderDeDatos extends ContentProvider {
    /**
     * Nombre de la base de datos
     */
    private static final String TAG = ProviderDeDatos.class.getSimpleName();
    private static final String DATABASE_NAME = "bd_hialina.db";
    private static final String RUTAS_JOIN_CLIENTES = " ruta INNER JOIN cliente ON " +
            "ruta.clienteId = cliente.idRemota";
    private static final String NOTAS_JOINCLIENTES_JOINPRODUCTOS = "nota INNER JOIN  cliente ON " +
            "nota.codigoClt = cliente.idRemota INNER JOIN producto ON " +
            "nota.codigoPrd = producto.idRemota";

    private final String[] proyRutasClientes = new String[]{
            ContractParaDatos.RUTA + "." + ContractParaDatos.ColumnasRuta.SECUENCIA,
            ContractParaDatos.CLIENTE + "." + ContractParaDatos.ColumnasCliente.NOMBRE,
            ContractParaDatos.CLIENTE + "." + ContractParaDatos.ColumnasCliente.DIRECCION,
            ContractParaDatos.CLIENTE + "." + ContractParaDatos.ColumnasCliente.ID_REMOTA};
    private final String[] proyNotasClientesProductos = new String[]{
            ContractParaDatos.NOTA_REMISION + "." + ContractParaDatos.ColumnasNotaRemision.FECHA_OPERACION,
            ContractParaDatos.CLIENTE + "." + ContractParaDatos.ColumnasCliente.NOMBRE,
            ContractParaDatos.PRODUCTO + "." + ContractParaDatos.ColumnasProducto.PRODUCTO,
            ContractParaDatos.CLIENTE + "." + ContractParaDatos.ColumnasCliente.ID_REMOTA,
            ContractParaDatos.NOTA_REMISION + "." + ContractParaDatos.ColumnasNotaRemision.PRECIO,
            ContractParaDatos.NOTA_REMISION + "." + ContractParaDatos.ColumnasNotaRemision.CANTIDAD,
            ContractParaDatos.NOTA_REMISION + "." + ContractParaDatos.ColumnasNotaRemision.TOTAL_NOTA};
    /**
     * Versión actual de la base de datos
     */
    private static final int DATABASE_VERSION = 17;
    /**
     * Instancia global del Content Resolver
     */
    private ContentResolver resolver;
    /**
     * Instancia del administrador de BD
     */
    private DatabaseHelper databaseHelper;

    @Override
    public boolean onCreate()
    {
        // Inicializando gestor BD
        databaseHelper = new DatabaseHelper(
                getContext(),
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );

        resolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        // Obtener base de datos
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        // Comparar Uri
        int match = ContractParaDatos.uriMatcher.match(uri);
        System.out.println(match);
        Cursor c;
        switch (match) {
            case ContractParaDatos.RUTAS_CLIENTES:
                builder.setTables(RUTAS_JOIN_CLIENTES);
                c = builder.query(db, proyRutasClientes,
                        null, null, null, null,
                        ContractParaDatos.ColumnasRuta.SECUENCIA);
                c.setNotificationUri(resolver, ContractParaDatos.CONTENT_URI_CLIENTE);
                break;
            case ContractParaDatos.NOTAS_CLIENTES_PRODUCTOS:
                builder.setTables(NOTAS_JOINCLIENTES_JOINPRODUCTOS);
                c = builder.query(db, proyNotasClientesProductos,
                        null, null, null, null,
                        null);
                c.setNotificationUri(resolver, ContractParaDatos.CONTENT_URI_NOTAREMISION);
                break;
            case ContractParaDatos.CLIENTES:
                // Consultando todos los registros
                c = db.query(ContractParaDatos.CLIENTE, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, ContractParaDatos.CONTENT_URI_CLIENTE);
                break;
            case ContractParaDatos.NUMERO_RUTAS:
                // Consultando todos los registros
                c = db.query(ContractParaDatos.NUMERO_RUTA, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, ContractParaDatos.CONTENT_URI_NUMERORUTA);
                break;
            case ContractParaDatos.DIA_RUTAS:
                // Consultando todos los registros
                c = db.query(ContractParaDatos.DIA_RUTA, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, ContractParaDatos.CONTENT_URI_DIARUTA);
                break;
            case ContractParaDatos.PRODUCTOS:
                // Consultando todos los registros
                c = db.query(ContractParaDatos.PRODUCTO, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, ContractParaDatos.CONTENT_URI_PRODUCTO);
                break;
            case ContractParaDatos.RUTAS:
                // Consultando todos los registros
                c = db.query(ContractParaDatos.RUTA, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, ContractParaDatos.CONTENT_URI_RUTA);
                break;
            case ContractParaDatos.BITACORAS:
                // Consultando todos los registros
                c = db.query(ContractParaDatos.BITACORA, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, ContractParaDatos.CONTENT_URI_BITACORA);
                break;
            case ContractParaDatos.NOTAS_REMISION:
                // Consultando todos los registros
                c = db.query(ContractParaDatos.NOTA_REMISION, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, ContractParaDatos.CONTENT_URI_NOTAREMISION);
                break;
            case ContractParaDatos.CLIENTES_ID:
                // Consultando un solo registro basado en el Id del Uri
                long idCliente = ContentUris.parseId(uri);
                c = db.query(ContractParaDatos.CLIENTE, projection,
                        ContractParaDatos.ColumnasCliente.ID_CLIENTES + " = " + idCliente,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, ContractParaDatos.CONTENT_URI_CLIENTE);
                break;
            case ContractParaDatos.PRODUCTOS_ID:
                 // Consultando un solo registro basado en el Id del Uri
                 long idProducto = ContentUris.parseId(uri);
                 c = db.query(ContractParaDatos.PRODUCTO, projection,
                         ContractParaDatos.ColumnasProducto.ID_PRODUCTOS + " = " + idProducto,
                         selectionArgs, null, null, sortOrder);
                 c.setNotificationUri(
                         resolver,
                         ContractParaDatos.CONTENT_URI_PRODUCTO);
                 break;
            case ContractParaDatos.NUMERORUTAS_ID:
                // Consultando un solo registro basado en el Id del Uri
                long idNumeroRuta = ContentUris.parseId(uri);
                c = db.query(ContractParaDatos.NUMERO_RUTA, projection,
                        ContractParaDatos.ColumnasNumeroRutas.ID_NUMERORUTA + " = " + idNumeroRuta,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaDatos.CONTENT_URI_NUMERORUTA);
                break;
            case ContractParaDatos.DIARUTAS_ID:
                // Consultando un solo registro basado en el Id del Uri
                String idDiaRuta = uri.getLastPathSegment();
                c = db.query(ContractParaDatos.DIA_RUTA, projection,
                        ContractParaDatos.ColumnasDiaRutas.ID_DIARUTA + " = " + idDiaRuta,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaDatos.CONTENT_URI_DIARUTA);
                break;
            case ContractParaDatos.RUTAS_ID:
                // Consultando un solo registro basado en el Id del Uri
                long idRuta = ContentUris.parseId(uri);
                c = db.query(ContractParaDatos.RUTA, projection,
                        ContractParaDatos.ColumnasRuta.ID_RUTAS + " = " + idRuta,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaDatos.CONTENT_URI_RUTA);
                break;
            case ContractParaDatos.BITACORAS_ID:
                // Consultando un solo registro basado en el Id del Uri
                long idBitacora = ContentUris.parseId(uri);
                c = db.query(ContractParaDatos.BITACORA, projection,
                        ContractParaDatos.ColumnasBitacora.ID_BITACORA + " = " + idBitacora,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaDatos.CONTENT_URI_BITACORA);
                break;
            case ContractParaDatos.NOTASREMISION_ID:
                // Consultando un solo registro basado en el Id del Uri
                long idNota = ContentUris.parseId(uri);
                c = db.query(ContractParaDatos.NOTA_REMISION, projection,
                        ContractParaDatos.ColumnasNotaRemision.ID_NOTA + " = " + idNota,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaDatos.CONTENT_URI_NOTAREMISION);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;

    }

    @Override
    public String getType(Uri uri)
    {
        switch (ContractParaDatos.uriMatcher.match(uri)) {
            case ContractParaDatos.CLIENTES:
                return ContractParaDatos.MULTIPLE_MIME_CLIENTE;
            case ContractParaDatos.NUMERO_RUTAS:
                return ContractParaDatos.MULTIPLE_MIME_NUMERORUTA;
            case ContractParaDatos.PRODUCTOS:
                return ContractParaDatos.MULTIPLE_MIME_PRODUCTO;
            case ContractParaDatos.RUTAS:
                return ContractParaDatos.MULTIPLE_MIME_RUTA;
            case ContractParaDatos.BITACORAS:
                return ContractParaDatos.MULTIPLE_MIME_BITACORA;
            case ContractParaDatos.NOTAS_REMISION:
                return ContractParaDatos.MULTIPLE_MIME_NOTAREMISION;
            case ContractParaDatos.DIA_RUTAS:
                return ContractParaDatos.MULTIPLE_MIME_DIARUTA;
            case ContractParaDatos.CLIENTES_ID:
                return ContractParaDatos.SINGLE_MIME_CLIENTE;
            case ContractParaDatos.PRODUCTOS_ID:
                return ContractParaDatos.SINGLE_MIME_PRODUCTO;
            case ContractParaDatos.NUMERORUTAS_ID:
                return ContractParaDatos.SINGLE_MIME_NUMERORUTA;
            case ContractParaDatos.RUTAS_ID:
                return ContractParaDatos.SINGLE_MIME_RUTA;
            case ContractParaDatos.DIARUTAS_ID:
                return ContractParaDatos.SINGLE_MIME_DIARUTA;
            case ContractParaDatos.BITACORAS_ID:
                return ContractParaDatos.SINGLE_MIME_BITACORA;
            case ContractParaDatos.NOTASREMISION_ID:
                return ContractParaDatos.SINGLE_MIME_NOTAREMISION;
            default:
                throw new IllegalArgumentException("Tipo de dato desconocido: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        // Validar la uri
        if (ContractParaDatos.uriMatcher.match(uri) != ContractParaDatos.BITACORAS &&
                ContractParaDatos.uriMatcher.match(uri) != ContractParaDatos.NOTAS_REMISION &&
                ContractParaDatos.uriMatcher.match(uri) != ContractParaDatos.CLIENTES &&
                ContractParaDatos.uriMatcher.match(uri) != ContractParaDatos.PRODUCTOS &&
                ContractParaDatos.uriMatcher.match(uri) != ContractParaDatos.RUTAS &&
                ContractParaDatos.uriMatcher.match(uri) != ContractParaDatos.NUMERO_RUTAS &&
                ContractParaDatos.uriMatcher.match(uri) != ContractParaDatos.DIA_RUTAS)
        {
            throw new IllegalArgumentException("URI desconocida : " + uri);
        }

        ContentValues contentValues;
        if (values != null)
        {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }

        // Inserción de nueva fila
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int match = ContractParaDatos.uriMatcher.match(uri);
        long rowId;
        switch (match)
        {
            case ContractParaDatos.NOTAS_REMISION:
                rowId = db.insert(ContractParaDatos.NOTA_REMISION, null, contentValues);
                if (rowId > 0)
                {
                    Uri uri_nota = ContentUris.withAppendedId(ContractParaDatos.CONTENT_URI_NOTAREMISION, rowId);
                    resolver.notifyChange(uri_nota, null, false);
                    return uri_nota;
                }
            case ContractParaDatos.NUMERO_RUTAS:
                rowId = db.insert(ContractParaDatos.NUMERO_RUTA, null, contentValues);
                if (rowId > 0)
                {
                    Uri uri_numeroruta = ContentUris.withAppendedId(ContractParaDatos.CONTENT_URI_NUMERORUTA, rowId);
                    Log.i(TAG, uri_numeroruta.toString());
                    resolver.notifyChange(uri_numeroruta, null, false);
                    return uri_numeroruta;
                }
            case ContractParaDatos.DIA_RUTAS:
                rowId = db.insert(ContractParaDatos.DIA_RUTA, null, contentValues);
                if (rowId > 0)
                {
                    Uri uri_diaruta = ContentUris.withAppendedId(ContractParaDatos.CONTENT_URI_DIARUTA, rowId);
                    Log.i(TAG, uri_diaruta.toString());
                    resolver.notifyChange(uri_diaruta, null, false);
                    return uri_diaruta;
                }
            case ContractParaDatos.BITACORAS:
                rowId = db.insert(ContractParaDatos.BITACORA, null, contentValues);
                if (rowId > 0)
                {
                    Uri uri_bitacora = ContentUris.withAppendedId(ContractParaDatos.CONTENT_URI_BITACORA, rowId);
                    resolver.notifyChange(uri_bitacora, null, false);
                    return uri_bitacora;
                }
            case ContractParaDatos.CLIENTES:
                rowId = db.insert(ContractParaDatos.CLIENTE, null, contentValues);
                if (rowId > 0)
                {
                    Uri uri_cliente = ContentUris.withAppendedId(ContractParaDatos.CONTENT_URI_CLIENTE, rowId);
                    resolver.notifyChange(uri_cliente, null, false);
                    return uri_cliente;
                }
            case ContractParaDatos.PRODUCTOS:
                rowId = db.insert(ContractParaDatos.PRODUCTO, null, contentValues);
                if (rowId > 0)
                {
                    Uri uri_producto = ContentUris.withAppendedId(ContractParaDatos.CONTENT_URI_PRODUCTO, rowId);
                    resolver.notifyChange(uri_producto, null, false);
                    return uri_producto;
                }
            case ContractParaDatos.RUTAS:
                rowId = db.insert(ContractParaDatos.RUTA, null, contentValues);
                if (rowId > 0)
                {
                    Uri uri_ruta = ContentUris.withAppendedId(ContractParaDatos.CONTENT_URI_RUTA, rowId);
                    resolver.notifyChange(uri_ruta, null, false);
                    return uri_ruta;
                }
            default:
                throw new SQLException("Falla al insertar fila en : " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int match = ContractParaDatos.uriMatcher.match(uri);
        int affected;
        switch (match)
        {
            case ContractParaDatos.CLIENTES:
                affected = db.delete(ContractParaDatos.CLIENTE, selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.PRODUCTOS:
                affected = db.delete(ContractParaDatos.PRODUCTO, selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.RUTAS:
                affected = db.delete(ContractParaDatos.RUTA, selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.NUMERO_RUTAS:
                affected = db.delete(ContractParaDatos.NUMERO_RUTA, selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.DIA_RUTAS:
                affected = db.delete(ContractParaDatos.DIA_RUTA, selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.BITACORAS:
                affected = db.delete(ContractParaDatos.BITACORA, selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.NOTAS_REMISION:
                affected = db.delete(ContractParaDatos.NOTA_REMISION, selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.CLIENTES_ID:
                long idCliente = ContentUris.parseId(uri);
                affected = db.delete(ContractParaDatos.CLIENTE,
                        ContractParaDatos.ColumnasCliente.ID_REMOTA + "=" + idCliente
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.PRODUCTOS_ID:
                long idProducto = ContentUris.parseId(uri);
                affected = db.delete(ContractParaDatos.PRODUCTO,
                        ContractParaDatos.ColumnasProducto.ID_REMOTA + "=" + idProducto
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.NUMERORUTAS_ID:
                long idNumeroRuta = ContentUris.parseId(uri);
                affected = db.delete(ContractParaDatos.NUMERO_RUTA,
                        ContractParaDatos.ColumnasNumeroRutas.ID_REMOTA + "=" + idNumeroRuta
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.DIARUTAS_ID:
                String id = uri.getLastPathSegment();
                affected = db.delete(ContractParaDatos.DIA_RUTA,
                        ContractParaDatos.ColumnasDiaRutas.ID_REMOTA + "=" + id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null);
                break;
            case ContractParaDatos.RUTAS_ID:
                long idRuta = ContentUris.parseId(uri);
                affected = db.delete(ContractParaDatos.RUTA,
                        ContractParaDatos.ColumnasRuta.ID_REMOTA + "=" + idRuta
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.BITACORAS_ID:
                long idBitacora = ContentUris.parseId(uri);
                affected = db.delete(ContractParaDatos.BITACORA,
                        ContractParaDatos.ColumnasBitacora.ID_REMOTA + "=" + idBitacora
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case ContractParaDatos.NOTASREMISION_ID:
                long idNota = ContentUris.parseId(uri);
                affected = db.delete(ContractParaDatos.NOTA_REMISION,
                        ContractParaDatos.ColumnasNotaRemision.ID_REMOTA + "=" + idNota
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            default:
                throw new IllegalArgumentException("Elemento desconocido: " + uri);
        }
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int affected;
        switch (ContractParaDatos.uriMatcher.match(uri))
        {
            case ContractParaDatos.CLIENTES:
                affected = db.update(ContractParaDatos.CLIENTE, values, selection, selectionArgs);
                break;
            case ContractParaDatos.PRODUCTOS:
                affected = db.update(ContractParaDatos.PRODUCTO, values, selection, selectionArgs);
                break;
            case ContractParaDatos.RUTAS:
                affected = db.update(ContractParaDatos.RUTA, values, selection, selectionArgs);
                break;
            case ContractParaDatos.BITACORAS:
                affected = db.update(ContractParaDatos.BITACORA, values, selection, selectionArgs);
                break;
            case ContractParaDatos.NOTAS_REMISION:
                affected = db.update(ContractParaDatos.NOTA_REMISION, values, selection, selectionArgs);
                break;
            case ContractParaDatos.NUMERO_RUTAS:
                affected = db.update(ContractParaDatos.NUMERO_RUTA, values, selection, selectionArgs);
                break;
            case ContractParaDatos.DIA_RUTAS:
                affected = db.update(ContractParaDatos.DIA_RUTA, values, selection, selectionArgs);
                break;
            case ContractParaDatos.CLIENTES_ID:
                String idCliente = uri.getPathSegments().get(1);
                affected = db.update(ContractParaDatos.CLIENTE, values,
                        ContractParaDatos.ColumnasCliente.ID_REMOTA + "=" + idCliente
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case ContractParaDatos.PRODUCTOS_ID:
                String idProducto = uri.getPathSegments().get(1);
                affected = db.update(ContractParaDatos.PRODUCTO, values,
                        ContractParaDatos.ColumnasProducto.ID_REMOTA + "=" + idProducto
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case ContractParaDatos.RUTAS_ID:
                String idRuta = uri.getPathSegments().get(1);
                affected = db.update(ContractParaDatos.RUTA, values,
                        ContractParaDatos.ColumnasRuta.ID_REMOTA + "=" + idRuta
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case ContractParaDatos.BITACORAS_ID:
                String idBitacora = uri.getPathSegments().get(1);
                affected = db.update(ContractParaDatos.BITACORA, values,
                        ContractParaDatos.ColumnasBitacora.ID_REMOTA + "=" + idBitacora
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case ContractParaDatos.NOTASREMISION_ID:
                String idNota = uri.getPathSegments().get(1);
                affected = db.update(ContractParaDatos.NOTA_REMISION, values,
                        ContractParaDatos.ColumnasNotaRemision.ID_REMOTA + "=" + idNota
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case ContractParaDatos.NUMERORUTAS_ID:
                String idNumeroRuta = uri.getPathSegments().get(1);
                affected = db.update(ContractParaDatos.NUMERO_RUTA, values,
                        ContractParaDatos.ColumnasNumeroRutas.ID_REMOTA + "=" + idNumeroRuta
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case ContractParaDatos.DIARUTAS_ID:
                String idDiaRuta = uri.getPathSegments().get(1);
                affected = db.update(ContractParaDatos.DIA_RUTA, values,
                        ContractParaDatos.ColumnasDiaRutas.ID_REMOTA + "=" + idDiaRuta
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        resolver.notifyChange(uri, null, false);
        return affected;
    }
}

