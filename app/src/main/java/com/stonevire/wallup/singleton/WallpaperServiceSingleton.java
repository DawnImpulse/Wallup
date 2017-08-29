package com.stonevire.wallup.singleton;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
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

import static com.stonevire.wallup.utils.Const.LIVE_IMAGES_POSITION;

/**
 * Created by Saksham on 7/19/2017.
 */

public class WallpaperServiceSingleton implements RequestResponse {

    //Instance of this class
    private static WallpaperServiceSingleton mInstance;

    //Context of this class
    private Context mContext;

    // Handler
    Handler handler = new Handler();

    //For Upcoming Time Calculations
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

    // Surface Holder
    SurfaceHolder mSurfaceHolder;

    // Use to store wallpapers directory
    File mFile;

    // For a random Image call to Unsplash
    VolleyWrapper mVolleyWrapper;

    //Upcoming Time & Current Time calender instances
    Calendar upcomingTime, currentTime;

    //To determine if we should draw right after fetching an image
    boolean shouldDrawAfterFetch = false;

    //To determine if image counter needs to be updated or not
    boolean increment = false;

    //If allowed to draw on Surface or not
    boolean drawOk = true;

    //Images cached size - No of images to be stored in cache
    int cachedSize = 5;

    //No of images more to be fetched after fetching current images
    int moreImagesToFetch = 0;

    //Current Image position to be displayed
    int position;

    //Instance of window manager
    WindowManager wm;

    //Used here to get screen x & y end coordinated i.e. size
    Point point;

    //Display instance playing middle role for Point & Window Manager
    Display display;

    /**
     * It is first to be called for drawing Bitmaps - Would be used to map preferences too
     */
    final Runnable drawRunner = new Runnable() {
        @Override
        public void run() {
            countIncrementer();
        }

    };

    /**
     * Determining if counter needs to be incremented or not
     */
    private void countIncrementer() {

        //Initializing Upcoming Time Variables
        upcomingTime = Calendar.getInstance();

        //Initializing Current Variables
        currentTime = Calendar.getInstance();

        /* To check whether Upcoming Time Variable is present in Preferences
        *  1. If Yes - Get the String and convert into Calender
        *  2. If No - Call DrawInitializer
        * */
        if (Prefs.contains(Const.LIVE_IMAGES_UPCOMING_TIME)) {

            //Get the upcoming time string from Preferences
            String upcoming = Prefs.getString(Const.LIVE_IMAGES_UPCOMING_TIME, "");
            try {
                //Using the string for Calender Instance
                upcomingTime.setTime(sdf.parse(upcoming));

                //If upcoming time <= current -> Ready to increment & call drawInitializer
                if (upcomingTime.getTimeInMillis() <= currentTime.getTimeInMillis())
                    increment = true;
                drawInitializer();

            } catch (ParseException e) {
                e.printStackTrace();
                drawInitializer();
            }

        } else
            drawInitializer();
    }

    /**
     * Initializing the current position and determining whether New Images needed to be fetched
     */
    private void drawInitializer() {

        /* Check if there are any Images in Cache or Not
        *  1. If Yes then get the position from Preference
        *  2. If No then get download more Images (New User or Cache Clean)
        */
        if (directory().listFiles().length != 0) {

            /* Check if Position is available in Preferences or not
            *  1. If Yes then get the position
            *  2. If No then its first time - initialize position to be 1
            */
            position = Prefs.getInt(LIVE_IMAGES_POSITION, 1);

            /*  Check for position against Image counter
             *  1. If position is greater than counter -> Get last image in cache
             *  2. If position is greater than counter-3 & cache is filled -> Fetch 2 more images & draw
             *  3. If position < 0 - Get last image in Cache
             *  draw() in all cases
             *  Counter is always 1 number ahead
             */
            if (position >= Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1)) {
                position = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 2) - 1;

            } else if (position >= Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1) - 3 && directory().listFiles().length >= 5) {

                //Fetching a total of 2 more Images
                moreImagesToFetch = 1;
                //No need to draw after fetching
                shouldDrawAfterFetch = false;
                //Call for random image from Unsplash
                randomImageCall();

            } else if (position < 0) {
                position = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 2) - 1;
            }

            //We will draw in any case
            draw();

            Log.d("Wallup", "Current Position : " + String.valueOf(position));

        } else {

            /* Check if New User or Not , by checking Position variable in Preferences
            *  1. If Exists - Get the position
            *  2. Else - Initialize in position in Preferences
            *  Fetching 5 images in Cache
            */
            if (Prefs.contains(LIVE_IMAGES_POSITION))
                position = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1) - 1;
            else {
                Prefs.putInt(LIVE_IMAGES_POSITION, 1);
                position = 1;
            }

            //We should draw once after first image fetch
            shouldDrawAfterFetch = true;
            //Total of 5 images to be fetched
            moreImagesToFetch = 4;
            //Call to get random image from Unsplash
            randomImageCall();
        }
    }

    /**
     * Singleton private constructor
     */
    private WallpaperServiceSingleton() {
    }

    /**
     * To get a single instance of Wallpaper Service
     *
     * @return
     */
    public static WallpaperServiceSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new WallpaperServiceSingleton();
        }

        return mInstance;
    }

    /**
     * Method to initialize many things at first call to this class
     *
     * @param mContext      - Context of Wallpaper Service
     * @param surfaceHolder - Holder we get from Wallpaper Service
     */
    public void WallpaperHandler(Context mContext, SurfaceHolder surfaceHolder) {
        //Initializing Context
        this.mContext = mContext;
        //Initializing Volley Wrapper
        this.mVolleyWrapper = new VolleyWrapper(mContext);
        //Initializing Window Manager
        this.wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //Initializing Display
        this.display = wm.getDefaultDisplay();
        //Initializing Surface Holder
        this.mSurfaceHolder = surfaceHolder;
        //Initializing File directory variable
        this.mFile = directory();
        //Initializing draw variable
        this.drawOk = true;
        //Initializing/Calling Draw Runner with Handler
        handler.post(drawRunner);

        //Deleting files from cache if its early install or data cleared etc ..
        if (!Prefs.contains(Const.LIVE_IMAGES_COUNT)) {
            if (directory().listFiles().length != 0) {
                File[] file = directory().listFiles();
                for (int i = 0; i < file.length; i++)
                    file[i].delete();
            }
        }
    }

    /**
     * Remove Handler from drawRunner
     */
    public void removeHandler() {
        handler.removeCallbacks(drawRunner);
        drawOk = false;
    }

    /**
     * The most important function - Used to Draw bitmap on Canvas
     */
    private void draw() {
        //Initializing Canvas object - Array so to be used inside inner class
        final Canvas[] c = {null};
        //Initializing Paint Object - Used to show kind of fade in animation , recursive calling
        final Paint p = new Paint();
        //Initializing Visibility object - To add an alpha to paint object
        final int[] visibility = {0};
        //Initializing File Draw Status - To check whether bitmap is drawn or not
        final boolean[] fileDrawStatus = {false};
        //Bitmap object to store the Scaled Cached Bitmap
        Bitmap mBitmap;
        //Initialize a new handler for kind of showing animation
        final Handler h = new Handler();
        //Runner to show kind of animation - Call itself again & again until we reach complete visibility
        final Runnable bitmapRunner;

        //Only one instance
        synchronized (this) {
            //If allowed to draw
            if (drawOk) {
                try {
                    //Getting the files directory
                    mFile = directory();

                    //Proceed only if there are files in Cache
                    if (mFile.length() != 0) {
                        //Get an array of all the files in directory
                        File[] files = mFile.listFiles();
                        //If next image should be displayed - then increment position variable
                        if (increment)
                            position += 1;
                        //Loop through all the files , and if our file found then proceed
                        for (int i = 0; i < files.length; i++) {

                            //If file name matches then proceed
                            if (files[i].getName().equals("wall" + position)) {

                                //Get a scaled bitmap of the file in the Cache
                                mBitmap = scaledBitmap(getFromInternalStorage(files[i].getName()));

                                //if bitmap return is null
                                if (mBitmap != null) {
                                    //Lock the canvas to be used to display Bitmap
                                    c[0] = mSurfaceHolder.getSurface().lockCanvas(null);

                                /*The runner is setting alpha and calling itself until the image is completely
                                * Kind of creating an illusion of fade in animation
                                * Will take a total of 255 milliseconds
                                */
                                    final Bitmap finalMBitmap = mBitmap;
                                    bitmapRunner = new Runnable() {
                                        @Override
                                        public void run() {
                                            //Proceed if visibility is less than 255 else unlock canvas and return from Runner
                                            if (visibility[0] < 255) {
                                                //Add 5 to visibility object
                                                visibility[0] = visibility[0] + 5;
                                                //Set the visibility or alpha in paint object
                                                p.setAlpha(visibility[0]);
                                                //draw the bitmap on canvas
                                                c[0].drawBitmap(finalMBitmap, 0, 0, p);
                                                //release or unlock the surface
                                                mSurfaceHolder.getSurface().unlockCanvasAndPost(c[0]);
                                                //get a new lock on canvas
                                                c[0] = mSurfaceHolder.getSurface().lockCanvas(null);
                                                //call this same runner after 5 milliseconds
                                                h.postDelayed(this, 5);
                                            } else {
                                                //release or unlock the canvas
                                                mSurfaceHolder.getSurface().unlockCanvasAndPost(c[0]);
                                                //Set visibility to 0 for next item
                                                visibility[0] = 0;
                                            }
                                        }
                                    };

                                    //Start the bitmap runner
                                    bitmapRunner.run();
                                    //Bitmap is drawn hence change the status
                                    fileDrawStatus[0] = true;
                                    //If next image to be switched then increment position in Preferences
                                    if (increment) {
                                        Prefs.putInt(LIVE_IMAGES_POSITION, position);
                                        //finally setting increment false since we have incremented it in Prefs
                                        increment = false;
                                    }
                                    //break from the for loop...
                                    break;
                                } // end of bitmap null check
                                else {
                                    deleteFile();
                                } // end og bitmap null check - else
                            } // end of if loop of file name matching
                        } // end of for loop

                        Log.d("Test", "--" + position);

                        /* Check File Drawn Status
                        *  1. If yes - Check for position variable and update accordingly
                        *  2. If no - Get/update upcoming time
                        */
                        if (!fileDrawStatus[0]) {
                            Log.d("Test", "Not Drawn " + position);

                            /* If file is not drawn because position is either incorrect
                            *  1. Then in Preferences update position to be latest image in cache
                            */
                            if (position != Prefs.getInt(Const.LIVE_IMAGES_COUNT, 2) - 1) {
                                Prefs.putInt(LIVE_IMAGES_POSITION, Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1));
                            }
                        } else {
                            //Sleep for desired upcoming time and/or update time in Prefs
                            upcomingTimeCheck();
                        }
                    } // end of checking the length of directory condition

                } catch (IllegalArgumentException e) {
                    Log.d("Test", "Inside Exception");
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // end of draw ok condition
        } // end of synchronize block
    } // end of draw function

    /**
     * Check for the upcoming time in Prefs and update/sleep accordingly to that
     */
    private void upcomingTimeCheck() {
        //Object to store time in Preferences
        Calendar timeInPrefs = Calendar.getInstance();
        //Current time object
        currentTime = Calendar.getInstance();
        //Object to calculate upcoming time
        upcomingTime = Calendar.getInstance();

        //Get next interval - add time in seconds
        upcomingTime.add(Calendar.SECOND, 30);
        //Parse the time in string to be easily stored in Preferences
        String nextDateTime = sdf.format(upcomingTime.getTime());

        /* Check whether Preferences contain the Upcoming Time Variable
         *  1. If yes - Match the current time to that and decide whether to update time in Prefs
         *              &/or make handler sleep accordingly
         *
         *  2. If no - Put the calculated next time in Prefs and make handler sleep accordingly
         */
        if (Prefs.contains(Const.LIVE_IMAGES_UPCOMING_TIME)) {
            try {

                //Set the timeInPrefs object by getting time from Preferences
                timeInPrefs.setTime(sdf.parse(Prefs.getString(Const.LIVE_IMAGES_UPCOMING_TIME, "")));

                /* Check whether time in Prefs in smaller or equal to current time
                *  If yes -> Update Preference variable with next time and sleep
                *  If no -> Sleep for desired time which is timeInPrefs - current time
                * */
                if (timeInPrefs.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                    Prefs.putString(Const.LIVE_IMAGES_UPCOMING_TIME, nextDateTime);
                    handler.postDelayed(drawRunner, upcomingTime.getTimeInMillis() - currentTime.getTimeInMillis());

                } else
                    handler.postDelayed(drawRunner, timeInPrefs.getTimeInMillis() - currentTime.getTimeInMillis());

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            //Add new variable in Prefs
            Prefs.putString(Const.LIVE_IMAGES_UPCOMING_TIME, nextDateTime);
            //Sleep for desired time
            handler.postDelayed(drawRunner, upcomingTime.getTimeInMillis() - currentTime.getTimeInMillis());
        }
    }// end of upcomingTimeCheck function

    /**
     * Method to fetch individual image - Get the bitmap
     *
     * @param refresh - To draw after getting image or not
     * @param count   - No of more images needed to be fetched
     * @param uri     - URI / URL of the image (direct)
     */
    private void fetchImage(final boolean refresh, final int count, final Uri uri) {

        //This object is responsible for actual call to the URI
        ImageRequest request = null;
        //Build the request=
        request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .build();
        //Create Fresco image Pipeline
        final ImagePipeline imagePipeline = Fresco.getImagePipeline();
        //Create DataSource , CloseableReference object
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(request, this);
        //Subscribe to the data source for the image
        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
            @Override
            protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }

                //Get closeable image from data source
                CloseableReference<CloseableImage> closeableImageRef = dataSource.getResult();

                //Check whether the reference is not null and instance of Closeable Bitmap
                if (closeableImageRef != null && closeableImageRef.get() instanceof CloseableBitmap) {
                    try {

                        //Get the actual bitmap
                        Bitmap bmp = ((CloseableBitmap) closeableImageRef.get()).getUnderlyingBitmap();
                        //If bitmap is not null then save it into Cache
                        if (bmp != null)
                            saveToInternalStorage(bmp);
                        //If required then call the draw() method directly
                        if (refresh)
                            draw();

                        //In case the length of directory is greater than cached size then delete extra images
                        if (directory().listFiles().length >= cachedSize + 1) ;
                        {
                            //Get files array
                            File[] f = directory().listFiles();
                            //variable to get no of images to delete
                            int j = f.length - cachedSize;
                            //sort the file array based on modified date
                            Arrays.sort(f, new Comparator<File>() {
                                public int compare(File f1, File f2) {
                                    return Long.compare(f1.lastModified(), f2.lastModified());
                                }
                            });
                            //get the starting position of the cache
                            int baseCount = Integer.parseInt(f[0].getName().replace("wall", ""));
                            //Only delete in case the position is greater than the no of images to delete
                            if (position >= baseCount + j) {
                                //start deleting the images
                                for (int i = 0; i < j; i++) {
                                    if (Integer.parseInt(f[i].getName().replace("wall", "")) < position - 2)
                                        f[i].delete();
                                }
                            }

                        }//end of extra images check condition

                    } finally {

                        //close the reference
                        closeableImageRef.close();
                        //Check if need to fetch more images
                        if (count != 0) {
                            //decreasing counter
                            moreImagesToFetch = count - 1;
                            //no need to draw now after fetching
                            shouldDrawAfterFetch = false;
                            //call to random Image
                            randomImageCall();
                        }
                        //fetchImage(false, count - 1);
                    } // end of finally
                }// end of check for closeable reference is null
            } // end of new result impl

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                fetchImage(shouldDrawAfterFetch, count, uri);

            }

        }, UiThreadImmediateExecutorService.getInstance());
    } // end of fetchImage

    /**
     * To save Bitmap in Internal Cache
     *
     * @param bitmapImage - The actual Bitmap
     */
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

    /**
     * To get Bitmap from Cache
     *
     * @param fileName - Name of the file in Cache
     * @return - Actual Bitmap
     */
    private Bitmap getFromInternalStorage(String fileName) {

        //Initialize bitmap object
        Bitmap b = null;
        //Initialize File Stream object
        FileInputStream fos = null;
        try {
            //Instantiate the stream object with File object from directory with filename
            fos = new FileInputStream(directoryFile(fileName));
            //decode the stream object for bitmap
            b = BitmapFactory.decodeStream(fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //close stream object
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return b;
    }// end of get from internal

    /**
     * Get the absolute path File or a given filename
     *
     * @param fileName - Name of the file
     * @return File object of that path
     */
    private File directoryFile(String fileName) {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        return new File(directory, fileName);
        /*String Path = Environment.getExternalStorageDirectory().getPath().toString() + "/Wallup";
        File directory = new File(Path);
        return new File(directory, fileName);*/
    }

    /**
     * Get the File path object for Directory where images needed to be stored
     *
     * @return - File object
     */
    private File directory() {
        ContextWrapper cw = new ContextWrapper(mContext);
        return cw.getDir("images", Context.MODE_PRIVATE);

        /*String Path = Environment.getExternalStorageDirectory().getPath().toString() + "/Wallup";
        File file = new File(Path);
        if (!file.exists()) {
            file.mkdir();
        }

        return file;*/
    }

    /**
     * Get hcf of width & height
     *
     * @param width  - Width of device
     * @param height - Height of device
     * @return
     */
    private int hcf(int width, int height) {
        while (height != 0) {
            int t = height;
            height = width % height;
            width = t;
        }
        return width;
    }

    /**
     * Call to unsplash to get Random Image Object
     */
    private void randomImageCall() {
        mVolleyWrapper.getCall(Const.UNSPLASH_RANDOM_CALL + "1920&h=1080", Const.WALLPAPER_SERVICE_CALLBACK);
        mVolleyWrapper.setListener(this);
    }

    /**
     * Scaling the bitmap to device size
     *
     * @param bitmap - Bitmap to be scale
     * @return - Scaled Bitmap
     */
    private Bitmap scaledBitmap(Bitmap bitmap) {

        //Instantiate Point object
        point = new Point();
        //Instantiate display object
        display = wm.getDefaultDisplay();
        //The point now has display dimens
        display.getSize(point);

        int scaleHcf, width = 0, height = 0, scaleX, scaleY;
        Bitmap b = null;

        //original width of bitmap
        int originalWidth = bitmap.getWidth();
        //original height of bitmap
        int originalHeight = bitmap.getHeight();
        //hcf of the display resolution
        scaleHcf = hcf(point.x, point.y);

        Log.d("Wallup", "Display HCF - " + String.valueOf(scaleHcf));

        // If bitmap is null or some other problem
        if (originalWidth == 0) {
            return null;
        }

        /* Get X & Y scaling increment factor
        *  If ratio i.e. hcf is less than 20 then use it else divide it by 8
        */
        scaleX = ((point.x / scaleHcf) > 20) ? (point.x / scaleHcf) / 8 : (point.x / scaleHcf);
        scaleY = ((point.y / scaleHcf) > 20) ? (point.y / scaleHcf) / 8 : (point.y / scaleHcf);

        //Loop while incrementing width and height by scaling factors
        while (width < originalWidth && height < originalHeight) {
            width += scaleX;
            height += scaleY;
        }

        //Decrease one scaling factor so it wont exceed the max bitmap length
        width -= scaleX;
        height -= scaleY;
        //Get the starting point to crop the original Bitmap
        int startingPointX = (originalWidth - width) / 2;
        int startingPointY = (originalHeight - height) / 2;
        // if we get starting point less than 0 then make it 0
        startingPointX = (startingPointX < 0) ? 0 : startingPointX;
        startingPointY = (startingPointY < 0) ? 0 : startingPointY;

        //Create cropped bitmap
        b = Bitmap.createBitmap(bitmap, startingPointX, startingPointY, width, height);
        //Create final scaled bitmap based on exact screen size
        Bitmap finalBitmap = Bitmap.createScaledBitmap(b, point.x, point.y, false);

        //Recycle original bitmap
        bitmap.recycle();
        //Recycle cropped bitmap
        b.recycle();

        return finalBitmap;
    } // end of scaled Bitmap

    /**
     * Delete file from internal storage
     */
    private void deleteFile() {
        File[] f = directory().listFiles();

        for(int i=0 ; i<f.length; i++)
        {
            if (f[i].getName().equals("wall" + position)){
                f[i].delete();
                break;
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        Log.e("Wallup", String.valueOf(volleyError));
        //Toast.makeText(mContext, "Unable To Download Images . Kindly Reapply Wallpaper !!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response, int callback) {
        try {
            if (response.has(Const.ERRORS)) {
                Log.e("Wallup", response.getString(Const.ERRORS));
                //randomImageCall();
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
