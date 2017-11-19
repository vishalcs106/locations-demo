package in.location_demo.demo.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Dell 3450 on 7/11/2017.
 */

public class Ride {
    public LatLng mFromLatLng;
    public LatLng mToLatLng;
    public String mFromName;
    public String mToName;
    public Marker fromMarker;
    public Marker toMarker;
}
