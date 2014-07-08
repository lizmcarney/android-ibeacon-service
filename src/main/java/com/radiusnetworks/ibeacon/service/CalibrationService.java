package com.radiusnetworks.ibeacon.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.radiusnetworks.bluetooth.BluetoothCrashResolver;
import com.radiusnetworks.ibeacon.BuildConfig;

import java.lang.ref.WeakReference;

public class CalibrationService extends Service {
    public static final String TAG = "CalibrationService";
    private int bindCount = 0;
    private BluetoothCrashResolver bluetoothCrashResolver;

    public CalibrationService() {
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "binding");
        bindCount++;
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "unbinding");
        bindCount--;
        return false;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "ConfigutationService version "+ BuildConfig.VERSION_NAME+" is beginning calibration");
        bluetoothCrashResolver = new BluetoothCrashResolver(this);
        bluetoothCrashResolver.start();
        //TODO: boot up the config
    }

    @Override
    @TargetApi(18)
    public void onDestroy() {
        if (android.os.Build.VERSION.SDK_INT < 18) {
            Log.w(TAG, "Not supported prior to API 18.");
            return;
        }
        bluetoothCrashResolver.stop();
        Log.i(TAG, "onDestroy called. done calibrating.");
    }

    /* CLIENT METHODS */
    public void startCalibration(){
        Log.d(TAG, "Starting calibration...");
        //TODO: start calibration
    }

    public void stopCalibration(){
        Log.d(TAG, "Halting calibration...");
        //TODO: stop calibration
    }

    /**
     * Command to the service to display a message
     */
    public static final int MSG_START_CALIBRATION = 2;
    public static final int MSG_STOP_CALIBRATION = 3;

    static class IncomingHandler extends Handler {
        private final WeakReference<CalibrationService> mService;

        IncomingHandler(CalibrationService service) {
            mService = new WeakReference<CalibrationService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            CalibrationService service = mService.get();
            StartRMData startRMData = (StartRMData) msg.obj;

            if (service != null) {
                switch (msg.what) {
                    case MSG_START_CALIBRATION:
                        service.startCalibration();
                        break;
                    case MSG_STOP_CALIBRATION:
                        service.stopCalibration();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }
}
