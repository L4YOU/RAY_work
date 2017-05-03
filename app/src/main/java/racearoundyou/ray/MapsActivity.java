package racearoundyou.ray;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private static final String TAG = "LATLNG";
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Event marker;
    List<Event> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        marker = new Event();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent login = new Intent(MapsActivity.this, LoginActivity.class);
                    startActivity(login);
                }
            }
        };
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
        mMap.setOnMapLongClickListener(this);
        //Get markers from database
        GetMarkers();
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
            Database.useApi().insertMarker(latLng.latitude, latLng.longitude).enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {
                    marker.setStatus(response.body().toString());
                    mMap.addMarker(new MarkerOptions().position(latLng));
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {
                    Toast.makeText(MapsActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                }
            });
        }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    protected void GetMarkers() {
        Database.useApi().getMarkers().enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                GenericTypeIndicator<Map<String, List<Event>>> IDEVENTMAP = new GenericTypeIndicator<Map<String, List<Event>>>() {};
                Map<String, Event> EventInfo = resp;
                if (response.body() == null){
                    Log.d(TAG, "НИХУА В ОТВЕТЕ НЕТ");
                }else {
                markers.addAll(response.body());
                for (Iterator<Event> i = markers.iterator(); i.hasNext();){
                    Event marker = i.next();
                    mMap.addMarker(marker.options());
                }}
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
