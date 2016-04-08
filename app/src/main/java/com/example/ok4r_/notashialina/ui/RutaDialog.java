package com.example.ok4r_.notashialina.ui;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.example.ok4r_.notashialina.sync.SyncAdapter;
import com.example.ok4r_.notashialina.R;
import com.example.ok4r_.notashialina.provider.ContractParaDatos;

/**
 * Fragmento con un diálogo personalizado
 */
public class RutaDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = RutaDialog.class.getSimpleName();
    View v;
    Spinner rutas;
    Spinner dias;

    public RutaDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    public AlertDialog createLoginDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_signin, null, false);
        rutas = (Spinner) v.findViewById(R.id.spinner2);
        dias = (Spinner) v.findViewById(R.id.spinner3);
        SyncAdapter.inicializarSyncAdapter(getActivity());
        SyncAdapter.sincronizarAhora(getActivity(), false, true);
        builder.setView(v);
        Button signin = (Button) v.findViewById(R.id.entrar_boton);
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
        rutas.setSelected(false);
        signin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cargar la ruta
                        if(!rutas.getSelectedItem().toString().equals("") &&
                                !dias.getSelectedItem().toString().equals(""))
                        {
                            SyncAdapter.sincronizarAhora(getActivity() ,rutas.getSelectedItem().toString(),dias.getSelectedItem().toString());
                            getActivity().setTitle("Ruta " +rutas.getSelectedItem().toString() + " " + dias.getSelectedItem().toString());
                        }
                        dismiss();
                    }
                }
        );
        rutas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {   if(!rutas.getSelectedItem().toString().equals(""))
                SyncAdapter.sincronizarAhora(getActivity(), false, rutas.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return builder.create();
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader;
        if(id==0) loader = new CursorLoader(getActivity(),ContractParaDatos.CONTENT_URI_NUMERORUTA, null, null, null, null);
        else loader = new CursorLoader(getActivity(),ContractParaDatos.CONTENT_URI_DIARUTA, null, null, null, ContractParaDatos.ColumnasDiaRutas.ID_REMOTA);
        return loader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data)
    {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        adapter.add("");
        adapter1.add("");
        switch (loader.getId())
        {
            case 0:
                if (data.moveToFirst())
                {
                    do
                    {
                        adapter.add(data.getString(1));
                        //Log.i(TAG, data.getString(1));
                    } while (data.moveToNext());
                    rutas.setAdapter(adapter);
                    rutas.setSelection(0);
                }
                break;
            case 1:
                if (data.moveToFirst())
                {
                    do
                    {
                        adapter1.add(data.getString(1));
                        //Log.i(TAG, data.getString(1));
                    } while (data.moveToNext());
                    dias.setAdapter(adapter1);
                    dias.setSelection(0);
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }
}

