package com.example.ok4r_.notashialina.ui;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ok4r- on 26/01/2016.
 */

import android.os.Bundle;
import android.view.View;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.example.ok4r_.notashialina.R;
import com.example.ok4r_.notashialina.provider.ContractParaDatos;
import com.example.ok4r_.notashialina.sync.SyncAdapter;
import com.example.ok4r_.notashialina.utils.Constantes;
import com.example.ok4r_.notashialina.utils.Localizacion;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity
{
    ViewPager mViewPager;
    NotasFragment notas;
    RutasFragment rutas;
    Double latitude = 0.0;
    Double longitud = 0.0;
    String ruta;
    String dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        setToolbar(); // Añadir la toolbar
        // Setear adaptador al viewpager.
        SyncAdapter.inicializarSyncAdapter(getApplicationContext());
        SyncAdapter.sincronizacionAutomatica(getApplicationContext());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(mViewPager);
        // Preparar las pestañas
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);
        // Create a GoogleApiClient instance
        //Iniciando el servicio de localizacion
        Intent intentLocalizacion = new Intent(getApplicationContext(), Localizacion.class);
        startService(intentLocalizacion); //Iniciar servicio
        IntentFilter filter = new IntentFilter(
                Constantes.LOCALIZACION);

        // Crear un nuevo ResponseReceiver
        ResponseReceiver receiver = new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    public void cambiarTitulo(Cursor c)
    {
        if(c.moveToFirst())
        {
            ruta = c.getString(0);
            dia = c.getString(1);
            String title = "Ruta " + ruta + " " + dia;
            setTitle(title);
        }
        c.close();
    }

    public Cursor obtenerRuta()
    {
        String[] projection= {ContractParaDatos.ColumnasRuta.NUMERO_RUTA, ContractParaDatos.ColumnasRuta.DIA};
        Uri uri = ContractParaDatos.CONTENT_URI_RUTA;
        Cursor c = getContentResolver().query(uri, projection, null, null, null);
        assert c != null;
        return c;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (item.getItemId())
        {
            case R.id.action_sync_productos:
                SyncAdapter.sincronizarAhora(this, true, false);
                return true;
            case R.id.action_settings:
                new RutaDialog().show(fragmentManager, "RutaDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Muestra una {@link Snackbar} prefabricada
     *
     * @param msg Mensaje a proyectar
     */
    private void showSnackBar(String msg) {
        Snackbar.make(findViewById(R.id.fab), msg, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Establece la toolbar como action bar
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Crea una instancia del view pager con los datos
     * predeterminados
     *
     * @param viewPager Nueva instancia
     */
    private void setupViewPager(ViewPager viewPager)
    {
        notas = new NotasFragment();
        rutas = new RutasFragment();
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(notas, "Notas");
        adapter.addFragment(rutas, "Ruta");
        viewPager.setAdapter(adapter);
    }

    public void onFabClick(View v)
    {
        Intent intent = new Intent(this, NuevaNota.class);
        intent.putExtra(Constantes.LATITUD, latitude);
        intent.putExtra(Constantes.LONGITUD, longitud);
        startActivity(intent);
    }


    /**
     * Método onClick() del FAB
     *
     * @param v View presionado
     */


    /**
     * Un {@link FragmentPagerAdapter} que gestiona las secciones, fragmentos y
     * títulos de las pestañas
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
    private class ResponseReceiver extends BroadcastReceiver {

        // Sin instancias
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constantes.LOCALIZACION:
                    latitude  = intent.getDoubleExtra(Constantes.LATITUD, 0.0);
                    longitud  = intent.getDoubleExtra(Constantes.LONGITUD, 0.0);
                    System.out.println("Position desde main "+ latitude+","+longitud);
                    break;
            }
        }
    }
}