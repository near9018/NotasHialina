package com.example.ok4r_.notashialina.utils;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import com.example.ok4r_.notashialina.provider.ContractParaDatos;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Localizacion extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LocationSettingsRequest.Builder builder;
    String ruta;

    @Override
    public void onCreate() {
        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            System.out.println("Servicio creado");
        }
        getRuta();
        createLocationRequest();

    }

    public void getRuta()
    {
        String[] projection= {ContractParaDatos.ColumnasRuta.NUMERO_RUTA};
        Uri uri = ContractParaDatos.CONTENT_URI_RUTA;
        Cursor c = getContentResolver().query(uri, projection, null, null, null);
        assert c != null;
        if (c.moveToFirst())
            ruta=c.getString(0);
        c.close();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Servicio iniciado");
        mGoogleApiClient.connect();
        super.onStartCommand(intent, flags, startId);
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        String fechaRegistro =  android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss",
                new java.util.Date()).toString();
        System.out.println("Location change  " + location.toString());
        if (ruta == null)
            getRuta();
        System.out.println("Ruta:  " + ruta);
        ContentValues values1 = new ContentValues();
        values1.put(ContractParaDatos.ColumnasBitacora.NUMERO_RUTA, ruta);
        values1.put(ContractParaDatos.ColumnasBitacora.FECHAHORA, fechaRegistro);
        values1.put(ContractParaDatos.ColumnasBitacora.LATITUD, location.getLatitude());
        values1.put(ContractParaDatos.ColumnasBitacora.LONGITUD, location.getLongitude());
        values1.put(ContractParaDatos.ColumnasBitacora.CONCEPTO, "GPS");
        values1.put(ContractParaDatos.ColumnasBitacora.PENDIENTE_INSERCION, 1);
        if(ruta != null)
            getContentResolver().insert(ContractParaDatos.CONTENT_URI_BITACORA, values1);

        Toast.makeText(this, location.toString(), Toast.LENGTH_SHORT).show();

        Intent localIntent = new Intent(Constantes.LOCALIZACION)
                .putExtra(Constantes.LATITUD, location.getLatitude());
        localIntent.putExtra(Constantes.LONGITUD, location.getLongitude());

        // Emitir el intent a la actividad
        LocalBroadcastManager.
                getInstance(Localizacion.this).sendBroadcast(localIntent);
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        stopLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(60000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, android.os.Process.myPid(), android.os.Process.myUid()) ==
                PackageManager.PERMISSION_GRANTED &&
                checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, android.os.Process.myPid(), android.os.Process.myUid()) ==
                        PackageManager.PERMISSION_GRANTED)
        {
            System.out.println("StartLocation");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
}


