package in.location_demo.demo.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Dell 3450 on 7/13/2017.
 */

public class Route {
    public List<LatLng> mLatLngPoints;
    public LatLng mFromLatLng, mToLatLng;
    public String SummaryText;
    public String mDuration;
    public String mDistance;
}
