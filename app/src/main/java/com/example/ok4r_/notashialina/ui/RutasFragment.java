package com.example.ok4r_.notashialina.ui;

import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ok4r_.notashialina.R;
import com.example.ok4r_.notashialina.adaptadores.AdaptadorDeRuta;
import com.example.ok4r_.notashialina.provider.ContractParaDatos;

/**
 * Created by ok4r- on 26/01/2016.
 */
public class RutasFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AdaptadorDeRuta adapter;
    private TextView emptyView;
    CursorLoader cursorLoader;
    View rootView;

    public RutasFragment(){}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        adapter = new AdaptadorDeRuta(getActivity().getApplicationContext());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.rutas_layout, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.reciclador);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView nombre = (TextView) v.findViewById(R.id.fechaView);
                Log.i("Clientes: ", "" + nombre.getText());
            }
        });
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });
        emptyView = (TextView) rootView.findViewById(R.id.recyclerview_data_empty);
        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        emptyView.setText("Cargando datos...");
        // Consultar todos los registros
        cursorLoader = new CursorLoader(
                getActivity(),
                ContractParaDatos.CONTENT_URI_RUTA_CLIENTE,
                null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        emptyView.setText("");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
