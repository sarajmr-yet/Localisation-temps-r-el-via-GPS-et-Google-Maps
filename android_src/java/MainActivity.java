package com.example.trackingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PERM_CODE = 200;

    private TextView tvLatitude, tvLongitude, tvStatus;
    private LocationManager locationManager;
    private RequestQueue queue;

    // ⚠️ Remplacer par l'IP de votre machine serveur
    private static final String SAVE_URL =
            "http://192.168.1.10/localisation/saveLocation.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLatitude  = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvStatus    = findViewById(R.id.tvStatus);
        Button btnMap = findViewById(R.id.btnOpenMap);

        queue = Volley.newRequestQueue(getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnMap.setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        checkAndRequestPermission();
    }

    private void checkAndRequestPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERM_CODE);
        } else {
            beginTracking();
        }
    }

    @SuppressLint("MissingPermission")
    private void beginTracking() {
        tvStatus.setText("GPS actif — en attente de signal...");
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                60000,
                150,
                gpsListener
        );
    }

    private final LocationListener gpsListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NonNull Location loc) {
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            double alt = loc.getAltitude();
            float  acc = loc.getAccuracy();

            tvLatitude.setText("Latitude : " + lat);
            tvLongitude.setText("Longitude : " + lng);
            tvStatus.setText("Précision : " + acc + " m");

            String msg = String.format(
                    getString(R.string.location_update),
                    lat, lng, alt, acc
            );
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            sendLocationToServer(lat, lng);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String label;
            switch (status) {
                case LocationProvider.AVAILABLE:              label = "DISPONIBLE"; break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE: label = "INDISPONIBLE"; break;
                default:                                      label = "HORS SERVICE";
            }
            Toast.makeText(getApplicationContext(),
                    String.format(getString(R.string.gps_status), provider, label),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Toast.makeText(getApplicationContext(),
                    String.format(getString(R.string.gps_enabled), provider),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Toast.makeText(getApplicationContext(),
                    String.format(getString(R.string.gps_disabled), provider),
                    Toast.LENGTH_SHORT).show();
        }
    };

    private void sendLocationToServer(final double lat, final double lng) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                SAVE_URL,
                response -> tvStatus.setText("Envoyé ✓ — " + response),
                error -> Toast.makeText(getApplicationContext(),
                        "Erreur envoi : " + error.getMessage(),
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                SimpleDateFormat sdf =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Map<String, String> p = new HashMap<>();
                p.put("latitude",  String.valueOf(lat));
                p.put("longitude", String.valueOf(lng));
                p.put("date",      sdf.format(new Date()));
                p.put("deviceId",  getDeviceId());
                return p;
            }
        };
        queue.add(req);
    }

    private String getDeviceId() {
        String androidId = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ANDROID_ID);
        return (androidId != null && !androidId.isEmpty()) ? androidId : "UNKNOWN";
    }

    @Override
    public void onRequestPermissionsResult(int code,
            @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code == PERM_CODE && results.length > 0
                && results[0] == PackageManager.PERMISSION_GRANTED) {
            beginTracking();
        } else {
            Toast.makeText(this, "Permission GPS refusée", Toast.LENGTH_LONG).show();
        }
    }
}
