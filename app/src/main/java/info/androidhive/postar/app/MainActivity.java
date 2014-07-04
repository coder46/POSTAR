package info.androidhive.postar.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;


public class MainActivity extends Activity {

    SurfaceView cameraPreview;
    SurfaceHolder previewHolder;
    Camera camera;
    boolean inPreview;

    final static String TAG = "PAAR";
    SensorManager sensorManager;  //Manages all the device's sensors
    Sensor accelerometer;
    Sensor magnetometer;
    int magnetometerSensor;
    float headingAngle;  // X axis
    float pitchAngle;    // Y axis
    float rollAngle;    // Z axis

    int accelerometerSensor;
    float xAxis;
    float yAxis;
    float zAxis;

    LocationManager locationManager;
    double latitude;
    double longitude;
    double altitude;

    TextView xAxisValue;
    TextView yAxisValue;
    TextView zAxisValue;
    TextView headingValue;
    TextView pitchValue;
    TextView rollValue;
    TextView altitudeValue;
    TextView latitudeValue;
    TextView longitudeValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        xAxisValue = (TextView) findViewById(R.id.xAxisValue);
        yAxisValue = (TextView) findViewById(R.id.yAxisValue);
        zAxisValue = (TextView) findViewById(R.id.zAxisValue);
        headingValue = (TextView) findViewById(R.id.headingValue);
        pitchValue = (TextView) findViewById(R.id.pitchValue);
        rollValue = (TextView) findViewById(R.id.rollValue);
        altitudeValue = (TextView) findViewById(R.id.altitudeValue);
        latitudeValue = (TextView) findViewById(R.id.latitudeValue);
        longitudeValue = (TextView) findViewById(R.id.longitudeValue);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //orientationSensor = Sensor.TYPE_ORIENTATION;
        accelerometerSensor = Sensor.TYPE_ACCELEROMETER;
        magnetometerSensor = Sensor.TYPE_MAGNETIC_FIELD;

        sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(accelerometerSensor),SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(magnetometerSensor),SensorManager.SENSOR_DELAY_NORMAL);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0 ,locationListener);


        inPreview = false;

        cameraPreview = (SurfaceView)findViewById(R.id.cameraPreview);
        previewHolder = cameraPreview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }


    }


    @Override
    public void onResume(){
        super.onResume();
        camera = Camera.open();
        sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(accelerometerSensor),SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(magnetometerSensor),SensorManager.SENSOR_DELAY_NORMAL);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0 ,locationListener);

    }

    @Override
    public void onPause(){

        if(inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera=null;
        inPreview=false;

        sensorManager.unregisterListener(sensorEventListener);
        locationManager.removeUpdates(locationListener);

        super.onPause();

    }

    final SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] mGravity;
        float[] mGeomagnetic;
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mGravity = sensorEvent.values;

                xAxis = sensorEvent.values[0];
                yAxis = sensorEvent.values[1];
                zAxis = sensorEvent.values[2];

                Log.d(TAG,"X Axis: " + String.valueOf(xAxis));
                Log.d(TAG,"Y Axis: " + String.valueOf(yAxis));
                Log.d(TAG,"Z Axis: " + String.valueOf(zAxis));

                xAxisValue.setText(String.valueOf(xAxis));
                yAxisValue.setText(String.valueOf(yAxis));
                zAxisValue.setText(String.valueOf(zAxis));
            }

            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = sensorEvent.values;

            Log.d(TAG, "mGravity:" + String.valueOf(mGravity));
            Log.d(TAG, "mGeio:" + String.valueOf(mGeomagnetic));


            if(mGravity!=null && mGeomagnetic!=null) {
                float R[] = new float[9];
                float I[] = new float[9];

                boolean success = SensorManager.getRotationMatrix(R,I,mGravity,mGeomagnetic);
                if(success){
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R,orientation);
                    headingAngle = orientation[0];
                    pitchAngle = orientation[1];
                    rollAngle = orientation[2];

                    Log.d(TAG,"Heading: " + String.valueOf(headingAngle));
                    Log.d(TAG,"Pitch: " + String.valueOf(pitchAngle));
                    Log.d(TAG,"Roll: " + String.valueOf(rollAngle));

                    headingValue.setText(String.valueOf(headingAngle));
                    pitchValue.setText(String.valueOf(pitchAngle));
                    rollValue.setText(String.valueOf(rollAngle));
                }

            }
            else{
                Log.e(TAG, "Somethings wronng" + String.valueOf(mGravity) + " " + String.valueOf(mGeomagnetic));
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();

            Log.d(TAG, "Latitude: " + String.valueOf(latitude));
            Log.d(TAG, "Longitude: " + String.valueOf(longitude));
            Log.d(TAG, "Altitude: " + String.valueOf(altitude));

            latitudeValue.setText(String.valueOf(latitude));
            longitudeValue.setText(String.valueOf(longitude));
            altitudeValue.setText(String.valueOf(altitude));
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
    };

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                setCameraDisplayOrientation(MainActivity.this,0,camera);

            }
            catch (Throwable t) {
                Log.e("ProAndroidAR2Activity", "Exception in setPreviewDisplay()", t);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int
                height) {

            Camera.Parameters parameters=camera.getParameters();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                parameters.set("orientation", "portrait");
                parameters.set("rotation",90);
                Log.e("or","portrait");

            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                parameters.set("orientation", "landscape");
                parameters.set("rotation", 90);
                Log.e("or","land");
            }

            Camera.Size size=getBestPreviewSize(width, height, parameters);
            if (size!=null) {
                parameters.setPreviewSize(size.width, size.height);
                camera.setParameters(parameters);
                camera.startPreview();
                inPreview=true;
            }

            //setCameraDisplayOrientation(MainActivity.this,0,camera);


        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }


    };

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
