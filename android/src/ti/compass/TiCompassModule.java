package ti.compass;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiLocationHelper;
import org.appcelerator.titanium.util.TiSensorHelper;


@Kroll.module(name = "TiCompass", id = "ti.compass")
public class TiCompassModule extends KrollModule {

    private static final String LCAT = "TiCompassModule";
    LocationManager lm;
    Location myLocation;
    Location destinationLoc;
    SensorManager sensorManager;
    private SensorEventListener eventListenerOrientation;
    private SensorEventListener eventListenerAccelerometer;

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
    public void init() {

        // demo data for AR location
        destinationLoc = new Location("service Provider");
        destinationLoc.setLatitude(48.135124);
        destinationLoc.setLongitude(11.581981);

        // GPS - get user location
        lm = TiLocationHelper.getLocationManager();
        myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // GPS position available
                myLocation = location;
                double latUser = location.getLatitude();
                double lonUser = location.getLongitude();
            }
        };
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        // Sensor - get orientation
        sensorManager = TiSensorHelper.getSensorManager();
        eventListenerOrientation = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // sensor data available
                float[] values = event.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];

                float direction = (x + 90) % 360;
                float roll = z;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(eventListenerOrientation, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        eventListenerAccelerometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // orientation data available
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
}

