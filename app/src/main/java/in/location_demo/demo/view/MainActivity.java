package in.location_demo.demo.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.location_demo.demo.Constants;
import in.location_demo.demo.PlaceJSONParser;
import in.location_demo.demo.R;
import in.location_demo.demo.RetrofitHelper;
import in.location_demo.demo.model.ApiService;
import in.location_demo.demo.model.Ride;
import in.location_demo.demo.model.Route;
import in.location_demo.demo.presenter.LocationPresenter;
import in.location_demo.demo.presenter.PlacePresenter;
import in.location_demo.demo.presenter.RoutesPresenter;
import retrofit2.Retrofit;
import rx.Observable;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener, PlacesViewInterface, LocationViewInterface, RoutesViewInterface {
    Ride mRide = new Ride();
    GoogleMap mGoogleMap;
    Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    ApiService apiService;
    PlacePresenter mPlacePresenter;
    LocationPresenter mLocationPresenter;
    RoutesPresenter mRoutesPresenter;
    ListView mRoutesListView;
    LinearLayout mapLayout;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final int INPUT_FROM = 1;
    private static final int INPUT_TO = 2;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    protected LocationManager locationManager;
    AutoCompleteTextView placesFrom, placesTo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        checkLocationPermission();
        initView();
        mPlacePresenter = new PlacePresenter(this);
        mLocationPresenter = new LocationPresenter(this);
        mRoutesPresenter = new RoutesPresenter(this);
    }

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.
                ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.
                            ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        mapLayout = (LinearLayout) findViewById(R.id.mapLayout);
        mRoutesListView = (ListView) findViewById(R.id.routesListView);
        mRoutesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Route route = (Route) adapterView.getItemAtPosition(i);
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(route.mLatLngPoints);
                mGoogleMap.addPolyline(polylineOptions);
                mRoutesListView.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
            }
        });

        placesFrom = (AutoCompleteTextView) findViewById(R.id.place_autocomplete_fragment_from);
        placesFrom.setThreshold(2);
        placesFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> map = (HashMap<String, String>) adapterView.getItemAtPosition(i);
                String address = map.get("description");
                placesFrom.setSelection(0);
                String url = Constants.LOCATION_URL +"?"+Constants.KEY_ADDRESS+"="+address+"&"+
                        Constants.KEY_APIKEY+"="+Constants.API_KEY;
                mLocationPresenter.getLocation(url, INPUT_FROM);
                mRide.mFromName = address;
            }
        });
        placesTo = (AutoCompleteTextView) findViewById(R.id.place_autocomplete_fragment_to);
        placesTo.setThreshold(2);
        placesTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> map = (HashMap<String, String>) adapterView.getItemAtPosition(i);
                placesTo.setSelection(0);
                String address = map.get("description");
                String url = Constants.LOCATION_URL +"?"+Constants.KEY_ADDRESS+"="+address+"&"+
                        Constants.KEY_APIKEY+"="+Constants.API_KEY;
                mLocationPresenter.getLocation(url, INPUT_TO);
                mRide.mToName = address;
            }
        });
        getMapReady();
        placesFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPlaces(s.toString(), INPUT_FROM);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        placesTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPlaces(s.toString(), INPUT_TO);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void getMapReady() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getPlaces(String s, int inputFrom) {
        String url = Constants.PLACES_URL +"?"+Constants.KEY_INPUT+"="+s+"&"+
                Constants.KEY_TYPE+"="+"geocode&"+Constants.KEY_SENSOR+"=false&"+
                Constants.KEY_APIKEY+"="+Constants.API_KEY+"&"+Constants.KEY_VALUE_COMPONENTS;
        mPlacePresenter.getPlaces(url, inputFrom);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleted() {
        System.out.print("");
    }

    @Override
    public void onError(String message) {
        System.out.print("");
    }

    @Override
    public void onResults(JsonObject results, int inputFrom) {
        try {
            List<HashMap<String, String>> places;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
            JSONObject jsonObject = new JSONObject(results.toString());
            places = placeJsonParser.parse(jsonObject);
            String[] from = new String[] { "description"};
            int[] to = new int[] { android.R.id.text1 };
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), places,
                    android.R.layout.simple_list_item_1, from, to);
            if(inputFrom == INPUT_FROM) {
                placesFrom.setAdapter(adapter);
            }
            else if(inputFrom == INPUT_TO)
                placesTo.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "API limit crossed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationCompleted() {

    }

    @Override
    public void onLocationError(String message) {

    }

    @Override
    public void onLocationResults(JsonObject response, int inputFrom) {
        JsonArray results = response.getAsJsonArray("results");
        JsonObject geometry  = results.get(0).getAsJsonObject().getAsJsonObject("geometry");
        JsonObject location = geometry.getAsJsonObject("location");
        Double lat = location.get("lat").getAsDouble();
        Double lng = location.get("lng").getAsDouble();
        LatLng newLatLng = new LatLng(lat,lng);
        if(inputFrom == INPUT_FROM){
            try {
                mRide.fromMarker.remove();
            }catch (Exception e){

            }
            mRide.fromMarker = mGoogleMap.addMarker(new MarkerOptions().position(newLatLng).
                    title(mRide.mFromName.substring(0,10)));
            mRide.mFromLatLng = newLatLng;
        } else if(inputFrom == INPUT_TO) {
            try {
                mRide.toMarker.remove();
            }catch (Exception e){

            }
            mRide.mToLatLng = newLatLng;
            mRide.toMarker = mGoogleMap.addMarker(new MarkerOptions().position(newLatLng).
                    title(mRide.mToName.substring(0,10)));
            if(!mRide.mFromName.equals("") && !mRide.mToName.equals("")) {
                String url = Constants.ROUTES_URL + "?" + Constants.KEY_ORIGIN + "=" +
                        mRide.mFromLatLng.latitude + "," + mRide.mFromLatLng.longitude + "&" +
                        Constants.KEY_DESTINATION + "=" + mRide.mToLatLng.latitude + "," +
                        mRide.mToLatLng.longitude + "&" + Constants.KEY_APIKEY + "=" + Constants.API_KEY;
                mRoutesPresenter.getRoutes(url);
            }
        }

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public Observable<JsonObject> getLocation(String url) {
        return apiService.request(url);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Location location = getLocation();
        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mGoogleMap.addMarker(new MarkerOptions().position(newLatLng).icon(BitmapDescriptorFactory
                .fromResource(R.drawable.current_location_map_pointer_small)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }

    public Location getLocation() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.
                        ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.
                        ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);


            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRoutesCompleted() {

    }

    @Override
    public void onRoutesError(String message) {
    }

    @Override
    public void onRoutesResults(JsonObject results) {
        JsonArray routes = results.getAsJsonArray("routes");
        ArrayList<Route> routesList = new ArrayList<>();
        for(int i=0;i<routes.size();i++){
            JsonObject routeObj = (JsonObject) routes.get(i);
            String summary = routeObj.get("summary").getAsString();
            JsonObject legsObj = routeObj.getAsJsonArray("legs").get(0).getAsJsonObject();
            JsonObject distanceObj = legsObj.get("distance").getAsJsonObject();
            String distance = distanceObj.get("text").getAsString();
            JsonObject durationObj = legsObj.get("duration").getAsJsonObject();
            String duration = durationObj.get("text").getAsString();
            Route route = new Route();
            route.SummaryText = summary;
            route.mDistance = distance;
            route.mDuration = duration;
            String points = routeObj.get("overview_polyline").getAsJsonObject().get("points").getAsString();
            route.mLatLngPoints = PolyUtil.decode(points);
            routesList.add(route);
        }
        if(routesList.size() >0){
            RoutesListAdapter routesAdapter = new RoutesListAdapter(routesList, mContext);
            mRoutesListView.setAdapter(routesAdapter);

        }
    }

    @Override
    public Observable<JsonObject> getRoutes(String url) {
        return apiService.request(url);
    }

    @Override
    public void onBackPressed(){
        if(mapLayout.getVisibility() == View.VISIBLE){
            mapLayout.setVisibility(View.GONE);
            mRoutesListView.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }
}
