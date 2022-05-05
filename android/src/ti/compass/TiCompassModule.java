package ti.compass;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiLocationHelper;
import org.appcelerator.titanium.util.TiSensorHelper;

import java.util.Random;


@Kroll.module(name = "TiCompass", id = "ti.compass")
public class TiCompassModule extends KrollModule {

    // Standard Debugging variables
    private static final String LCAT = "TiCompassModule";
    private static final float ALPHA = 0.5f; //lower alpha should equal smoother movement
    private static final String TAG = "MainActivity";
    private static final double ANGLE_FOV = 15;
    private static final double ROLL_THRESHOLD = 40; // Degree after wich POIs are to be rendered
    private static final int currentLimit = 0;
    float[] mGravity;
    float[] mGeomagnetic;
    LocationManager lm;
    Location myLocation;
    Location destinationLoc;
    float currentBearing = 0;
    Double centerX = 0d;
    SensorManager sensorManager;
    float distanceList = 0;
    private SensorEventListener eventListenerOrientation;
    private double thita, slope, roll = 0;
    private float direction = 0;
    private Sensor sensorAccelerometer;
    private SensorEventListener eventListenerAccelerometer;

    // You can define constants with @Kroll.constant, for example:
    // @Kroll.constant public static final String EXTERNAL_NAME = value;

    public TiCompassModule() {
        super();
    }

    @Kroll.onAppCreate
    public static void onAppCreate(TiApplication app) {
        Log.d(LCAT, "inside onAppCreate");
        // put module init code that needs to run when the application is created
    }


    // Methods
    @SuppressLint("MissingPermission")
    @Kroll.method
    public void init(KrollDict obj) {
        centerX = obj.getDouble("centerX");

        lm = TiLocationHelper.getLocationManager();
        myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        destinationLoc = new Location("service Provider");
        destinationLoc.setLatitude(48.135124);
        destinationLoc.setLongitude(11.581981);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = location;

                double latUser = location.getLatitude();
                double lonUser = location.getLongitude();

                String str_lat = "Latitude: " + String.format("%.3f", latUser);
                String str_lon = "Longitude: " + String.format("%.3f", lonUser);
                Log.i("", str_lat + " " + str_lon);

                double latDest = destinationLoc.getLatitude();
                double lonDest = destinationLoc.getLongitude();
                float[] results = new float[3];
                Location.distanceBetween(latUser, lonUser, latDest, lonDest, results);

                Log.i(LCAT, str_lat + " " + str_lon + "target: " + latDest + " " + lonDest + " - Distance: " + results[0]);
                distanceList = results[0];

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        sensorManager = TiSensorHelper.getSensorManager();

        eventListenerOrientation = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] values = event.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];

                direction = (x + 90) % 360; // float direction
                roll = z;
                int i = 0;
                double userLat = 0, userLon = 0;
                if (myLocation != null) {
                    userLat = myLocation.getLatitude();
                    userLon = myLocation.getLongitude();

                }
                StringBuilder builder = new StringBuilder(); // Store the results

                if (roll > ROLL_THRESHOLD) {
                    double distanceToDestination = distanceList;

                    //if (distanceToDestination < currentLimit) {
                    double destLat = destinationLoc.getLatitude();
                    double destLon = destinationLoc.getLongitude();

                    slope = (destLon) - userLon / (destLat) - userLat;
                    thita = Math.atan(slope);
                    thita *= 180;
                    thita /= 3.14;
                    thita += 360;
                    thita %= 360;

                    if (userLat > destLat && userLon < destLon) {
                        thita = (thita + 180) % 360;
                    }

                    Log.i(TAG, "Your Angle: " + direction + " Angle: " + thita); // Angle with Magnetic North

                    double low = (direction - ANGLE_FOV + 360) % 360;
                    double high = (direction + ANGLE_FOV + 360) % 360;

                    //Log.i(TAG, "Lower Limit: " + low + " Upper Limit: " + high);

                    if ((low > high) && ((low <= thita && thita < 360) || (0 <= thita && thita <= high))) {
                        Log.i(TAG, "Rendered");
                        renderPOI();
                    } else if (thita > low && thita < high) {
                        Log.i(TAG, "Rendered");
                        renderPOI();
                    }
                }

                //}
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(eventListenerOrientation, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        //sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        eventListenerAccelerometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] values = event.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(eventListenerAccelerometer, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

    }

    private void renderPOI(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        TiApplication.getAppRootOrCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
/*
        KrollDict kd = new KrollDict();
        kd.put("valueX", render_x);
        kd.put("valueY", render_y);
        fireEvent("update", kd);*/
    }
}

