package com.example.mimaps;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mimaps.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private double latitud, longitud;
    SharedPreferences preferences;
    private Button btnFavorito;
    private Button btnlimpiar;
    private Button btnalterno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnFavorito=(Button) findViewById(R.id.fav);
        btnFavorito.setOnClickListener(this);
        btnlimpiar=(Button) findViewById(R.id.limp);
        btnlimpiar.setOnClickListener(this);
        btnalterno=(Button) findViewById(R.id.ub);
        btnalterno.setOnClickListener(this);



    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        preferences=getSharedPreferences("MyPreferences1", Context.MODE_PRIVATE);
        //recibo los valores obtenidos por el gps en la actividad anterior
        longitud=Double.parseDouble(getIntent().getStringExtra("longitud"));
        latitud=Double.parseDouble(getIntent().getStringExtra("latitud"));
        //creo una ubicacion
        LatLng miUbicacion = new LatLng(latitud, longitud);

        //agrego un marca con la ubicacion
        mMap.addMarker(new MarkerOptions().position(miUbicacion).title("Mi ubicacion"));
        //mover la camara a mi ubicacion
        mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
        //habilito los controles del zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //doy zoom 16 para que se acerque
        CameraUpdate ZoomCam=CameraUpdateFactory.zoomTo(16);
        mMap.animateCamera(ZoomCam);

        // fijo el long click al mapa
        mMap.setOnMapLongClickListener(this);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(MapsActivity.this,
                "Click posición"+latLng.latitude+latLng.longitude,Toast.LENGTH_SHORT).show();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marcador").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        //colocar Icono como señalizacion
                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marcador_foreground)).anchor(0.0f,1.0f)
        //cambiar de color la marca
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        );
        guardaPreferencias(latLng);
    }
    public void guardaPreferencias(LatLng latLng){

        SharedPreferences.Editor editor=preferences.edit();
        editor.putFloat("latitud",(float) latLng.latitude);
        editor.putFloat("longitud",(float)latLng.longitude);

        editor.commit();
    }

    public void cargarPreferencias(){

        double lat=preferences.getFloat("latitud",0);
        double log=preferences.getFloat("longitud",0);

        if (lat!=0){
            LatLng puntoPref=new LatLng(lat,log);
            mMap.addMarker(new MarkerOptions().position(puntoPref).title("Mi ubicacion"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(puntoPref));
            CameraUpdate ZoomCam=CameraUpdateFactory.zoomTo(16);
            mMap.animateCamera(ZoomCam);


        }else{
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setTitle("No tiene ningun sitio Favorito");
            alert.setPositiveButton("OK",null);
            alert.create().show();

        }
        Toast.makeText(MapsActivity.this,
                "mi favorito es: "+lat+log,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onClick(View v) {
        if (v==btnalterno){
            cargarPreferencias();
        }
        else
        if (v==btnFavorito){
            cargarPreferencias();
        }
        else if(v==btnlimpiar){
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setTitle("Marcas eliminadas");
            alert.setPositiveButton("OK",null);
            alert.create().show();
            //markerName.remove();
            mMap.clear();

        }
    }
}