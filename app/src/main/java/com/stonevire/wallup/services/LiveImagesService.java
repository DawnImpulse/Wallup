package com.stonevire.wallup.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
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

import java.util.ArrayList;

/**
 * Created by Saksham on 7/18/2017.
 */

public class LiveImagesService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        Fresco.initialize(this);
        return new LiveImagesServiceEngine();
    }

    private class LiveImagesServiceEngine extends Engine {
        ArrayList<Bitmap> bitmapList;
        private Handler handler = new Handler();
        private boolean visible = true;
        int i = 0;

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        Display display = wm.getDefaultDisplay();

        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
                handler.postDelayed(drawRunner, 20*1000);
            }

        };

        public LiveImagesServiceEngine() {
            ImageRequest request = null;
            bitmapList = new ArrayList<Bitmap>();
            display.getSize(point);
            
            request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse("https://source.unsplash.com/random/1980x1080"))
                    .build();

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
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
                        Bitmap bmp = ((CloseableBitmap) closeableImageRef.get()).getUnderlyingBitmap();

                        int ow = bmp.getWidth();
                        int oh = bmp.getHeight();

                        int a = (oh / 16) * 9;

                        Bitmap b = Bitmap.createBitmap(bmp, ow/2 - oh/2, 0, a, oh);

                        int o1 = b.getWidth();
                        int o2 = b.getHeight();

                        /*final float horizontalScaleFactor = (float) o1 / (float) point.x;
                        final float verticalScaleFactor = (float) o2 / (float) point.y;

                        Log.d("Test", String.valueOf(horizontalScaleFactor) + " -- " + verticalScaleFactor);
                        final float scaleFactor = Math.max(verticalScaleFactor, horizontalScaleFactor);
                        final int finalWidth = (int) (o1 / scaleFactor);
                        final int finalHeight = (int) (o2 / scaleFactor);*/

                        /*final Bitmap wallpaperBmp = Bitmap.createScaledBitmap(
                                bmp, o1, o2, true);

                        bmp.recycle();*/

                        Bitmap r = Bitmap.createScaledBitmap(b,point.x,point.y,true);
                        b.recycle();
                        bitmapList.add(r);

                        handler.post(drawRunner);
                    }
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                }
            }, UiThreadImmediateExecutorService.getInstance());
        }

        private void draw() {
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;

            try {
                c = holder.lockCanvas();
                Log.d("Test", "Inside Try");
                if (c != null) {
                    if (i == 0) {
                        c.drawBitmap(bitmapList.get(0), 0, 0, null);
                        //i++;
                    } else {
                        c.drawBitmap(bitmapList.get(1), 0, 0, null);
                        i--;
                    }
                }
            } catch (IllegalStateException e) {
                Log.d("Test", "Inside Exception");
                e.printStackTrace();
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

        }
    }
}
