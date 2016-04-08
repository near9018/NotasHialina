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
public class AdaptadorDeProductos extends RecyclerView.Adapter<AdaptadorDeProductos.ViewHolder>
        implements View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener
{
    private Cursor cursor;
    private Context context;
    private View.OnClickListener listener;
    private View.OnKeyListener keyListener;
    private View.OnFocusChangeListener focusChangeListener;
    NuevaNota nota;

    @Override
    public void onClick(View v)
    {
        if(listener != null)
            listener.onClick(v);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        if(keyListener != null)
            keyListener.onKey(v, keyCode, event);
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)

    {
        if(focusChangeListener != null)
            focusChangeListener.onFocusChange(v, hasFocus);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // Campos respectivos de un item

        public EditText cantidad;
        public TextView producto;
        public TextView precioUnitario;



        public ViewHolder(View v)
        {
            super(v);
            cantidad = (EditText) v.findViewById(R.id.editText);
            producto = (TextView) v.findViewById(R.id.textView7);
            precioUnitario = (TextView) v.findViewById(R.id.textView8);
        }
    }

    public AdaptadorDeProductos(Context context)
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
                .inflate(R.layout.item_notaventa_layout, viewGroup, false);
        v.setOnClickListener(this);
        nota = new NuevaNota();
        //v.setFocusable(true);
        v.setOnKeyListener(this);
        v.setOnFocusChangeListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i)
    {
        cursor.moveToPosition(i);
        String producto;
        producto = cursor.getString(1);
        viewHolder.producto.setText(producto);
        viewHolder.precioUnitario.setText("");
        viewHolder.cantidad.setText("");
        viewHolder.cantidad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText cantidad = (EditText) v.findViewById(R.id.editText);
                TextView precioU = viewHolder.precioUnitario;
                if (!hasFocus) {
                    if (cantidad.length() > 0) {
                        cantidad.getText().clear();
                    }
                    if (precioU != null)
                        precioU.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }
}
