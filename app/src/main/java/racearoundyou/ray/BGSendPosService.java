package racearoundyou.ray;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by L4YOU on 16.05.2017.
 */

public class BGSendPosService extends IntentService implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Database db;
    private FirebaseUser FBUser;

    public BGSendPosService() {
        super("BGSendPosService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent){

    }

    @Override
    public void onCreate(){
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        db = new Database();
        db.setUPDB();
        FBUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onLocationChanged(Location location) {
        User user = new User();
        user.setLatitude(location.getLatitude());
        user.setLongtitude(location.getLongitude());
        db.setUserPosition(user);
        Log.d("POS", user.getLatitude().toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (FBUser != null) {
            db.UserRef().child(FBUser.getUid() + "/latitude").removeValue();
            db.UserRef().child(FBUser.getUid() + "/longtitude").removeValue();
        }
    }
}
