package com.example.ok4r_.notashialina.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ok4r_.notashialina.R;
import com.example.ok4r_.notashialina.modelo.SpinnerItem;

import java.util.List;

public class ArrayAdapterSpinner extends ArrayAdapter<SpinnerItem>
{
    Context context;
    List<SpinnerItem > data=null;
    int myLayoutResourceID;
    public ArrayAdapterSpinner(Context context, List<SpinnerItem> objects)
    {
        super(context, 0, objects);
        data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row=convertView;
        FilterAdapterSpinnerHolder holder=null;
        /*
        Obtener el objeto procesado actualmente
        */
        //SpinnerItem currentItem = getItem(position);
        /*
        Obtener LayoutInflater de la actividad
         */
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /*
        Evitar inflar de nuevo un elemento previamente inflado
         */
        if(row==null){
            row = inflater.inflate(R.layout.item_layout, parent, false);
            holder = new FilterAdapterSpinnerHolder();
            holder.id = (TextView) row.findViewById(R.id.clienteView);
            holder.nombre = (TextView) row.findViewById(R.id.etiqueta);
            holder.direccion = (TextView) row.findViewById(R.id.fechaView);
            holder.clave = (TextView) row.findViewById(R.id.clave);
            row.setTag(holder);
        }else
        {
            holder = (FilterAdapterSpinnerHolder) row.getTag();
        }
        SpinnerItem spinnerItem = data.get(position);
        holder.id.setText(spinnerItem.getID());
        holder.nombre.setText(spinnerItem.getNombre());
        holder.direccion.setText(spinnerItem.getDireccion());
        holder.clave.setText("");

        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        /*
        Debido a que deseamos usar spinner_item.xml para inflar los
        items del Spinner en ambos casos, entonces llamamos a getView()
         */
        return getView(position,convertView,parent);
    }

    static class FilterAdapterSpinnerHolder
    {
        TextView id;
        TextView nombre;
        TextView direccion;
        TextView clave;
    }
}