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
import com.stonevire.wallup.utils.Const;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by Saksham on 7/19/2017.
 */

public class WallpaperServiceSingleton {

    private static WallpaperServiceSingleton mInstance;
    private Context mContext;

    Handler handler = new Handler();
    SurfaceHolder mSurfaceHolder;
    File mFile;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

    boolean shouldDrawAfterFetch = false;
    boolean drawOk;
    boolean earlyFetch = false;
    boolean saveDraw = false;
    int position;
    int cachedSize = 5;

    WindowManager wm;
    Point point;
    Display display;

    final Runnable drawRunner = new Runnable() {
        @Override
        public void run() {

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

    };

    private void drawInitializer() {
        if (directory().listFiles().length != 0) {
            if (Prefs.contains(Const.LIVE_IMAGES_POSITION)) {
                int temp = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 2) - 1;
                position = Prefs.getInt(Const.LIVE_IMAGES_POSITION, temp);
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
                fetchImage(false, 1);
                draw();
            } else
                draw();
            //handler.postDelayed(drawRunner, 10 * 1000);
        } else {
            if (Prefs.contains(Const.LIVE_IMAGES_POSITION))
                position = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1);
            else {
                Prefs.putInt(Const.LIVE_IMAGES_POSITION, 1);
                position = 1;
            }
            fetchImage(true, 4);

            //handler.postDelayed(drawRunner, 20 * 1000);
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

    public void wallpaperHandler(Context mContext, SurfaceHolder surfaceHolder) {
        this.mContext = mContext;

        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        point = new Point();
        display = wm.getDefaultDisplay();

        this.mSurfaceHolder = surfaceHolder;
        mFile = directory();
        handler.post(drawRunner);
        drawOk = true;
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
                        mFile = directory();
                        if (mFile.length() != 0) {
                            File[] files = mFile.listFiles();
                            boolean fileDrawStatus = false;

                            for (int i = 0; i < files.length; i++) {
                                if (files[i].getName().equals("wall" + position)) {
                                    try {
                                        c.drawBitmap(getFromInternalStorage(files[i].getName()), 0, 0, null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    fileDrawStatus = true;
                                    position += 1;
                                    Prefs.putInt(Const.LIVE_IMAGES_POSITION, position);
                                    break;
                                }
                            }
                            Log.d("Test", "--" + position);
                            if (!fileDrawStatus) {
                                Log.d("Test", "Not Drawn " + position);
                                if (position != Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1) - 1)
                                    Prefs.putInt(Const.LIVE_IMAGES_POSITION, Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1));
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

        Calendar current = Calendar.getInstance();
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.SECOND, 30);
        String nextDateTime = sdf.format(calender.getTime());
        Prefs.putString(Const.LIVE_IMAGES_UPCOMING_TIME, nextDateTime);
        handler.postDelayed(drawRunner, calender.getTimeInMillis() - current.getTimeInMillis());
    }

    private void fetchImage(final boolean refresh, final int count) {
        ImageRequest request = null;
        display.getSize(point);

        request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse("https://source.unsplash.com/random/1980x1080"))
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

                        int ow = bmp.getWidth();
                        int oh = bmp.getHeight();

                        int a = (oh / 16) * 9;

                        Bitmap b = Bitmap.createBitmap(bmp, ow / 2 - oh / 2, 0, a, oh);

                        int o1 = b.getWidth();
                        int o2 = b.getHeight();

                        Bitmap r = Bitmap.createScaledBitmap(b, point.x, point.y, true);
                        saveToInternalStorage(r);
                        r.recycle();
                        b.recycle();

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
                        if (count != 0)
                            fetchImage(false, count - 1);
                    }
                }
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

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
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
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
}
