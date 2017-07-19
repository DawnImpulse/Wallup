package com.stonevire.wallup.services;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.pixplicity.easyprefs.library.Prefs;
import com.stonevire.wallup.singleton.WallpaperServiceSingleton;
import com.stonevire.wallup.utils.Const;

import java.io.File;

/**
 * Created by Saksham on 7/18/2017.
 */

public class LiveImagesService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        if (!Fresco.hasBeenInitialized())
            Fresco.initialize(this);
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        return new LiveImagesServiceEngine();
    }

    private class LiveImagesServiceEngine extends Engine {


        private Handler handler = new Handler();
        File mFile;

        private boolean visible = true;
        boolean drawOk;
        boolean earlyFetch = false;
        boolean saveDraw = false;
        int position;
        int cachedSize = 5;

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        Display display = wm.getDefaultDisplay();


        /*private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                if (directory().listFiles().length != 0) {
                    position = Prefs.getInt(Const.LIVE_IMAGES_POSITION, 0);
                    Log.d("Test", String.valueOf(position));
                    if (position > directory().listFiles().length) {
                        position = 0;
                        draw();
                    } else if (position == directory().listFiles().length - 2) {
                        fetchImage(false);
                        fetchImage(false);
                        fetchImage(false);
                        fetchImage(false);
                        draw();
                    } else
                        draw();
                    handler.postDelayed(drawRunner, 10 * 1000);
                } else {
                    fetchImage(true);
                    fetchImage(false);
                    fetchImage(false);
                    fetchImage(false);
                    fetchImage(false);
                    handler.postDelayed(drawRunner, 20 * 1000);
                }
            }

        };*/

        public LiveImagesServiceEngine() {
            cachedSize = Prefs.getInt(Const.LIVE_IMAGES_CACHE_SIZE, 5);
            //mFile = directory();
//            handler.post(drawRunner);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.d("Test", "Surface Created");
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            WallpaperServiceSingleton.getInstance().removeHandler();
            Log.d("Test", "Surface Destroyed");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                //handler.post(drawRunner);
                WallpaperServiceSingleton.getInstance().wallpaperHandler(getApplicationContext(),getSurfaceHolder());
                drawOk = true;
                Log.d("Test", "Surface Visibility True");
            } else {
                //handler.removeCallbacks(drawRunner);
                WallpaperServiceSingleton.getInstance().removeHandler();
                drawOk = false;
                Log.d("Test", "Surface Visibility False");
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d("Test", "Surface Changed");
        }

        @Override
        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            super.onSurfaceRedrawNeeded(holder);
        }

       /* private void draw() {
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;

            if (drawOk) {
                try {
                    c = holder.lockCanvas();
                    if (c != null) {
                        mFile = directory();
                        if (mFile.length() != 0) {
                            File[] files = mFile.listFiles();
                            Arrays.sort(files, new Comparator<File>() {
                                public int compare(File f1, File f2) {
                                    return Long.compare(f1.lastModified(), f2.lastModified());
                                }
                            });

                            if (position >= files.length) {
                                c.drawBitmap(getFromInternalStorage(files[0].getName()), 0, 0, null);
                                Prefs.putInt(Const.LIVE_IMAGES_POSITION, 1);
                            } else {
                                c.drawBitmap(getFromInternalStorage(files[position].getName()), 0, 0, null);

                                String tempName = files[position].getName();
                                File[] tempFiles = directory().listFiles();
                                int tempPos = 0;

                                Arrays.sort(tempFiles, new Comparator<File>() {
                                    public int compare(File f1, File f2) {
                                        return Long.compare(f1.lastModified(), f2.lastModified());
                                    }
                                });

                                String a = null;
                                for (int i = 0; i < tempFiles.length; i++) {
                                    a += "-" + tempFiles[i].getName();
                                }
                                Log.d("Test", a);

                                for (int i = 0; i < tempFiles.length; i++) {
                                    if (tempFiles[i].getName().equals(tempName)) {
                                        if (i == tempFiles.length - 1)
                                            tempPos = 0;
                                        else
                                            tempPos = i + 1;
                                        break;
                                    }
                                }
                                Prefs.putInt(Const.LIVE_IMAGES_POSITION, tempPos);
                            }
                        } else {
                            fetchImage(true);
                            fetchImage(false);
                        }
                    } else {
                        Log.d("Test", "Canvas is Null !!");
                    }
                } catch (IllegalStateException e) {
                    Log.d("Test", "Inside Exception");
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) holder.unlockCanvasAndPost(c);
                }
            }
        }

        private void fetchImage(final boolean refresh) {
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
                            //r.recycle();
                            b.recycle();

                            if (refresh) {
                                draw();
                            }

                            if (directory().listFiles().length >= cachedSize + 1) ;
                            {
                                int j = directory().listFiles().length - cachedSize;
                                if (position >= j) {
                                    File[] f = directory().listFiles();
                                    Arrays.sort(f, new Comparator<File>() {
                                        public int compare(File f1, File f2) {
                                            return Long.compare(f1.lastModified(), f2.lastModified());
                                        }
                                    });
                                    for (int i = 0; i < j; i++) {
                                        f[i].delete();
                                    }
                                }
                            }
                        } finally {
                            closeableImageRef.close();
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
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            return new File(directory, fileName);
        }

        private File directory() {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            return cw.getDir("imageDir", Context.MODE_PRIVATE);
        }*/
    }
}
