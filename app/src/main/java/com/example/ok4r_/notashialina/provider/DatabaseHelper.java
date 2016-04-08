package com.example.ok4r_.notashialina.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase envoltura para el gestor de Bases de datos
 */
class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase database)
    {
        createTableProductos(database); // Crear la tabla productos
        createTableClientes(database); // Crear la tabla clientes
        createTableRutas(database); // Crear la tabla Rutas
        createTableBitacora(database); // Crear la tabla Bitacora
        createTableNotasRemision(database); // Crear la tabla NotasRemision
        createTableNumeroRuta(database); // Crear la tabla para los numeros de las rutas
        createTableDiaRuta(database); // Crear la tabla para los dias de la ruta seleccionada
    }

    /**
     * Crear tabla en la base de datos
     *
     * @param database Instancia de la base de datos
     */
    private void createTableClientes(SQLiteDatabase database)
    {
        String cmd = "CREATE TABLE " + ContractParaDatos.CLIENTE + " (" +
                ContractParaDatos.ColumnasCliente.ID_CLIENTES + " INTEGER PRIMARY KEY, " +
                ContractParaDatos.ColumnasCliente.NOMBRE + " TEXT, " +
                ContractParaDatos.ColumnasCliente.DIRECCION + " TEXT, " +
                ContractParaDatos.ColumnasCliente.TELEFONO + " TEXT, " +
                ContractParaDatos.ColumnasCliente.CONTACTO + " TEXT, " +
                ContractParaDatos.ColumnasCliente.EMAIL + " TEXT, " +
                ContractParaDatos.ColumnasCliente.RFC + " TEXT, " +
                ContractParaDatos.ColumnasCliente.DESCUENTO + " TEXT, " +

                ContractParaDatos.ColumnasCliente.ID_REMOTA + " TEXT UNIQUE, " +
                ContractParaDatos.ColumnasCliente.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaDatos.ESTADO_OK+"," +
                ContractParaDatos.ColumnasCliente.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);
    }
    private void createTableProductos(SQLiteDatabase database)
    {
        String cmd = "CREATE TABLE " + ContractParaDatos.PRODUCTO + " (" +
                ContractParaDatos.ColumnasProducto.ID_PRODUCTOS + " INTEGER PRIMARY KEY, " +
                ContractParaDatos.ColumnasProducto.PRODUCTO + " TEXT, " +
                ContractParaDatos.ColumnasProducto.PRECIO + " TEXT, " +
                ContractParaDatos.ColumnasProducto.IVA + " TEXT, " +

                ContractParaDatos.ColumnasProducto.ID_REMOTA + " TEXT UNIQUE, " +
                ContractParaDatos.ColumnasProducto.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaDatos.ESTADO_OK+", " +
                ContractParaDatos.ColumnasProducto.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);
    }

    private void createTableRutas(SQLiteDatabase database)
    {
        String cmd = "CREATE TABLE " + ContractParaDatos.RUTA + " (" +
                ContractParaDatos.ColumnasRuta.ID_RUTAS + " INTEGER PRIMARY KEY, " +
                ContractParaDatos.ColumnasRuta.NUMERO_RUTA + " TEXT, " +
                ContractParaDatos.ColumnasRuta.DIA + " TEXT, " +
                ContractParaDatos.ColumnasRuta.SECUENCIA + " TEXT, " +
                ContractParaDatos.ColumnasRuta.CLIENTE + " TEXT, " +

                ContractParaDatos.ColumnasRuta.ID_REMOTA + " TEXT UNIQUE, " +
                ContractParaDatos.ColumnasRuta.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaDatos.ESTADO_OK+", " +
                ContractParaDatos.ColumnasRuta.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);
    }

    private void createTableBitacora(SQLiteDatabase database)
    {
        String cmd = "CREATE TABLE " + ContractParaDatos.BITACORA + " (" +
                ContractParaDatos.ColumnasBitacora.ID_BITACORA + " INTEGER PRIMARY KEY, " +
                ContractParaDatos.ColumnasBitacora.NUMERO_RUTA + " TEXT, " +
                ContractParaDatos.ColumnasBitacora.FECHAHORA + " TEXT, " +
                ContractParaDatos.ColumnasBitacora.CLIENTE_ID + " TEXT, " +
                ContractParaDatos.ColumnasBitacora.LATITUD + " TEXT, " +
                ContractParaDatos.ColumnasBitacora.LONGITUD + " TEXT, " +
                ContractParaDatos.ColumnasBitacora.CONCEPTO + " TEXT, " +
                ContractParaDatos.ColumnasBitacora.INCIDENCIA + " TEXT, " +

                ContractParaDatos.ColumnasBitacora.ID_REMOTA + " TEXT UNIQUE," +
                ContractParaDatos.ColumnasBitacora.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaDatos.ESTADO_OK+"," +
                ContractParaDatos.ColumnasBitacora.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);
    }

    private void createTableNotasRemision(SQLiteDatabase database)
    {
        String cmd = "CREATE TABLE " + ContractParaDatos.NOTA_REMISION + " (" +
                ContractParaDatos.ColumnasNotaRemision.ID_NOTA + " INTEGER PRIMARY KEY, " +
                ContractParaDatos.ColumnasNotaRemision.RUTA + " TEXT, " +
                ContractParaDatos.ColumnasNotaRemision.VENDEDOR + " TEXT, " +
                ContractParaDatos.ColumnasNotaRemision.FECHA_OPERACION + " TEXT, " +
                ContractParaDatos.ColumnasNotaRemision.FECHA_REGISTRO + " TEXT, " +
                ContractParaDatos.ColumnasNotaRemision.CODIGO_CLIENTE + " TEXT, " +
                ContractParaDatos.ColumnasNotaRemision.CODIGO_PRODUCTO + " TEXT, " +
                ContractParaDatos.ColumnasNotaRemision.PRECIO + " TEXT, " +
                ContractParaDatos.ColumnasNotaRemision.IVA + " TEXT, " +
                ContractParaDatos.ColumnasNotaRemision.CANTIDAD + " TEXT, " +
                ContractParaDatos.ColumnasNotaRemision.TOTAL_NOTA + " TEXT, " +
                ContractParaDatos.ColumnasNotaRemision.ACTIVA + " TEXT, " +

                ContractParaDatos.ColumnasNotaRemision.ID_REMOTA + " TEXT UNIQUE, " +
                ContractParaDatos.ColumnasNotaRemision.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaDatos.ESTADO_OK+", " +
                ContractParaDatos.ColumnasNotaRemision.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);
    }

    private void createTableNumeroRuta(SQLiteDatabase database)
    {
        String cmd = "CREATE TABLE " + ContractParaDatos.NUMERO_RUTA + " (" +
                ContractParaDatos.ColumnasNumeroRutas.ID_NUMERORUTA + " INTEGER PRIMARY KEY, " +

                ContractParaDatos.ColumnasNumeroRutas.ID_REMOTA + " TEXT UNIQUE, " +
                ContractParaDatos.ColumnasNumeroRutas.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaDatos.ESTADO_OK+", " +
                ContractParaDatos.ColumnasNumeroRutas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);
    }

    private void createTableDiaRuta(SQLiteDatabase database)
    {
        String cmd = "CREATE TABLE " + ContractParaDatos.DIA_RUTA + " (" +
                ContractParaDatos.ColumnasDiaRutas.ID_DIARUTA + " INTEGER PRIMARY KEY, " +

                ContractParaDatos.ColumnasDiaRutas.ID_REMOTA + " TEXT UNIQUE, " +
                ContractParaDatos.ColumnasDiaRutas.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaDatos.ESTADO_OK+", " +
                ContractParaDatos.ColumnasDiaRutas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try
        {
            db.execSQL("drop table if exists " + ContractParaDatos.CLIENTE);
            db.execSQL("drop table if exists " + ContractParaDatos.PRODUCTO);
            db.execSQL("drop table if exists " + ContractParaDatos.RUTA);
            db.execSQL("drop table if exists " + ContractParaDatos.BITACORA);
            db.execSQL("drop table if exists " + ContractParaDatos.NOTA_REMISION);
            db.execSQL("drop table if exists " + ContractParaDatos.NUMERO_RUTA);
            db.execSQL("drop table if exists " + ContractParaDatos.DIA_RUTA);
        }
        catch (SQLiteException e) { }
        onCreate(db);
    }

}
