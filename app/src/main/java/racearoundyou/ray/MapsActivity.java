package racearoundyou.ray;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.location.LocationListener;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements AddMarker.OnDataSetListener,
        ClubSearch.OnListFragmentInteractionListener,
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        CreateClub.ClubDataSetListener{
    private static final String TAG = "LATLNG";
    private GoogleMap mMap;
    private FirebaseUser FBUser;
    Database db;
    private ValueEventListener muPL;
    private ValueEventListener mePL;
    private ValueEventListener mcPL;
    private GoogleApiClient mGoogleApiClient;
//    private Location mLastLocation;
//    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    private UiSettings mUISettings;
    private Drawer result = null;
    private Drawer resultAppended = null;
    private AccountHeader headerResult = null;
    Map<String, Marker> UserPos = new HashMap<>();
    Map<String, Marker> EventPos = new HashMap<>();
    private Intent login;
    String eKey;
    String uKey;
    List<Club>  ClubList = new ArrayList<>();
    User user;
    private Integer sUser;
    private Integer sEvent;
    private Integer sKoff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
        }
        else {
            buildGoogleApiClient();
        }
        sUser = 0;
        sEvent = 0;
        FBUser = FirebaseAuth.getInstance().getCurrentUser();
        db = new Database();
        db.setUPDB();
        if (FBUser == null) {
            login = new Intent(MapsActivity.this, LoginActivity.class);
            startActivity(login);
        } else {
            db.UserRef().child(FBUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user.isFirstAuth()){
                        login = new Intent(MapsActivity.this, LoginActivity.class);
                        login.putExtra("isFirstAuth", user.isFirstAuth());
                        startActivity(login);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, "CHECK DB RULES   " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        setContentView(R.layout.activity_maps);

        checkLocationPermission();

        //DRAW MENU
        if (FBUser != null){
        final IProfile profile = new ProfileDrawerItem().withName("Temp Usermeme").withEmail(FBUser.getEmail()).withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460").withIdentifier(100);
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.color.grey_main)
                .addProfiles(profile)
                .withSavedInstance(savedInstanceState)
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        Intent Profile = new Intent(MapsActivity.this, Profile.class);
                        Profile.putExtra("email", user.getEmail())
                        .putExtra("name", user.getNickname())
                        .putStringArrayListExtra("clubs", user.getClubs());
                        startActivity(Profile);
                        result.closeDrawer();
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile profile) {
                        return false;
                    }
                })
                .build();}
        //first create the main drawer (this one will be used to add the second drawer on the other side)
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        new SectionDrawerItem().withName("Сообщества"),
                        new PrimaryDrawerItem().withName("Поиск сообщества").withIdentifier(1).withIcon(FontAwesome.Icon.faw_jpy),
                        new PrimaryDrawerItem().withName("Создать сообщество").withIdentifier(2).withIcon(FontAwesome.Icon.faw_plus),
                        new SectionDrawerItem().withName("Друзья"),
                        new SecondaryDrawerItem().withName("Список друзей").withIdentifier(3).withIcon(FontAwesome.Icon.faw_users),
                        new SecondaryDrawerItem().withName("Добавить друга").withIdentifier(4).withIcon(FontAwesome.Icon.faw_user_plus)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        Toast.makeText(MapsActivity.this, "onDrawerOpened", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Toast.makeText(MapsActivity.this, "onDrawerClosed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            if (drawerItem instanceof Nameable) {
                                Toast.makeText(MapsActivity.this, ((Nameable) drawerItem).getName().getText(MapsActivity.this), Toast.LENGTH_SHORT).show();
                                if (drawerItem.getIdentifier() == 2){
                                    Fragment createClubFragment = new CreateClub();
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.map, createClubFragment).addToBackStack(null).commit();
                                } else if (drawerItem.getIdentifier() == 1){
                                    Fragment searchClubFragment = new ClubSearch();
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.map, searchClubFragment).addToBackStack(null).commit();
                                }
                            }

                            if (drawerItem instanceof Badgeable) {
                                Badgeable badgeable = (Badgeable) drawerItem;
                                if (badgeable.getBadge() != null) {
                                    //note don't do this if your badge contains a "+"
                                    //only use toString() if you set the test as String
                                    int badge = Integer.valueOf(badgeable.getBadge().toString());
                                    if (badge > 0) {
                                        badgeable.withBadge(String.valueOf(badge - 1));
                                        result.updateItem(drawerItem);
                                    }
                                }
                            }
                        }

                        return false;
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(MapsActivity.this, ((SecondaryDrawerItem) drawerItem).getName().getText(MapsActivity.this), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        if (drawerView == result.getSlider()) {
                            Log.e("sample", "left opened");
                        } else if (drawerView == resultAppended.getSlider()) {
                            Log.e("sample", "right opened");
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        if (drawerView == result.getSlider()) {
                            Log.e("sample", "left closed");
                        } else if (drawerView == resultAppended.getSlider()) {
                            Log.e("sample", "right closed");
                        }
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .build();

        //now we add the second drawer on the other site.
        //use the .append method to add this drawer to the first one
        resultAppended = new DrawerBuilder()
                .withActivity(this)
                .withFooter(R.layout.footer)
                .withDisplayBelowStatusBar(true)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad),
                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_github),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_question).withEnabled(false),
                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_bullhorn)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            Toast.makeText(MapsActivity.this, ((Nameable) drawerItem).getName().getText(MapsActivity.this), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .withDrawerGravity(Gravity.END)
                .append(result);

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
        mUISettings = mMap.getUiSettings();
        mUISettings.setZoomControlsEnabled(true);
        mUISettings.setCompassEnabled(true);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("CurrLatLng", latLng);
        BottomSheetDialogFragment markerInfoFragment = new AddMarker();
        markerInfoFragment.setArguments(bundle);
        markerInfoFragment.show(getSupportFragmentManager(), markerInfoFragment.getTag());
    }

    public void onEventSet(Event mEvent){
        db.AddEvent(mEvent);
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserInfo();
        getEvents();
        getClubList();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
        if (muPL != null){
            db.UserRef().removeEventListener(muPL);
        }
        if (mePL != null){
            db.MarkersRef().removeEventListener(mePL);
        }
        if (mcPL != null){
            db.ClubRef().removeEventListener(mcPL);
        }
        Intent bgService = new Intent(this, BGSendPosService.class);
        startService(bgService);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void getClubList(){
        ValueEventListener clublistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Club>> clubmap = new GenericTypeIndicator<Map<String, Club>>() {
                };
                Map<String, Club> ClubInfo = dataSnapshot.getValue(clubmap);
                if (ClubInfo != null)
                {
                    for(String key: ClubInfo.keySet()){
                        Club mClub = new Club();
                        mClub.setName(ClubInfo.get(key).getName());
                        mClub.setDescription(ClubInfo.get(key).getDescription());
                        mClub.setUserAmount(ClubInfo.get(key).getUserAmount());
                        mClub.setId(key);
                        mClub.setUsers(ClubInfo.get(key).getUsers());
                        ClubList.add(mClub);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        db.ClubRef().addValueEventListener(clublistener);
        mcPL = clublistener;
    }

    public void getUserInfo(){
        ValueEventListener usersPositionListner = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                GenericTypeIndicator<Map<String, User>> USERPOSITIONMAP = new GenericTypeIndicator<Map<String, User>>() {};
                Map<String, User> UserInfo = dataSnapshot.getValue(USERPOSITIONMAP);
                if (UserInfo != null)
                {
                    for(String key: UserInfo.keySet())
                    {
                        if ((UserInfo.get(key).getLongtitude()) != null && (UserInfo.get(key).getLatitude() != null)) {
                            if (!UserPos.containsKey(key)) {
                                UserPos.put(key, mMap.addMarker(new MarkerOptions().position(new LatLng(UserInfo.get(key).getLatitude(), UserInfo.get(key).getLongtitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ricon))));
                            } else if ((UserPos.get(key).getPosition() != null) && (UserPos.get(key).getPosition().latitude != UserInfo.get(key).getLatitude()) || (UserPos.get(key).getPosition().longitude != UserInfo.get(key).getLongtitude())){
                                Marker location = UserPos.get(key);
                                location.setPosition(new LatLng(UserInfo.get(key).getLatitude(), UserInfo.get(key).getLongtitude()));
                                UserPos.put(key, location);
                            }
                        } else {
                            if (UserPos.containsKey(key)){
                                UserPos.get(key).remove();
                                UserPos.remove(key);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        };
        db.UserRef().addValueEventListener(usersPositionListner);
        muPL = usersPositionListner;
    }

    public void getEvents() {
        ValueEventListener EventListner = new ValueEventListener() {
                @Override
                public void onDataChange (DataSnapshot dataSnapshot){
                    GenericTypeIndicator<Map<String, Event>> IDEVENTMAP = new GenericTypeIndicator<Map<String, Event>>() {};
                    Map<String, Event> EventInfo = dataSnapshot.getValue(IDEVENTMAP);
                    if (EventInfo != null)
                        {
                            for(String key: EventInfo.keySet())
                            {
                                for (String interest: user.getInterests().keySet()){
                                    sUser = sUser + user.getInterests().get(interest)/10;
                                }
                                for (String interest: EventInfo.get(key).getInterests().keySet()){
                                    sEvent = sEvent + EventInfo.get(key).getInterests().get(interest)/10;
                                }
                                sKoff = sUser*sEvent;
                                if ((EventInfo.get(key).getLongtitude()) != null && (EventInfo.get(key).getLatitude() != null)) {
                                    if (!EventPos.containsKey(key)) {
                                        EventPos.put(key, mMap.addMarker(new MarkerOptions().position(new LatLng(EventInfo.get(key).getLatitude(), EventInfo.get(key).getLongtitude()))));
                                    }
                                } else {
                                    if (EventPos.containsKey(key)){
                                        EventPos.get(key).remove();
                                        EventPos.remove(key);
                                    }
                                }
                            }
                        }
                }
                @Override
                public void onCancelled (DatabaseError databaseError){
                    Log.d(TAG, databaseError.toString());
                }
            };
        db.MarkersRef().addValueEventListener(EventListner);
        mePL = EventListner;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
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
    public void onLocationChanged(Location location){
//        mLastLocation = location;
//        if(mCurrLocationMarker != null) {
//            if ((mCurrLocationMarker.getPosition().latitude != mLastLocation.getLatitude()) || (mCurrLocationMarker.getPosition().longitude != mLastLocation.getLongitude())) {
//                mCurrLocationMarker.remove();
//            }
//        }

        user.setLatitude(location.getLatitude());
        user.setLongtitude(location.getLongitude());
        db.setUserPosition(user);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the apppended drawer to the bundle
        outState = resultAppended.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (EventPos.containsValue(marker))
        {
            for (Map.Entry entry: EventPos.entrySet()){
                if (marker.equals(entry.getValue())){
                    eKey = entry.getKey().toString();
                    break;
                }
            }
            Bundle bundle = new Bundle();
            bundle.putString("eKey", eKey);
            BottomSheetDialogFragment markerInfoFragment = new MarkerInfo();
            markerInfoFragment.setArguments(bundle);
            markerInfoFragment.show(getSupportFragmentManager(), markerInfoFragment.getTag());
        }
        if (UserPos.containsValue(marker))
        {
            for (Map.Entry entry: UserPos.entrySet()){
                if (marker.equals(entry.getValue())){
                    uKey = entry.getKey().toString();
                    break;
                }
            }
            Bundle bundle = new Bundle();
            bundle.putString("uKey", uKey);
            BottomSheetDialogFragment markerInfoFragment = new UserInfo();
            markerInfoFragment.show(getSupportFragmentManager(), markerInfoFragment.getTag());
        }
        return true;
    }

    @Override
    public void onListFragmentInteraction(Club mClub) {

    }

    @Override
    public void SendClub(Club mClub) {
        ArrayList<String> clubList = new ArrayList<>();
        if (user.getClubs() != null) {
            clubList.addAll(user.getClubs());
        }
        clubList.add(mClub.getName());
        user.setClubs(clubList);
        db.UserRef().child(FBUser.getUid()).setValue(user);
        db.ClubRef().child(mClub.getName()).setValue(mClub);
    }
}
