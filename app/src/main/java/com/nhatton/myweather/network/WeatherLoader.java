package com.nhatton.myweather.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nhatton.myweather.db.model.WeatherDomain;
import com.nhatton.myweather.network.model.WeatherModel;
import com.nhatton.myweather.ui.main.WeatherAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by nhatton on 15/3/18.
 */

public class WeatherLoader {

    private int oldListSize = 0;

    public interface WeatherLoadListener {
        void onLoadComplete(SparseArray<WeatherDomain> cacheList);

        void onLoadError(String error);
    }

    private static final String TAG = "WeatherLoader";

    private static final int DOWNLOAD_COMPLETE = 25;
    private static final int ICON_COMPLETE = 2509;
    private static final int ICON_EXIST = 2590;
    private static final int ALL_DOWNLOAD_COMPLETE = 2592;

    private static final int BITMAP_QUALITY = 85;
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

    private WeatherLoadListener mListener;

    private WeatherAdapter mAdapter;

    private Context mContext;

    private SparseArray<WeatherDomain> resultList;

    public WeatherLoader(WeatherAdapter adapter, Context context, WeatherLoadListener listener) {
        mAdapter = adapter;
        mContext = context;
        mListener = listener;

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
                GetWeatherTask task = (GetWeatherTask) msg.obj;

                switch (msg.what) {
                    case DOWNLOAD_COMPLETE:
                        mAdapter.updateRow(position, task.getWeatherData());
                        if(resultList.size() == mAdapter.getItemCount() - oldListSize){
                            mListener.onLoadComplete(resultList);
                        }
                        break;
                    case ICON_COMPLETE:
                        mAdapter.updateRowIcon(task.getPosition());
                        recycleTask(task);
                        break;
                    case ICON_EXIST:
                        recycleTask(task);
                        break;
                    default:
                        super.handleMessage(msg);
                }

            }
        };
    }

    public void sync(SparseArray<String> citiesMap) {
        oldListSize = 0;
        resultList = new SparseArray<>();

        tuneConnection();

        for (int i = 0; i < citiesMap.size(); i++) {

            int position = citiesMap.keyAt(i);

            GetWeatherTask task = mGetWeatherTaskQueue.poll();
            if (null == task) {
                task = new GetWeatherTask();
            }
            task.initialize(position, citiesMap.get(position));
            mDownloadThreadPool.execute(task);
        }
        mDownloadThreadPool.shutdown();

//        try {
//            mDownloadThreadPool.awaitTermination(mAdapter.getItemCount() * 10, TimeUnit.SECONDS);
//            mListener.onLoadComplete(resultList);
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            mListener.onLoadError(e.getMessage());
//        }

        mDownloadThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
    }

    public void load(ArrayList<String> cities) {
        oldListSize = mAdapter.getItemCount() - cities.size();

        resultList = new SparseArray<>();

        tuneConnection();

        for (int i = 0; i < cities.size(); i++) {
            GetWeatherTask task = mGetWeatherTaskQueue.poll();
            if (null == task) {
                task = new GetWeatherTask();
            }
            task.initialize(i + oldListSize, cities.get(i));
            mDownloadThreadPool.execute(task);
        }
        mDownloadThreadPool.shutdown();

//        try {
//            mDownloadThreadPool.awaitTermination(mAdapter.getItemCount() * 10, TimeUnit.SECONDS);
//            mListener.onLoadComplete(resultList);
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            mListener.onLoadError(e.getMessage());
//        }

        mDownloadThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
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

    public void setAdapter(WeatherAdapter weatherAdapter) {
        mAdapter = weatherAdapter;
    }

    private void tuneConnection() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            adjustThreadCount(networkInfo);
            if (mAdapter.getItemCount() < mDownloadThreadPool.getCorePoolSize()) {
                mDownloadThreadPool.setCorePoolSize(mAdapter.getItemCount());
            }
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
        private WeatherDomain mWeather;

        public GetWeatherTask() {

        }

        public void initialize(int position, String city) {
            mTarget = city;
            mPosition = position;
        }

        @Override
        public void run() {
            Log.i(TAG, "Getting weather data for " + mTarget);
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            String response = WeatherAPI.getWeatherByCity(mTarget);
            Gson gson = new Gson();
            WeatherModel weatherModel = gson.fromJson(response, WeatherModel.class);

            if (weatherModel != null) {
                mWeather = new WeatherDomain(weatherModel);

                resultList.append(mPosition, mWeather);

                Message msg = mHandler.obtainMessage(DOWNLOAD_COMPLETE, mPosition, 0, this);
                msg.sendToTarget();

                if (saveIconToLocal()) {
                    msg = mHandler.obtainMessage(ICON_COMPLETE, this);
                    msg.sendToTarget();
                } else {
                    msg = mHandler.obtainMessage(ICON_EXIST, this);
                    msg.sendToTarget();
                }
            } else {
                Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
            }

            Thread.interrupted();
        }

        private boolean saveIconToLocal() {
            String path = Environment.getExternalStorageDirectory().toString();
            String iconName = mWeather.getIcon() + ".png";
            String url = WeatherAPI.IMG_ROOT + iconName;

            try {
                File file = new File(path, iconName);
                if (file.exists()) {
                    return false;
                } else {
                    InputStream is = new URL(url).openStream();
                    Bitmap icon = BitmapFactory.decodeStream(is);
                    is.close();

                    OutputStream os = new FileOutputStream(file);
                    icon.compress(Bitmap.CompressFormat.PNG, BITMAP_QUALITY, os);
                    os.flush();
                    os.close();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                mListener.onLoadError(e.getMessage());
                return false;
            }
        }

        public int getPosition() {
            return mPosition;
        }

        public WeatherDomain getWeatherData() {
            return mWeather;
        }

        void recycle() {
            mPosition = -1;
            mWeather = null;
        }
    }
}
