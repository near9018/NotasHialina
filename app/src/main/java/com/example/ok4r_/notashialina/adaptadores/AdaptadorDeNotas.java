package com.example.ok4r_.notashialina.adaptadores;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.example.ok4r_.notashialina.R;
import com.example.ok4r_.notashialina.ui.NuevaNota;

/**
 * Created by ok4r- on 22/02/2016.
 */
public class AdaptadorDeNotas extends RecyclerView.Adapter<AdaptadorDeNotas.ViewHolder>
{
    private Cursor cursor;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // Campos respectivos de un item

        public TextView cliente;
        public TextView producto;
        public TextView fecha;
        public TextView total;



        public ViewHolder(View v)
        {
            super(v);
            cliente = (TextView) v.findViewById(R.id.clienteView);
            producto = (TextView) v.findViewById(R.id.productoView);
            fecha = (TextView) v.findViewById(R.id.fechaView);
            total = (TextView) v.findViewById(R.id.totalView);
        }
    }

    public AdaptadorDeNotas(Context context)
    {
        this.context= context;
    }

    @Override
    public int getItemCount()
    {
        if (cursor!=null)
            return cursor.getCount();
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_notaventa_principal, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i)
    {
        cursor.moveToPosition(i);
        String cliente;
        String producto;
        String fecha;
        String total;
        cliente = cursor.getString(1);
        producto = cursor.getString(5) + " " + cursor.getString(2);
        fecha = cursor.getString(0);
        total = "$"+cursor.getString(6);
        viewHolder.cliente.setText(cliente);
        viewHolder.producto.setText(producto);
        viewHolder.fecha.setText(fecha);
        viewHolder.total.setText(total);
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }
}
