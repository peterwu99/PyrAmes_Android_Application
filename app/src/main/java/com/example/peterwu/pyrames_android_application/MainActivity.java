package com.example.peterwu.pyrames_android_application;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.MetadataChangeSet;
//import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
//import com.google.api.services.drive.Drive;

//mark time stamp for start time                        //Done
//wider range for graphing                              //Done
//instructions to use app readme.                       //Incomplete
//indication that button pressed                        //Eh, Incomplete
//download app to device //'build apk', 'sideloading'   //Incomplete

//user: pyramesapp
//pass: pyrames123
//pyramesca318
//autoscaling
//set-up

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    BluetoothAdapter bluetooth;

    //Viewport variables
    //
    private int lastX = 0; //count for index of new points added to viewports

    private LineGraphSeries<DataPoint> series1;
    private LineGraphSeries<DataPoint> series2;
    private LineGraphSeries<DataPoint> series3;
    private LineGraphSeries<DataPoint> series4;

    private GraphView graph1;
    private GraphView graph2;
    private GraphView graph3;
    private GraphView graph4;

    int minX = 0; int maxX = 100;
    int yRange = 1500;
    int minY1 = 8500; int maxY1 = minY1+yRange;
    int minY2 = 9000; int maxY2 = minY2+yRange;
    int minY3 = 11500; int maxY3 = minY3+yRange;
    int minY4 = 10500; int maxY4 = minY4+yRange;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private GoogleApiClient googleclient;

    String bigString = "";
    int bigStringIndex = 0;

    int dataArr[][] = new int[4][50000];
    int dataArrIndex = 0;

    int graphIndex = 0;

    int yIncrement = 100;

    String startTime;

    //viewports
    //
    Viewport viewport1 = null; Viewport viewport2 = null; Viewport viewport3 = null; Viewport viewport4 = null;

    private boolean autoScaleIsOn = false;
    private void setAutoScalingOn() {
        autoScaleIsOn = true;
    }
    private void setAutoScalingOff() {
        autoScaleIsOn = false;
    }

    public void upload()
    {
        //
        //
        //DRIVE STUFF
        //
        /*
        Log.i(TAG, "API client connected.");
        */
        client.connect();
        googleclient.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());

        saveFileToDrive();

    }

    static TextView textViewBattery;

    static boolean displayingGraphs = false;

    static boolean clickedButtonPopUp = false;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        googleclient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //System.out.println("Entered onCreate");

        //------------------
        //Graphics
        //
        //

        //
        // pop-up window
        Button buttonPopUp = (Button) findViewById(R.id.buttonPopUp);
        buttonPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Pop.class));
            }
        });

        //
        // Graphs
        //
        //data
        series1 = new LineGraphSeries<DataPoint>();
        series2 = new LineGraphSeries<DataPoint>();
        series3 = new LineGraphSeries<DataPoint>();
        series4 = new LineGraphSeries<DataPoint>();

        //customize viewports
        graph1 = (GraphView) findViewById(R.id.graph1);
        graph1.addSeries(series1);
        viewport1 = graph1.getViewport();
        viewport1.setYAxisBoundsManual(true);
        viewport1.setMinY(minY1);
        viewport1.setMaxY(maxY1);
        viewport1.setScrollable(true);
        viewport1.setXAxisBoundsManual(true);
        viewport1.setMinX(minX);
        viewport1.setMaxX(maxX);

        graph2 = (GraphView) findViewById(R.id.graph2);
        graph2.addSeries(series2);
        viewport2 = graph2.getViewport();
        viewport2.setYAxisBoundsManual(true);
        viewport2.setMinY(minY2);
        viewport2.setMaxY(maxY2);
        viewport2.setScrollable(true);
        viewport2.setScalable(true);
        viewport2.setXAxisBoundsManual(true);
        viewport2.setMinX(minX);
        viewport2.setMaxX(maxX);

        graph3 = (GraphView) findViewById(R.id.graph3);
        graph3.addSeries(series3);
        viewport3 = graph3.getViewport();
        viewport3.setYAxisBoundsManual(true);
        viewport3.setMinY(minY3);
        viewport3.setMaxY(maxY3);
        viewport3.setScrollable(true);
        viewport3.setScalable(true);
        viewport3.setXAxisBoundsManual(true);
        viewport3.setMinX(minX);
        viewport3.setMaxX(maxX);

        graph4 = (GraphView) findViewById(R.id.graph4);
        graph4.addSeries(series4);
        viewport4 = graph4.getViewport();
        viewport4.setYAxisBoundsManual(true);
        viewport4.setMinY(minY4);
        viewport4.setMaxY(maxY4);
        viewport4.setScrollable(true);
        viewport4.setScalable(true);
        viewport4.setXAxisBoundsManual(true);
        viewport4.setMinX(minX);
        viewport4.setMaxX(maxX);

        //Battery Life Text View
        //
        //
        textViewBattery = (TextView) findViewById(R.id.textViewBattery);

        //Auto-scale button
        final Button buttonGraphs = (Button) findViewById(R.id.buttonGraphs);
        buttonGraphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Drawable btnText = buttonGraphs.getBackground();
                ColorDrawable buttonColor = (ColorDrawable) buttonGraphs.getBackground();
                if(buttonColor.getColor() == 0xffffb6c1) {
                    //buttonGraphs.setText("Auto-Scale Is On");
                    buttonGraphs.setBackgroundColor(Color.GREEN);
                    displayingGraphs = true;
                    setAutoScalingOn();
                }
                else {
                    //buttonGraphs.setText("Auto-Scale Is Off");
                    buttonGraphs.setBackgroundColor(0xffffb6c1);
                    displayingGraphs = false;
                    setAutoScalingOff();
                }
            }
        });

        //------------------
        //bluetooth
        // code for bluetooth, bluetooth code
        //
        //
        //
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (bluetooth != null) {
            System.out.println("bluetooth isn't null");

            if (!bluetooth.isEnabled()) {
                bluetooth.enable();
            }
        }
    }

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    static boolean isStreaming = false;
    static boolean startReading = false;

    private InputStream mmInStream;
    private static OutputStream mmOutStream;

    @Override
    protected void onResume() {
        super.onResume();

        //System.out.println("Resuming Main Activity");

        isStreaming = false;
        startReading = false;

        //displayingGraphs = false;

        //
        // streams data if device is connected
        //

        BluetoothDevice device = null;
        //temporarily disable
        Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice tDevice : pairedDevices) {
                device = tDevice;
            }
        }

        if(device==null) {
            System.out.println("Device null");
        }
        else
            System.out.println("address: "+device.getAddress());

        //create socket...
        try {
            btSocket = createBluetoothSocket(device);
            //Method m= device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            //btSocket = (BluetoothSocket) m.invoke(device, 1);
        } catch (Exception e) {
            System.out.println("Socket creation failed");
            //Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }

        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
            System.out.println("Socket Connected");
        } catch (IOException e) {
            System.out.println("Socket Didn't Connect");
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        } catch (Exception e) {

        }

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            //Create I/O streams for connection
            tmpIn = btSocket.getInputStream();
            tmpOut = btSocket.getOutputStream();
        } catch (IOException e) {

        } catch (Exception e) {

        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        //"write" method starts working from this point onwards

        final Button buttonStream = (Button) findViewById(R.id.buttonStream);
        buttonStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = (String) buttonStream.getText();
                if (btnText.equals("Stream")) {

                    startTime = DateFormat.getDateTimeInstance().format(new Date());
                    bigString = "";
                    bigStringIndex = 0;

                    series1 = new LineGraphSeries<DataPoint>();
                    series2 = new LineGraphSeries<DataPoint>();
                    series3 = new LineGraphSeries<DataPoint>();
                    series4 = new LineGraphSeries<DataPoint>();

                    graph1.removeAllSeries();
                    graph1.addSeries(series1);
                    viewport1 = graph1.getViewport();
                    viewport1.setYAxisBoundsManual(true);
                    viewport1.setMinY(minY1);
                    viewport1.setMaxY(maxY1);
                    viewport1.setScrollable(true);
                    viewport1.setXAxisBoundsManual(true);
                    viewport1.setMinX(minX);
                    viewport1.setMaxX(maxX);

                    graph2.removeAllSeries();
                    graph2.addSeries(series2);
                    viewport2 = graph2.getViewport();
                    viewport2.setYAxisBoundsManual(true);
                    viewport2.setMinY(minY2);
                    viewport2.setMaxY(maxY2);
                    viewport2.setScrollable(true);
                    viewport2.setScalable(true);
                    viewport2.setXAxisBoundsManual(true);
                    viewport2.setMinX(minX);
                    viewport2.setMaxX(maxX);

                    graph3.removeAllSeries();
                    graph3.addSeries(series3);
                    viewport3 = graph3.getViewport();
                    viewport3.setYAxisBoundsManual(true);
                    viewport3.setMinY(minY3);
                    viewport3.setMaxY(maxY3);
                    viewport3.setScrollable(true);
                    viewport3.setScalable(true);
                    viewport3.setXAxisBoundsManual(true);
                    viewport3.setMinX(minX);
                    viewport3.setMaxX(maxX);

                    graph4.removeAllSeries();
                    graph4.addSeries(series4);
                    viewport4 = graph4.getViewport();
                    viewport4.setYAxisBoundsManual(true);
                    viewport4.setMinY(minY4);
                    viewport4.setMaxY(maxY4);
                    viewport4.setScrollable(true);
                    viewport4.setScalable(true);
                    viewport4.setXAxisBoundsManual(true);
                    viewport4.setMinX(minX);
                    viewport4.setMaxX(maxX);

                    //mConnectedThread.write("Y");
                    MainActivity.write("Y");
                    buttonStream.setText("Stop");
                    isStreaming = true;

                    /*
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted Exception");
                    }
                    */
                } else {
                    //mConnectedThread.write("X");
                    MainActivity.write("X");
                    buttonStream.setText("Stream");
                    isStreaming = false;
                    startReading = false;
                    upload();
                }
            }
        });

        new Thread(new Runnable() {
            byte[] buffer = new byte[256]; // 256, sleep 100: 2466 E / min.
            int bytes;
            int vpYBuffer = 100;

            @Override
            public void run() {
                //we add 100 new entries
                for (int i = 0; i < 1000000; i++) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(isStreaming) {
                                try {
                                    bytes = mmInStream.read(buffer);

                                    String readMessage = new String(buffer, 0, bytes);
                                    bigString = bigString.concat(readMessage);

                                } catch (IOException e) {
                                    System.out.println("Can't read mmInStream");
                                }

                                if(!startReading&&bigString.length()>74) {

                                    try {
                                        TimeUnit.MILLISECONDS.sleep(200);
                                    } catch (InterruptedException e) {
                                        System.out.println("Interrupted Exception");
                                    }

                                    char tChar = bigString.charAt(bigStringIndex);
                                    char tChar2 = bigString.charAt(bigStringIndex + 37);

                                    System.out.println(tChar + " " +tChar2);

                                    while (tChar != 'E' || tChar2 != 'E') {
                                        bigStringIndex++;
                                        tChar = bigString.charAt(bigStringIndex);
                                        tChar2 = bigString.charAt(bigStringIndex + 37);
                                    }

                                    //System.out.println(bigStringIndex);
                                    //System.out.println(bigString);

                                    startReading = true;
                                }

                                if (startReading&&bigString.length()>(37*(dataArrIndex+1)+37*10)) {

                                    bigStringIndex += 2;
                                    String s1 = bigString.substring(bigStringIndex, bigStringIndex + 5);
                                    bigStringIndex += 7;
                                    String s2 = bigString.substring(bigStringIndex, bigStringIndex + 5);
                                    bigStringIndex += 7;
                                    String s3 = bigString.substring(bigStringIndex, bigStringIndex + 5);
                                    bigStringIndex += 7;
                                    String s4 = bigString.substring(bigStringIndex, bigStringIndex + 5);
                                    bigStringIndex += 7;
                                    //String s5 = bigString.substring(bigStringIndex, bigStringIndex + 5); //not used, blank space
                                    bigStringIndex += 7;

                                    dataArr[0][dataArrIndex] = Integer.parseInt(s1);
                                    dataArr[1][dataArrIndex] = Integer.parseInt(s2);
                                    dataArr[2][dataArrIndex] = Integer.parseInt(s3);
                                    dataArr[3][dataArrIndex] = Integer.parseInt(s4);

                                    int i1 = dataArr[0][dataArrIndex];
                                    int i2 = dataArr[1][dataArrIndex];
                                    int i3 = dataArr[2][dataArrIndex];
                                    int i4 = dataArr[3][dataArrIndex];

                                    //System.out.println(i1+" "+i2+" "+i3+" "+i4);

                                    dataArrIndex++;

                                    //Auto-scale, autoscale
                                    if(displayingGraphs) {
                                        if (dataArrIndex % 2 == 0) {
                                            series1.appendData(new DataPoint(lastX++, i1), true, maxX);
                                            series2.appendData(new DataPoint(lastX++, i2), true, maxX);
                                            series3.appendData(new DataPoint(lastX++, i3), true, maxX);
                                            series4.appendData(new DataPoint(lastX++, i4), true, maxX);
                                            graphIndex++;

                                            if (autoScaleIsOn && dataArrIndex % (25) == 0) {
                                                viewport1.setMinY(series1.getLowestValueY() - vpYBuffer);
                                                viewport1.setMaxY(series1.getHighestValueY() + vpYBuffer);
                                                viewport2.setMinY(series2.getLowestValueY() - vpYBuffer);
                                                viewport2.setMaxY(series2.getHighestValueY() + vpYBuffer);
                                                viewport3.setMinY(series3.getLowestValueY() - vpYBuffer);
                                                viewport3.setMaxY(series3.getHighestValueY() + vpYBuffer);
                                                viewport4.setMinY(series4.getLowestValueY() - vpYBuffer);
                                                viewport4.setMaxY(series4.getHighestValueY() + vpYBuffer);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });

                    // sleep to slow down addition of entries
                    try {
                        Thread.sleep(50); // display 30 points &
                    } catch (InterruptedException e) {
                        //manage error...
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onPause()
    {
        if (googleclient != null) {
            googleclient.disconnect();
        }
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            System.out.println("Bluetooth Socket Didn't Close");
            //insert code to deal with this
        } catch (Exception e) {

        }
    }

    //
    //
    // Display Device Battery Life somewhere
    // TO DO
    // TO DO
    //
    //

    // stream data + parse thread
    //
    //
    //
    public static void write(String input) {
        byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            System.out.println("wrote "+input);
        } catch (IOException e) {
            //if you cannot write, close the application
            System.out.println("Didn't Write");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        googleclient.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    // ------------------------------------------------------
    // for Google API Usage
    //
    //

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    private Bitmap mBitmapToSave;
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_IMAGE:
                // Called after a photo has been taken.
                if (resultCode == Activity.RESULT_OK) {
                    // Store the image data as a bitmap for writing later.
                    mBitmapToSave = (Bitmap) data.getExtras().get("data");
                }
                break;
            case REQUEST_CODE_CREATOR:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "File successfully saved.");
                    mBitmapToSave = null;

                    // Just start the camera again for another photo.
                    //startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                    //        REQUEST_CODE_CAPTURE_IMAGE);
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "API client connected.");
        //moved drive quickstart stuff into upload method
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    private static final String TAG = "drive";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;


    @Override
    /**
     * Called whenever the API client fails to connect.
     */
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        //final Bitmap image = mBitmapToSave;
        Drive.DriveApi.newDriveContents(googleclient)
                .setResultCallback(new ResultCallback<DriveContentsResult>() {

                    @Override
                    public void onResult(DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything and must fail
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        Log.i(TAG, "New contents created.");
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        // Write the data from it.
                        //byte[] buf = bigString.getBytes();
                        String newString = startTime+"\n"+bigString;
                        byte[] buf = newString.getBytes();
                        try {
                            outputStream.write(buf);
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        //NEW: (IN PROGRESS)
                        String fileName = DateFormat.getDateTimeInstance().format(new Date());
                        String fileTitle = fileName+".txt";
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("text/plain").setTitle(fileTitle).build();
                        //PREVIOUS:
                        //String fileTitle = fileName+".png";
                        //MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        //        .setMimeType("image/jpeg").setTitle(fileTitle).build();

                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(googleclient);
                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "Failed to launch file chooser.");
                        }
                    }
                });
    }

}
