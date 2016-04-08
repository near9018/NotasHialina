package com.example.ok4r_.notashialina.ui;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ok4r_.notashialina.R;
import com.example.ok4r_.notashialina.adaptadores.AdaptadorDeProductos;
import com.example.ok4r_.notashialina.adaptadores.ArrayAdapterSpinner;
import com.example.ok4r_.notashialina.modelo.SpinnerItem;
import com.example.ok4r_.notashialina.provider.ContractParaDatos;
import com.example.ok4r_.notashialina.sync.SyncAdapter;
import com.example.ok4r_.notashialina.utils.Constantes;
import com.example.ok4r_.notashialina.utils.Localizacion;
import com.example.ok4r_.notashialina.utils.Utilidades;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NuevaNota extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        gpsDialog.OnSimpleDialogListener
{
    //Atributos de la nota
    String rutaNota = "";
    String vendedorNota = "Prueba";
    String fechaOperacionNota = "";
    String fechaRegistroNota = "";
    String codigoClienteNota = "";
    String codigoProductoNota = "";
    String precioNota = "";
    String ivaNota = "";
    String cantidadNota = "";
    String totalNota = "";
    Spinner clientes;
    ArrayAdapterSpinner spinnerAdapter;
    AdaptadorDeProductos adapter;
    RecyclerView productos;
    ContentResolver resolver;
    Double descuentoCliente;
    Double precioDeLista;
    TextView fecha;
    TextView total;
    TextView totalView;
    Button crearNota;
    EditText cantidad;
    TextView precioFinalView;
    Double precioFinal = 0.0;
    int cantidadProducto;
    Context context;
    Localizacion gps;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_nota_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        resolver = getContentResolver();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        context = getApplicationContext();
        clientes=(Spinner) findViewById(R.id.spinner4);
        fecha = (TextView) findViewById(R.id.fechaNota);
        total = (TextView) findViewById(R.id.textView13);
        crearNota = (Button)findViewById(R.id.button);
        totalView = (TextView) findViewById(R.id.button2);
        total.setVisibility(View.INVISIBLE);
        getFechaActual();
        Cursor c = getRuta();
        if (c.moveToFirst())
            rutaNota = c.getString(0);
        c.close();
        adapter = new AdaptadorDeProductos(this);
        productos = (RecyclerView)findViewById(R.id.productosRecycler);
        productos.setLayoutManager(layoutManager);
        productos.setAdapter(adapter);
        productos.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplication(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onItemClickRecycler(view, position);
                    }
                }));
        clientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(!precioNota.equals(""))
                {
                    DecimalFormat decimalFormat = new DecimalFormat("###.##");
                    View v= productos.getFocusedChild();
                    precioFinalView = (TextView) v.findViewById(R.id.textView8);
                    cantidad = (EditText) v.findViewById(R.id.editText);
                    if (precioFinalView.getVisibility() == View.VISIBLE)
                    {
                        Cursor c = getCliente(view);
                        if (c.moveToFirst())
                            descuentoCliente = Double.parseDouble(c.getString(0));
                        c.close();
                        c = getProducto(v);
                        if(c.moveToFirst())
                        {
                            precioDeLista = c.getDouble(0);
                            codigoProductoNota = c.getString(1);
                            ivaNota = c.getString(2);
                            precioFinal = precioDeLista*(1 - (descuentoCliente/100));
                            precioNota = decimalFormat.format(precioFinal);
                        }
                        c.close();
                        cantidadNota = cantidad.getText().toString();
                        cantidadProducto = Integer.parseInt(cantidadNota);
                        precioNota = decimalFormat.format(precioFinal);
                        precioFinalView.setText(precioNota);
                        Double totalPrecio = Integer.parseInt(cantidadNota) * Double.parseDouble(precioNota);
                        totalNota = "$ " + decimalFormat.format(totalPrecio);
                        total.setText(totalNota);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.nueva_nota);
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
        crearNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCrearNota(v);
            }
        });
    }

    private void getFechaActual()
    {
        Date date = new Date();
        Locale locale=Locale.getDefault();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", locale);
        fechaOperacionNota = format.format(date);
        fecha.setText(fechaOperacionNota);
        fechaRegistroNota =  android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()).toString();
    }

    public Cursor getRuta()
    {
        String[] projection= {ContractParaDatos.ColumnasRuta.NUMERO_RUTA};
        Uri uri = ContractParaDatos.CONTENT_URI_RUTA;
        Cursor c = getContentResolver().query(uri, projection, null, null, null);
        assert c != null;
        return c;
    }

    public Cursor getCliente(View view)
    {
        String[] projection= {ContractParaDatos.ColumnasCliente.DESCUENTO};
        TextView idCliente = (TextView) view.findViewById(R.id.clienteView);
        codigoClienteNota = idCliente.getText().toString();
        Uri uri = ContractParaDatos.CONTENT_URI_CLIENTE;
        String select = ContractParaDatos.ColumnasCliente.ID_REMOTA + " = " + codigoClienteNota;
        Cursor c = resolver.query(uri, projection, select, null, null);
        assert c != null;
        return c;
    }

    public Cursor getProducto(View v)
    {
        TextView producto = (TextView) v.findViewById(R.id.textView7);
        Uri uri = ContractParaDatos.CONTENT_URI_PRODUCTO;
        String select = ContractParaDatos.ColumnasProducto.PRODUCTO + " = ?";
        String[] projection = new String[] {ContractParaDatos.ColumnasProducto.PRECIO,
                ContractParaDatos.ColumnasProducto.ID_REMOTA,
                ContractParaDatos.ColumnasProducto.IVA};
        String[] args = new String[] {producto.getText().toString()};
        Cursor c = resolver.query(uri, projection, select, args, null);
        assert c != null;
        return c;
    }

    private void onItemClickRecycler(View view, int position)
    {
        cantidad = (EditText) view.findViewById(R.id.editText);
        precioFinalView = (TextView)view.findViewById(R.id.textView8);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final DecimalFormat decimalFormat = new DecimalFormat("###.##");
        view.requestFocus();
        //Obteniendo el descuento en base al cliente seleccionado
        Cursor c = getCliente(clientes.getSelectedView());
        if (c.moveToFirst())
            descuentoCliente = Double.parseDouble(c.getString(0));
        c.close();
        //Se obtiene el precio final en base al producto y descuento actuales
        c = getProducto(view);
        if(c.moveToFirst()) {
            precioDeLista = c.getDouble(0);
            codigoProductoNota = c.getString(1);
            ivaNota = c.getString(2);
            precioFinal = precioDeLista*(1 - (descuentoCliente/100));
        }
        c.close();
        imm.showSoftInput(cantidad, InputMethodManager.SHOW_IMPLICIT);
        cantidad.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_UP && cantidad.getText().toString().equals(""))
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_1:
                            cantidad.append("1");
                            break;
                        case KeyEvent.KEYCODE_2:
                            cantidad.append("2");
                            break;
                        case KeyEvent.KEYCODE_3:
                            cantidad.append("3");
                            break;
                        case KeyEvent.KEYCODE_4:
                            cantidad.append("4");
                            break;
                        case KeyEvent.KEYCODE_5:
                            cantidad.append("5");
                            break;
                        case KeyEvent.KEYCODE_6:
                            cantidad.append("6");
                            break;
                        case KeyEvent.KEYCODE_7:
                            cantidad.append("7");
                            break;
                        case KeyEvent.KEYCODE_8:
                            cantidad.append("8");
                            break;
                        case KeyEvent.KEYCODE_9:
                            cantidad.append("9");
                            break;
                        case KeyEvent.KEYCODE_ENTER:
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                            break;
                    }
                }
                else if (event.getAction() == KeyEvent.ACTION_UP && Integer.parseInt(cantidad.getText().toString())<100)
                {
                    switch(keyCode)
                    {
                        case KeyEvent.KEYCODE_0:
                            cantidad.append("0");
                            break;
                        case KeyEvent.KEYCODE_1:
                            cantidad.append("1");
                            break;
                        case KeyEvent.KEYCODE_2:
                            cantidad.append("2");
                            break;
                        case KeyEvent.KEYCODE_3:
                            cantidad.append("3");
                            break;
                        case KeyEvent.KEYCODE_4:
                            cantidad.append("4");
                            break;
                        case KeyEvent.KEYCODE_5:
                            cantidad.append("5");
                            break;
                        case KeyEvent.KEYCODE_6:
                            cantidad.append("6");
                            break;
                        case KeyEvent.KEYCODE_7:
                            cantidad.append("7");
                            break;
                        case KeyEvent.KEYCODE_8:
                            cantidad.append("8");
                            break;
                        case KeyEvent.KEYCODE_9:
                            cantidad.append("9");
                            break;
                        case KeyEvent.KEYCODE_ENTER:
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            break;
                    }
                }
                else if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                return true;
            }
        });
        cantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!cantidad.getText().toString().equals("")) {
                    cantidadNota = cantidad.getText().toString();
                    cantidadProducto = Integer.parseInt(cantidadNota);
                    precioNota = decimalFormat.format(precioFinal);
                    precioFinalView.setText(precioNota);
                    precioFinalView.setVisibility(View.VISIBLE);
                    Double totalPrecio = Integer.parseInt(cantidadNota) * Double.parseDouble(precioNota);
                    totalNota = decimalFormat.format(totalPrecio);
                    total.setText("$ " + totalNota);
                    total.setVisibility(View.VISIBLE);
                } else {
                    cantidadNota = "";
                    totalNota = "";
                    precioNota = "";
                    precioFinalView.setVisibility(View.INVISIBLE);
                    total.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void onClickCrearNota(View v)
    {
        gps = new Localizacion();
        Double latitude;
        Double longitude;

        if(!rutaNota.equals("") &&
                !vendedorNota.equals("") &&
                !fechaOperacionNota.equals("") &&
                !fechaRegistroNota.equals("") &&
                !codigoClienteNota.equals("") &&
                !codigoProductoNota.equals("") &&
                !precioNota.equals("") &&
                !ivaNota.equals("") &&
                !cantidadNota.equals("") &&
                !totalNota.equals(""))
        {
            System.out.println("Ruta " + rutaNota);
            System.out.println("Vendedor " + vendedorNota);
            System.out.println("Fecha de operacion " + fechaOperacionNota);
            System.out.println("Fecha de registro " + fechaRegistroNota);
            System.out.println("Codigo del cliente " + codigoClienteNota);
            System.out.println("Codigo del producto " + codigoProductoNota);
            System.out.println("Precio por unidad " + precioNota);
            System.out.println("IVA " + ivaNota);
            System.out.println("Cantidad de producto " + cantidadNota);
            System.out.println("Total de la nota " + totalNota);
            ContentValues values = new ContentValues();
            //Nota
            values.put(ContractParaDatos.ColumnasNotaRemision.RUTA, rutaNota);
            values.put(ContractParaDatos.ColumnasNotaRemision.VENDEDOR, vendedorNota);
            values.put(ContractParaDatos.ColumnasNotaRemision.FECHA_OPERACION, fechaOperacionNota);
            values.put(ContractParaDatos.ColumnasNotaRemision.FECHA_REGISTRO, fechaRegistroNota);
            values.put(ContractParaDatos.ColumnasNotaRemision.CODIGO_CLIENTE, codigoClienteNota);
            values.put(ContractParaDatos.ColumnasNotaRemision.CODIGO_PRODUCTO, codigoProductoNota);
            values.put(ContractParaDatos.ColumnasNotaRemision.PRECIO, precioNota);
            values.put(ContractParaDatos.ColumnasNotaRemision.IVA, ivaNota);
            values.put(ContractParaDatos.ColumnasNotaRemision.CANTIDAD, cantidadNota);
            values.put(ContractParaDatos.ColumnasNotaRemision.TOTAL_NOTA, totalNota);
            values.put(ContractParaDatos.ColumnasNotaRemision.PENDIENTE_INSERCION, 1);
            //Bitacora
            Bundle extras = getIntent().getExtras();
            latitude = extras.getDouble(Constantes.LATITUD);
            longitude = extras.getDouble(Constantes.LONGITUD);
            ContentValues values1 = new ContentValues();
            values1.put(ContractParaDatos.ColumnasBitacora.NUMERO_RUTA, rutaNota);
            values1.put(ContractParaDatos.ColumnasBitacora.FECHAHORA, fechaRegistroNota);
            values1.put(ContractParaDatos.ColumnasBitacora.CLIENTE_ID, codigoClienteNota);
            values1.put(ContractParaDatos.ColumnasBitacora.LATITUD, latitude);
            values1.put(ContractParaDatos.ColumnasBitacora.LONGITUD, longitude);
            values1.put(ContractParaDatos.ColumnasBitacora.CONCEPTO, "VENTA");
            values1.put(ContractParaDatos.ColumnasBitacora.PENDIENTE_INSERCION, 1);
            getContentResolver().insert(ContractParaDatos.CONTENT_URI_NOTAREMISION, values);
            getContentResolver().insert(ContractParaDatos.CONTENT_URI_BITACORA, values1);
            //SyncAdapter.sincronizarAhora(this, true);
            if (Utilidades.materialDesign())
                finishAfterTransition();
            else finish();
        }else
        {
            Toast.makeText(getApplicationContext(), "No se puede crear una nota vacia", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        CursorLoader loader;
        // Consultar todos los registros
        if(id==0)
        {
            loader = new CursorLoader(getApplicationContext(), ContractParaDatos.CONTENT_URI_CLIENTE, null, null, null, null);
        }
        else{
            loader = new CursorLoader(getApplicationContext(),ContractParaDatos.CONTENT_URI_PRODUCTO, null, null, null, null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(loader.getId()==0)
        {
            List<SpinnerItem> items = new ArrayList<>();
            if (data.moveToFirst())
            {
                do
                {
                    items.add(new SpinnerItem(data.getString(8), data.getString(1), data.getString(2)));
                } while (data.moveToNext());
                spinnerAdapter = new ArrayAdapterSpinner(this, items);
                clientes.setAdapter(spinnerAdapter);
            }
        }
        else
        {
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onPossitiveButtonClick() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    @Override
    public void onNegativeButtonClick() {
        Toast.makeText(
                this,
                "No se pudo crear la nota",
                Toast.LENGTH_LONG)
                .show();
    }
}
