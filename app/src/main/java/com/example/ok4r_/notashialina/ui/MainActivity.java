package com.example.ok4r_.notashialina.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ok4r_.notashialina.R;
import com.example.ok4r_.notashialina.adaptadores.AdaptadorDeRuta;
import com.example.ok4r_.notashialina.adaptadores.ArrayAdapterSpinner;
import com.example.ok4r_.notashialina.modelo.SpinnerItem;
import com.example.ok4r_.notashialina.provider.ContractParaDatos;
import com.example.ok4r_.notashialina.sync.SyncAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AdaptadorDeRuta adapter;
    private TextView emptyView;
    //private ProviderDeDatos provider;
    ArrayAdapterSpinner spinnerAdapter;
    Spinner sp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        sp = (Spinner) findViewById(R.id.spinner);
        recyclerView = (RecyclerView) findViewById(R.id.reciclador);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdaptadorDeRuta(this);
        recyclerView.setAdapter(adapter);
        emptyView = (TextView) findViewById(R.id.recyclerview_data_empty);
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
        SyncAdapter.inicializarSyncAdapter(this);
        //recyclerView.setVisibility(View.INVISIBLE);

    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onClickFab(View v) {
       /* Intent intent = new Intent(this, InsertActivity.class);
        if (Utilidades.materialDesign())
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        else startActivity(intent);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync_productos) {
            SyncAdapter.sincronizarAhora(this, "1", "Lunes");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        CursorLoader loader;
        emptyView.setText("Cargando datos...");
        // Consultar todos los registros
        if(id==0)
        {
            loader = new CursorLoader(getApplicationContext(),ContractParaDatos.CONTENT_URI_CLIENTE, null, null, null, null);
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
                sp.setAdapter(spinnerAdapter);
            }
        }
        else{
            adapter.swapCursor(data);
            emptyView.setText("");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
