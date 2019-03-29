package com.lemon.support.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import java.util.List;

/**
 * Created by yerunjie on 2018/1/22.
 */

public class LocalUtils {
    private static LocalUtils localUtils;
    private LocationManager locationManager;
    private Context context;
    private String locationProvider = null;

    private LocalUtils(Context context) {
        this.context = context;
    }

    public static LocalUtils create(Context context) {
        if (localUtils == null)
            localUtils = new LocalUtils(context);
        return localUtils;
    }

    public boolean canGetLocal(final String get_local_error) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(context, get_local_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String[] getLocal() {
        if (locationProvider != null) {
            Location location = locationManager.getLastKnownLocation(locationProvider);
            try {
                String[] local = new String[]{location.getLongitude() + "", location.getLatitude() + ""};
                return local;
            } catch (Exception e) {
                return new String[]{"121.454187", "31.202881"};
            }

        }
        return null;
    }
}
