package bike.rapido.rapido_test;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Dell 3450 on 7/11/2017.
 */

public class MyLocationListener {


    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;


}
