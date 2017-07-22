package com.stonevire.wallup.singleton;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.android.volley.VolleyError;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pixplicity.easyprefs.library.Prefs;
import com.stonevire.wallup.network.volley.RequestResponse;
import com.stonevire.wallup.network.volley.VolleyWrapper;
import com.stonevire.wallup.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

import timber.log.BuildConfig;
import timber.log.Timber;

import static com.stonevire.wallup.utils.Const.LIVE_IMAGES_POSITION;

/**
 * Created by Saksham on 7/19/2017.
 */

public class WallpaperServiceSingleton implements RequestResponse {

    private static WallpaperServiceSingleton mInstance;
    private Context mContext;

    Handler handler = new Handler();
    SurfaceHolder mSurfaceHolder;
    File mFile;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    VolleyWrapper mVolleyWrapper;

    boolean shouldDrawAfterFetch = false;
    boolean drawOk;
    boolean rotationChanged = false;
    int position;
    int cachedSize = 5;
    int moreImagesToFetch = 0;

    WindowManager wm;
    Point point;
    Display display;

    final Runnable drawRunner = new Runnable() {
        @Override
        public void run() {
            int rotation = display.getRotation() / 90;
            int savedRotation = Prefs.getInt(Const.LIVE_IMAGES_ROTATION, -1);
            int savedRotation1 = savedRotation / 90;

            if (savedRotation != -1) {
                if (rotation % 2 == savedRotation1 % 2)
                    drawTimeInitializer();
                else {
                    drawInitializer();
                    rotationChanged = true;
                }
            } else {
                Prefs.putInt(Const.LIVE_IMAGES_ROTATION, display.getRotation());
                drawTimeInitializer();
            }


        }

    };

    private void drawTimeInitializer() {
        Calendar calendar = Calendar.getInstance();
        Calendar current = Calendar.getInstance();

        if (Prefs.contains(Const.LIVE_IMAGES_UPCOMING_TIME)) {
            String upcoming = Prefs.getString(Const.LIVE_IMAGES_UPCOMING_TIME, "");
            if (!upcoming.equals("")) {
                try {
                    calendar.setTime(sdf.parse(upcoming));
                    if (calendar.getTimeInMillis() > current.getTimeInMillis()) {
                        long remaining = calendar.getTimeInMillis() - current.getTimeInMillis();
                        handler.postDelayed(drawRunner, remaining);
                    } else {
                        drawInitializer();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    drawInitializer();
                }
            } else
                drawInitializer();
        } else
            drawInitializer();
    }

    private void drawInitializer() {
        if (directory().listFiles().length != 0) {
            if (Prefs.contains(LIVE_IMAGES_POSITION)) {
                int temp = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 2) - 1;
                position = Prefs.getInt(LIVE_IMAGES_POSITION, temp);
            } else {
                position = 1;
            }

            Log.d("Test", String.valueOf(position));

            if (position > Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1)) {

                position = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 3) - 3;
                if (position < 0)
                    position = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 2) - 1;
                draw();

            } else if (position >= Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1) - 3 && directory().listFiles().length >= 5) {
                moreImagesToFetch = 1;
                shouldDrawAfterFetch = false;
                randomImageCall();
                draw();
            } else
                draw();
        } else {
            if (Prefs.contains(LIVE_IMAGES_POSITION))
                position = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1);
            else {
                Prefs.putInt(LIVE_IMAGES_POSITION, 1);
                position = 1;
            }

            shouldDrawAfterFetch = true;
            moreImagesToFetch = 4;
            randomImageCall();
        }
    }

    private WallpaperServiceSingleton() {
    }

    public static WallpaperServiceSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new WallpaperServiceSingleton();
        }

        return mInstance;
    }

    public void WallpaperHandler(Context mContext, SurfaceHolder surfaceHolder) {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        this.mContext = mContext;
        mVolleyWrapper = new VolleyWrapper(mContext);

        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        this.mSurfaceHolder = surfaceHolder;
        mFile = directory();
        handler.post(drawRunner);
        drawOk = true;

        if (!Prefs.contains(Const.LIVE_IMAGES_COUNT)) {
            if (directory().listFiles().length != 0) {
                File[] file = directory().listFiles();
                for (int i = 0; i < file.length; i++)
                    file[i].delete();
            }
        }
    }

    public void removeHandler() {
        handler.removeCallbacks(drawRunner);
        drawOk = false;
    }

    private void draw() {
        Canvas c = null;

        if (drawOk) {
            try {
                c = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder) {
                    if (c != null) {
                        if (rotationChanged == true) {
                            position = position - 1;
                            rotationChanged = false;
                        }
                        mFile = directory();
                        if (mFile.length() != 0) {
                            File[] files = mFile.listFiles();
                            boolean fileDrawStatus = false;

                            for (int i = 0; i < files.length; i++) {
                                if (files[i].getName().equals("wall" + position)) {
                                    try {
                                        Bitmap mBitmap = scaledBitmap(getFromInternalStorage(files[i].getName()));
                                        c.drawBitmap(mBitmap, 0, 0, null);
                                        mBitmap.recycle();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    fileDrawStatus = true;
                                    position += 1;
                                    Prefs.putInt(LIVE_IMAGES_POSITION, position);
                                    break;
                                }
                            }
                            Log.d("Test", "--" + position);
                            if (!fileDrawStatus) {
                                Log.d("Test", "Not Drawn " + position);
                                if (position != Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1) - 1)
                                    Prefs.putInt(LIVE_IMAGES_POSITION, Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1));
                            }
                        }
                    } else {
                        Log.d("Test", "Canvas is Null !!");
                    }
                }
            } catch (IllegalStateException e) {
                Log.d("Test", "Inside Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                if (c != null) mSurfaceHolder.unlockCanvasAndPost(c);
            }
        }

        //Getting Next Time Interval to show Image

        Calendar current = Calendar.getInstance();
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.SECOND, 30);
        String nextDateTime = sdf.format(calender.getTime());
        Prefs.putString(Const.LIVE_IMAGES_UPCOMING_TIME, nextDateTime);
        handler.postDelayed(drawRunner, calender.getTimeInMillis() - current.getTimeInMillis());
    }

    private void fetchImage(final boolean refresh, final int count, final Uri uri) {
        ImageRequest request = null;

        request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .build();

        final ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromCache(Uri.parse("https://source.unsplash.com/random/1980x1080"));
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(request, this);

        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
            @Override
            protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }

                CloseableReference<CloseableImage> closeableImageRef = dataSource.getResult();
                if (closeableImageRef != null && closeableImageRef.get() instanceof CloseableBitmap) {
                    try {
                        Bitmap bmp = ((CloseableBitmap) closeableImageRef.get()).getUnderlyingBitmap();
                        Timber.d("Bitmap From Closable - " + bmp.getByteCount());
                        saveToInternalStorage(bmp);

                        if (refresh) {
                            draw();
                        }

                        if (directory().listFiles().length >= cachedSize + 1) ;
                        {
                            File[] f = directory().listFiles();
                            int j = f.length - cachedSize;
                            Arrays.sort(f, new Comparator<File>() {
                                public int compare(File f1, File f2) {
                                    return Long.compare(f1.lastModified(), f2.lastModified());
                                }
                            });

                            int baseCount = Integer.parseInt(f[0].getName().replace("wall", ""));

                            if (position >= baseCount + j) {
                                for (int i = 0; i < j; i++) {
                                    if (Integer.parseInt(f[i].getName().replace("wall", "")) < position - 2)
                                        f[i].delete();
                                }
                            }
                        }
                    } finally {
                        closeableImageRef.close();
                        imagePipeline.evictFromCache(Uri.parse("https://source.unsplash.com/random/1980x1080"));
                        if (count != 0) {
                            moreImagesToFetch = count - 1;
                            shouldDrawAfterFetch = false;
                            randomImageCall();
                        }
                        //fetchImage(false, count - 1);
                    }
                }
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                fetchImage(shouldDrawAfterFetch, count, uri);

            }
        }, UiThreadImmediateExecutorService.getInstance());
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        FileOutputStream fos = null;
        int count = 1;
        try {
            count = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1);
            fos = new FileOutputStream(directoryFile("wall" + count));
            Prefs.putInt(Const.LIVE_IMAGES_COUNT, ++count);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getFromInternalStorage(String fileName) {
        Bitmap b = null;
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(directoryFile(fileName));
            b = BitmapFactory.decodeStream(fos);
            Timber.d("Bitmap From Save To Internal - ", b.getByteCount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return b;
    }

    private File directoryFile(String fileName) {
       /* ContextWrapper cw = new ContextWrapper(mContext);

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);*/
        String Path = Environment.getExternalStorageDirectory().getPath().toString() + "/Wallup";
        File directory = new File(Path);
        return new File(directory, fileName);
    }

    private File directory() {
        /*ContextWrapper cw = new ContextWrapper(mContext);
        return cw.getDir("imageDir", Context.MODE_PRIVATE);*/

        String Path = Environment.getExternalStorageDirectory().getPath().toString() + "/Wallup";
        File file = new File(Path);
        if (!file.exists()) {
            file.mkdir();
        }

        return file;
    }

    private int hcf(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    private void randomImageCall() {
        mVolleyWrapper.getCall(Const.UNSPLASH_RANDOM_CALL + "1080", Const.WALLPAPER_SERVICE_CALLBACK);
        mVolleyWrapper.setListener(this);
    }

    private Bitmap scaledBitmap(Bitmap bitmap) {
        point = new Point();
        display = wm.getDefaultDisplay();
        display.getSize(point);

        int scaleHcf, width = 0, height = 0, scaleX, scaleY;
        Bitmap b = null;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        scaleHcf = hcf(point.x, point.y);

        Log.d("Wallup", String.valueOf(scaleHcf));
        // If bitmap is null or some other problem
        if (originalWidth == 0) {
            Timber.d("Wallup", originalWidth + "x" + originalHeight);
            return null;
        }

        scaleX = ((point.x / scaleHcf) > 20) ? (point.x / scaleHcf) / 8 : (point.x / scaleHcf);
        scaleY = ((point.y / scaleHcf) > 20) ? (point.y / scaleHcf) / 8 : (point.y / scaleHcf);

        Timber.d("Test", "Original - " + originalWidth + "x" + originalHeight);

        while (width < originalWidth && height < originalHeight) {
            width += scaleX;
            height += scaleY;
        }
        Timber.d("Test", "Scaling Factor " + (point.x / scaleHcf) + "x" + (point.y / scaleHcf));
        Timber.d("Test", "First Modified - " + width + "x" + height);
        width -= scaleX;
        height -= scaleY;

        int startingPointX = (originalWidth - width) / 2;
        int startingPointY = (originalHeight - height) / 2;

        startingPointX = (startingPointX < 0) ? 0 : startingPointX;
        startingPointY = (startingPointY < 0) ? 0 : startingPointY;

        Timber.d("Test", "Second Modified - " + width + "x" + height);
        Timber.d("Test", "Starting - " + startingPointX + "x" + startingPointY);

        b = Bitmap.createBitmap(bitmap, startingPointX, startingPointY, width, height);
        Timber.d("Test", "Final - " + b.getWidth() + "x" + b.getHeight());

        Bitmap finalBitmap = Bitmap.createScaledBitmap(b, point.x, point.y, false);
        bitmap.recycle();
        b.recycle();
        return finalBitmap;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        Log.e("Wallup", String.valueOf(volleyError));
        randomImageCall();
        //Toast.makeText(mContext, "Unable To Download Images . Kindly Reapply Wallpaper !!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response, int callback) {
        try {
            if (response.has(Const.ERRORS)) {
                Log.e("Wallup", response.getString(Const.ERRORS));
                randomImageCall();
            } else {
                JSONObject urlsObject = response.getJSONObject(Const.IMAGE_URLS);
                if (urlsObject.has(Const.CUSTOM)) {
                    fetchImage(shouldDrawAfterFetch, moreImagesToFetch, Uri.parse(urlsObject.getString(Const.CUSTOM)));
                } else {
                    fetchImage(shouldDrawAfterFetch, moreImagesToFetch, Uri.parse(urlsObject.getString(Const.IMAGE_REGULAR)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONArray response, int callback) {

    }

    @Override
    public void onResponse(String response, int callback) {

    }
}
