package com.nhatton.myweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by nhatton on 15/3/18.
 */

public class WeatherLoader {

    private static final int DOWNLOAD_COMPLETE = 25;
    private static final int ALL_DOWNLOAD_COMPLETE = 259;

    /*
    * Gets the number of available cores
    * (not always the same as the maximum number of cores)
    */
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final int MAXIMUM_POOL_SIZE = NUMBER_OF_CORES * 2;

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // A queue of Runnables
    private final BlockingQueue<Runnable> mDownloadWorkQueue;

    private final BlockingQueue<GetWeatherTask> mGetWeatherTaskQueue;

    private ThreadPoolExecutor mDownloadThreadPool;

    private Handler mHandler;

    private WeatherAdapter mAdapter;
    private Context mContext;

    public WeatherLoader(WeatherAdapter adapter, Context context) {
        mAdapter = adapter;
        mContext = context;

        mDownloadWorkQueue = new LinkedBlockingDeque<>();

        mGetWeatherTaskQueue = new LinkedBlockingDeque<>();

        mDownloadThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);

        mHandler = new Handler(Looper.getMainLooper()) {

            /*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message msg) {
                int position = msg.arg1;
                WeatherModel weather = (WeatherModel) msg.obj;

                switch (msg.what) {
                    case DOWNLOAD_COMPLETE:
                        mAdapter.updateRow(position, weather);
                        break;
                    case ALL_DOWNLOAD_COMPLETE:
                        break;
                    default:
                        super.handleMessage(msg);
                }

            }
        };
    }

    public void load(ArrayList<String> cities){
        for(int i = 0; i < cities.size(); i++) {
            GetWeatherTask task = mGetWeatherTaskQueue.poll();
            if(null == task) {
                task = new GetWeatherTask(i, cities.get(i));
            }
            mDownloadThreadPool.execute(task);
        }
    }
    /**
     * Recycles tasks by calling their internal recycle() method and then putting them back into
     * the task queue.
     *
     * @param getWeatherTask The task to recycle
     */
    private void recycleTask(GetWeatherTask getWeatherTask) {

        // Frees up memory in the task
        getWeatherTask.recycle();

        // Puts the task object back into the queue for re-use.
        mDownloadWorkQueue.offer(getWeatherTask);
    }

    private void tuneConnection() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            adjustThreadCount(networkInfo);
        }
    }

    private void setThreadCount(int count) {
        mDownloadThreadPool.setCorePoolSize(count);
        mDownloadThreadPool.setMaximumPoolSize(count * 2);
    }

    public void adjustThreadCount(NetworkInfo info) {
        if (info == null || !info.isConnectedOrConnecting()) {
            setThreadCount(NUMBER_OF_CORES);
            return;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                setThreadCount(4);
                break;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        setThreadCount(3);
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        setThreadCount(2);
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        setThreadCount(1);
                        break;
                    default:
                        setThreadCount(NUMBER_OF_CORES);
                }
                break;
            default:
                setThreadCount(NUMBER_OF_CORES);
        }
    }

    class GetWeatherTask implements Runnable {

        private String mTarget;
        private int mPosition;

        public GetWeatherTask(int position, String city) {
            mTarget = city;
            mPosition = position;
        }

        @Override
        public void run() {
            String response = WeatherAPI.getWeatherByCity(mTarget);
            Gson gson = new Gson();
            WeatherModel weather = gson.fromJson(response, WeatherModel.class);

            Message msg = mHandler.obtainMessage(DOWNLOAD_COMPLETE, mPosition, 0, weather);
            msg.sendToTarget();
        }

        void recycle() {

        }
    }
}
