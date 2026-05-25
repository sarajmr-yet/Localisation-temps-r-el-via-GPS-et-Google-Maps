package com.example.trackingapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private RequestQueue queue;

    private static final String FETCH_URL =
            "http://192.168.1.10/localisation/getLocations.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        queue = Volley.newRequestQueue(getApplicationContext());

        SupportMapFragment fragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment != null) fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setZoomControlsEnabled(true);
        loadMarkersFromServer();
    }

    private void loadMarkersFromServer() {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                FETCH_URL,
                null,
                response -> {
                    try {
                        JSONArray list = response.getJSONArray("locations");
                        if (list.length() == 0) {
                            Toast.makeText(this,
                                    "Aucune position enregistrée",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject item = list.getJSONObject(i);
                            double lat  = item.getDouble("latitude");
                            double lng  = item.getDouble("longitude");
                            String date = item.getString("date");
                            String dev  = item.getString("imei");

                            LatLng point = new LatLng(lat, lng);
                            gMap.addMarker(new MarkerOptions()
                                    .position(point)
                                    .title("Appareil : " + dev)
                                    .snippet("Date : " + date));

                            if (i == list.length() - 1) {
                                gMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(point, 14));
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Erreur parsing : " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this,
                        "Erreur réseau : " + error.getMessage(),
                        Toast.LENGTH_LONG).show()
        );
        queue.add(req);
    }
}
