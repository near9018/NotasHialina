package com.example.ok4r_.notashialina.adaptadores;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ok4r_.notashialina.R;

/**
 * Adaptador del recycler view
 */
public class AdaptadorDeRuta extends RecyclerView.Adapter<AdaptadorDeRuta.ViewHolder>
        implements View.OnClickListener
{
    private Cursor cursor;
    private Context context;
    private View.OnClickListener listener;


    @Override
    public void onClick(View v)
    {
        if(listener != null)
            listener.onClick(v);
    }
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView id;
        public TextView nombre;
        public TextView direccion;
        public TextView clave;


        public ViewHolder(View v)
        {
            super(v);
            id = (TextView) v.findViewById(R.id.clienteView);
            nombre = (TextView) v.findViewById(R.id.etiqueta);
            direccion = (TextView) v.findViewById(R.id.fechaView);
            clave = (TextView) v.findViewById(R.id.clave);
        }

    }

    public AdaptadorDeRuta(Context context) {
        this.context= context;

    }

    @Override
    public int getItemCount() {
        if (cursor!=null)
        return cursor.getCount();
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout, viewGroup, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        cursor.moveToPosition(i);
        String id;
        String nombre;
        String direccion;
        String idRemotaCliente;

        id = cursor.getString(0);
        nombre = cursor.getString(2);
        direccion = cursor.getString(1);
        idRemotaCliente = cursor.getString(3);

        viewHolder.id.setText(id);
        viewHolder.nombre.setText(nombre);
        viewHolder.direccion.setText(direccion);
        viewHolder.clave.setText(idRemotaCliente);
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }
}