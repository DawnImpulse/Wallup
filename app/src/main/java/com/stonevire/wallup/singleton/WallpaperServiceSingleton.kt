/*
 * Copyright 2017 Saksham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stonevire.wallup.singleton

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.Display
import android.view.SurfaceHolder
import android.view.WindowManager

import com.android.volley.VolleyError
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipeline
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.pixplicity.easyprefs.library.Prefs
import com.stonevire.wallup.network.volley.RequestResponse
import com.stonevire.wallup.network.volley.VolleyWrapper
import com.stonevire.wallup.utils.Const

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Comparator

import com.stonevire.wallup.utils.Const.LIVE_IMAGES_POSITION

/**
 * Created by Saksham on 7/19/2017.
 */

/**
 * Created by DawnImpulse on 2017 07 15
 * Last Branch Update - v4A
 * Updates :
 * DawnImpulse - 2017 10 07 - v4A - Caching also changes + url changes
 */


class WallpaperServiceSingleton
/**
 * Singleton private constructor
 */
private constructor() : RequestResponse {

    //Context of this class
    private var mContext: Context? = null

    // Handler
    internal var handler = Handler()

    //For Upcoming Time Calculations
    internal var sdf = SimpleDateFormat("yyyy-MMM-dd HH:mm:ss")

    // Surface Holder
    internal var mSurfaceHolder: SurfaceHolder

    // Use to store wallpapers directory
    internal var mFile: File

    // For a random Image call to Unsplash
    internal var mVolleyWrapper: VolleyWrapper

    //Upcoming Time & Current Time calender instances
    internal var upcomingTime: Calendar
    internal var currentTime: Calendar

    //To determine if we should draw right after fetching an image
    internal var shouldDrawAfterFetch = false

    //To determine if image counter needs to be updated or not
    internal var increment = false

    //If allowed to draw on Surface or not
    internal var drawOk = true

    //Images cached size - No of images to be stored in cache
    internal var cachedSize = 5

    //No of images more to be fetched after fetching current images
    internal var moreImagesToFetch = 0

    //Current Image position to be displayed
    internal var position: Int = 0

    //Instance of window manager
    internal var wm: WindowManager

    //Used here to get screen x & y end coordinated i.e. size
    internal var point: Point

    //Display instance playing middle role for Point & Window Manager
    internal var display: Display

    /**
     * It is first to be called for drawing Bitmaps - Would be used to map preferences too
     */
    internal val drawRunner: Runnable = Runnable { countIncrementer() }

    /**
     * Determining if counter needs to be incremented or not
     */
    private fun countIncrementer() {

        //Initializing Upcoming Time Variables
        upcomingTime = Calendar.getInstance()

        //Initializing Current Variables
        currentTime = Calendar.getInstance()

        /* To check whether Upcoming Time Variable is present in Preferences
        *  1. If Yes - Get the String and convert into Calender
        *  2. If No - Call DrawInitializer
        * */
        if (Prefs.contains(Const.LIVE_IMAGES_UPCOMING_TIME)) {

            //Get the upcoming time string from Preferences
            val upcoming = Prefs.getString(Const.LIVE_IMAGES_UPCOMING_TIME, "")
            try {
                //Using the string for Calender Instance
                upcomingTime.time = sdf.parse(upcoming)

                //If upcoming time <= current -> Ready to increment & call drawInitializer
                if (upcomingTime.timeInMillis <= currentTime.timeInMillis)
                    increment = true
                drawInitializer()

            } catch (e: ParseException) {
                e.printStackTrace()
                drawInitializer()
            }

        } else
            drawInitializer()
    }

    /**
     * Initializing the current position and determining whether New Images needed to be fetched
     */
    private fun drawInitializer() {

        /* Check if there are any Images in Cache or Not
        *  1. If Yes then get the position from Preference
        *  2. If No then get download more Images (New User or Cache Clean)
        */
        if (directory().listFiles().size != 0) {

            /* Check if Position is available in Preferences or not
            *  1. If Yes then get the position
            *  2. If No then its first time - initialize position to be 1
            */
            position = Prefs.getInt(LIVE_IMAGES_POSITION, 1)

            /*  Check for position against Image counter
             *  1. If position is greater than counter -> Get last image in cache
             *  2. If position is greater than counter-3 & cache is filled -> Fetch 2 more images & draw
             *  3. If position < 0 - Get last image in Cache
             *  draw() in all cases
             *  Counter is always 1 number ahead
             */
            if (position >= Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1)) {
                position = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 2) - 1

            } else if (position >= Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1) - 3 && directory().listFiles().size >= 5) {

                //Fetching a total of 2 more Images
                moreImagesToFetch = 1
                //No need to draw after fetching
                shouldDrawAfterFetch = false
                //Call for random image from Unsplash
                randomImageCall()

            } else if (position < 0) {
                position = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 2) - 1
            }

            //We will draw in any case
            draw()

            Log.d("Wallup", "Current Position : " + position.toString())

        } else {

            /* Check if New User or Not , by checking Position variable in Preferences
            *  1. If Exists - Get the position
            *  2. Else - Initialize in position in Preferences
            *  Fetching 5 images in Cache
            */
            if (Prefs.contains(LIVE_IMAGES_POSITION))
                position = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1) - 1
            else {
                Prefs.putInt(LIVE_IMAGES_POSITION, 1)
                position = 1
            }

            //We should draw once after first image fetch
            shouldDrawAfterFetch = true
            //Total of 5 images to be fetched
            moreImagesToFetch = 4
            //Call to get random image from Unsplash
            randomImageCall()
        }
    }

    /**
     * Method to initialize many things at first call to this class
     *
     * @param mContext      - Context of Wallpaper Service
     * @param surfaceHolder - Holder we get from Wallpaper Service
     */
    fun WallpaperHandler(mContext: Context, surfaceHolder: SurfaceHolder) {
        //Initializing Context
        this.mContext = mContext
        //Initializing Volley Wrapper
        this.mVolleyWrapper = VolleyWrapper(mContext)
        //Initializing Window Manager
        this.wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        //Initializing Display
        this.display = wm.defaultDisplay
        //Initializing Surface Holder
        this.mSurfaceHolder = surfaceHolder
        //Initializing File directory variable
        this.mFile = directory()
        //Initializing draw variable
        this.drawOk = true
        //Initializing/Calling Draw Runner with Handler
        handler.post(drawRunner)

        //Deleting files from cache if its early install or data cleared etc ..
        if (!Prefs.contains(Const.LIVE_IMAGES_COUNT)) {
            if (directory().listFiles().size != 0) {
                val file = directory().listFiles()
                for (i in file.indices)
                    file[i].delete()
            }
        }
    }

    /**
     * Remove Handler from drawRunner
     */
    fun removeHandler() {
        handler.removeCallbacks(drawRunner)
        drawOk = false
    }

    /**
     * The most important function - Used to Draw bitmap on Canvas
     */
    private fun draw() {
        //Initializing Canvas object - Array so to be used inside inner class
        val c = arrayOf<Canvas>(null)
        //Initializing Paint Object - Used to show kind of fade in animation , recursive calling
        val p = Paint()
        //Initializing Visibility object - To add an alpha to paint object
        val visibility = intArrayOf(0)
        //Initializing File Draw Status - To check whether bitmap is drawn or not
        val fileDrawStatus = booleanArrayOf(false)
        //Bitmap object to store the Scaled Cached Bitmap
        var mBitmap: Bitmap?
        //Initialize a new handler for kind of showing animation
        val h = Handler()
        //Runner to show kind of animation - Call itself again & again until we reach complete visibility
        var bitmapRunner: Runnable

        //Only one instance
        synchronized(this) {
            //If allowed to draw
            if (drawOk) {
                try {
                    //Getting the files directory
                    mFile = directory()

                    //Proceed only if there are files in Cache
                    if (mFile.length() != 0L) {
                        //Get an array of all the files in directory
                        val files = mFile.listFiles()
                        //If next image should be displayed - then increment position variable
                        if (increment)
                            position += 1
                        //Loop through all the files , and if our file found then proceed
                        for (i in files.indices) {

                            //If file name matches then proceed
                            if (files[i].name == "wall" + position) {

                                //Get a scaled bitmap of the file in the Cache
                                mBitmap = scaledBitmap(getFromInternalStorage(files[i].name))

                                //if bitmap return is null
                                if (mBitmap != null) {
                                    //Lock the canvas to be used to display Bitmap
                                    c[0] = mSurfaceHolder.surface.lockCanvas(null)

                                    /*The runner is setting alpha and calling itself until the image is completely
                                * Kind of creating an illusion of fade in animation
                                * Will take a total of 255 milliseconds
                                */
                                    bitmapRunner = object : Runnable {
                                        override fun run() {
                                            //Proceed if visibility is less than 255 else unlock canvas and return from Runner
                                            if (visibility[0] < 255) {
                                                //Add 5 to visibility object
                                                visibility[0] = visibility[0] + 5
                                                //Set the visibility or alpha in paint object
                                                p.alpha = visibility[0]
                                                //draw the bitmap on canvas
                                                c[0].drawBitmap(mBitmap!!, 0f, 0f, p)
                                                //release or unlock the surface
                                                mSurfaceHolder.surface.unlockCanvasAndPost(c[0])
                                                //get a new lock on canvas
                                                c[0] = mSurfaceHolder.surface.lockCanvas(null)
                                                //call this same runner after 5 milliseconds
                                                h.postDelayed(this, 5)
                                            } else {
                                                //release or unlock the canvas
                                                mSurfaceHolder.surface.unlockCanvasAndPost(c[0])
                                                //Set visibility to 0 for next item
                                                visibility[0] = 0
                                            }
                                        }
                                    }

                                    //Start the bitmap runner
                                    bitmapRunner.run()
                                    //Bitmap is drawn hence change the status
                                    fileDrawStatus[0] = true
                                    //If next image to be switched then increment position in Preferences
                                    if (increment) {
                                        Prefs.putInt(LIVE_IMAGES_POSITION, position)
                                        //finally setting increment false since we have incremented it in Prefs
                                        increment = false
                                    }
                                    //break from the for loop...
                                    break
                                } // end of bitmap null check
                                else {
                                    deleteFile()
                                } // end og bitmap null check - else
                            } // end of if loop of file name matching
                        } // end of for loop

                        Log.d("Test", "--" + position)

                        /* Check File Drawn Status
                        *  1. If yes - Check for position variable and update accordingly
                        *  2. If no - Get/update upcoming time
                        */
                        if (!fileDrawStatus[0]) {
                            Log.d("Test", "Not Drawn " + position)
                            //File name didn't matched - clear all files and re download
                            if (directory().listFiles().size != 0) {
                                val file = directory().listFiles()
                                for (j in file.indices) {
                                    if (j == file.size - 1) {
                                        val filename = file[j].name.replace("wall", "")
                                        position = Integer.valueOf(filename)!!
                                        Prefs.putInt(Const.LIVE_IMAGES_POSITION, position)
                                        Prefs.putInt(Const.LIVE_IMAGES_COUNT, position + 1)
                                    }
                                }

                            }

                            shouldDrawAfterFetch = true
                            moreImagesToFetch = 3
                            randomImageCall()
                            /* If file is not drawn because position is either incorrect
                            *  1. Then in Preferences update position to be latest image in cache
                            */
                            /*    if (position != Prefs.getInt(Const.LIVE_IMAGES_COUNT, 2) - 3) {
                                Prefs.putInt(LIVE_IMAGES_POSITION, Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1));
                            }*/
                        } else {
                            //Sleep for desired upcoming time and/or update time in Prefs
                            upcomingTimeCheck()
                        }
                    } // end of checking the length of directory condition

                } catch (e: IllegalArgumentException) {
                    Log.d("Test", "Inside Exception")
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } // end of draw ok condition
        } // end of synchronize block
    } // end of draw function

    /**
     * Check for the upcoming time in Prefs and update/sleep accordingly to that
     */
    private fun upcomingTimeCheck() {
        //Object to store time in Preferences
        val timeInPrefs = Calendar.getInstance()
        //Current time object
        currentTime = Calendar.getInstance()
        //Object to calculate upcoming time
        upcomingTime = Calendar.getInstance()

        //Get next interval - add time in seconds
        upcomingTime.add(Calendar.SECOND, 30)
        //Parse the time in string to be easily stored in Preferences
        val nextDateTime = sdf.format(upcomingTime.time)

        /* Check whether Preferences contain the Upcoming Time Variable
         *  1. If yes - Match the current time to that and decide whether to update time in Prefs
         *              &/or make handler sleep accordingly
         *
         *  2. If no - Put the calculated next time in Prefs and make handler sleep accordingly
         */
        if (Prefs.contains(Const.LIVE_IMAGES_UPCOMING_TIME)) {
            try {

                //Set the timeInPrefs object by getting time from Preferences
                timeInPrefs.time = sdf.parse(Prefs.getString(Const.LIVE_IMAGES_UPCOMING_TIME, ""))

                /* Check whether time in Prefs in smaller or equal to current time
                *  If yes -> Update Preference variable with next time and sleep
                *  If no -> Sleep for desired time which is timeInPrefs - current time
                * */
                if (timeInPrefs.timeInMillis <= currentTime.timeInMillis) {
                    Prefs.putString(Const.LIVE_IMAGES_UPCOMING_TIME, nextDateTime)
                    handler.postDelayed(drawRunner, upcomingTime.timeInMillis - currentTime.timeInMillis)

                } else
                    handler.postDelayed(drawRunner, timeInPrefs.timeInMillis - currentTime.timeInMillis)

            } catch (e: ParseException) {
                e.printStackTrace()
            }

        } else {
            //Add new variable in Prefs
            Prefs.putString(Const.LIVE_IMAGES_UPCOMING_TIME, nextDateTime)
            //Sleep for desired time
            handler.postDelayed(drawRunner, upcomingTime.timeInMillis - currentTime.timeInMillis)
        }
    }// end of upcomingTimeCheck function

    /**
     * Method to fetch individual image - Get the bitmap
     *
     * @param refresh - To draw after getting image or not
     * @param count   - No of more images needed to be fetched
     * @param uri     - URI / URL of the image (direct)
     */
    private fun fetchImage(refresh: Boolean, count: Int, uri: Uri) {

        //This object is responsible for actual call to the URI
        var request: ImageRequest? = null
        //Build the request=
        request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .build()
        //Create Fresco image Pipeline
        val imagePipeline = Fresco.getImagePipeline()
        //Create DataSource , CloseableReference object
        val dataSource = imagePipeline.fetchDecodedImage(request, this)
        //Subscribe to the data source for the image
        dataSource.subscribe(object : BaseDataSubscriber<CloseableReference<CloseableImage>>() {
            override fun onNewResultImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
                if (!dataSource.isFinished) {
                    return
                }

                //Get closeable image from data source
                val closeableImageRef = dataSource.result

                //Check whether the reference is not null and instance of Closeable Bitmap
                if (closeableImageRef != null && closeableImageRef.get() is CloseableBitmap) {
                    try {

                        //Get the actual bitmap
                        val bmp = (closeableImageRef.get() as CloseableBitmap).underlyingBitmap
                        //If bitmap is not null then save it into Cache
                        if (bmp != null)
                            saveToInternalStorage(bmp)
                        //If required then call the draw() method directly
                        if (refresh)
                            draw()

                        //In case the length of directory is greater than cached size then delete extra images
                        if (directory().listFiles().size >= cachedSize + 1);
                        run {
                            //Get files array
                            val f = directory().listFiles()
                            //variable to get no of images to delete
                            val j = f.size - cachedSize
                            //sort the file array based on modified date
                            Arrays.sort(f) { f1, f2 -> java.lang.Long.compare(f1.lastModified(), f2.lastModified()) }
                            //get the starting position of the cache
                            val baseCount = Integer.parseInt(f[0].name.replace("wall", ""))
                            //Only delete in case the position is greater than the no of images to delete
                            if (position >= baseCount + j) {
                                //start deleting the images
                                for (i in 0 until j) {
                                    if (Integer.parseInt(f[i].name.replace("wall", "")) < position - 2)
                                        f[i].delete()
                                }
                            }

                        }//end of extra images check condition

                    } finally {

                        //close the reference
                        closeableImageRef.close()
                        //Check if need to fetch more images
                        if (count != 0) {
                            //decreasing counter
                            moreImagesToFetch = count - 1
                            //no need to draw now after fetching
                            shouldDrawAfterFetch = false
                            //call to random Image
                            randomImageCall()
                        }
                        //fetchImage(false, count - 1);
                    } // end of finally
                }// end of check for closeable reference is null
            } // end of new result impl

            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
                fetchImage(shouldDrawAfterFetch, count, uri)

            }

        }, UiThreadImmediateExecutorService.getInstance())
    } // end of fetchImage

    /**
     * To save Bitmap in Internal Cache
     *
     * @param bitmapImage - The actual Bitmap
     */
    private fun saveToInternalStorage(bitmapImage: Bitmap?) {
        var fos: FileOutputStream? = null
        var count = 1
        try {
            count = Prefs.getInt(Const.LIVE_IMAGES_COUNT, 1)
            fos = FileOutputStream(directoryFile("wall" + count))
            Prefs.putInt(Const.LIVE_IMAGES_COUNT, ++count)
            bitmapImage!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * To get Bitmap from Cache
     *
     * @param fileName - Name of the file in Cache
     * @return - Actual Bitmap
     */
    private fun getFromInternalStorage(fileName: String): Bitmap? {

        //Initialize bitmap object
        var b: Bitmap? = null
        //Initialize File Stream object
        var fos: FileInputStream? = null
        try {
            //Instantiate the stream object with File object from directory with filename
            fos = FileInputStream(directoryFile(fileName))
            //decode the stream object for bitmap
            b = BitmapFactory.decodeStream(fos)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                //close stream object
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return b
    }// end of get from internal

    /**
     * Get the absolute path File or a given filename
     *
     * @param fileName - Name of the file
     * @return File object of that path
     */
    private fun directoryFile(fileName: String): File {
        val cw = ContextWrapper(mContext)
        val directory = cw.getDir("images", Context.MODE_PRIVATE)
        return File(directory, fileName)
        /*String Path = Environment.getExternalStorageDirectory().getPath().toString() + "/Wallup";
        File directory = new File(Path);
        return new File(directory, fileName);*/
    }

    /**
     * Get the File path object for Directory where images needed to be stored
     *
     * @return - File object
     */
    private fun directory(): File {
        val cw = ContextWrapper(mContext)
        return cw.getDir("images", Context.MODE_PRIVATE)

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
    private fun hcf(width: Int, height: Int): Int {
        var width = width
        var height = height
        while (height != 0) {
            val t = height
            height = width % height
            width = t
        }
        return width
    }

    /**
     * Call to unsplash to get Random Image Object
     */
    private fun randomImageCall() {
        mVolleyWrapper.getCall(Const.UNSPLASH_RANDOM_CALL + "&h=1080", Const.WALLPAPER_SERVICE_CALLBACK)
        mVolleyWrapper.setListener(this)
    }

    /**
     * Scaling the bitmap to device size
     *
     * @param bitmap - Bitmap to be scale
     * @return - Scaled Bitmap
     */
    private fun scaledBitmap(bitmap: Bitmap?): Bitmap? {

        //Instantiate Point object
        point = Point()
        //Instantiate display object
        display = wm.defaultDisplay
        //The point now has display dimens
        display.getSize(point)

        val scaleHcf: Int
        var width = 0
        var height = 0
        val scaleX: Int
        val scaleY: Int
        var b: Bitmap? = null

        //original width of bitmap
        val originalWidth = bitmap!!.width
        //original height of bitmap
        val originalHeight = bitmap.height
        //hcf of the display resolution
        scaleHcf = hcf(point.x, point.y)

        Log.d("Wallup", "Display HCF - " + scaleHcf.toString())

        // If bitmap is null or some other problem
        if (originalWidth == 0) {
            return null
        }

        /* Get X & Y scaling increment factor
        *  If ratio i.e. hcf is less than 20 then use it else divide it by 8
        */
        scaleX = if (point.x / scaleHcf > 20) point.x / scaleHcf / 8 else point.x / scaleHcf
        scaleY = if (point.y / scaleHcf > 20) point.y / scaleHcf / 8 else point.y / scaleHcf

        //Loop while incrementing width and height by scaling factors
        while (width < originalWidth && height < originalHeight) {
            width += scaleX
            height += scaleY
        }

        //Decrease one scaling factor so it wont exceed the max bitmap length
        width -= scaleX
        height -= scaleY
        //Get the starting point to crop the original Bitmap
        var startingPointX = (originalWidth - width) / 2
        var startingPointY = (originalHeight - height) / 2
        // if we get starting point less than 0 then make it 0
        startingPointX = if (startingPointX < 0) 0 else startingPointX
        startingPointY = if (startingPointY < 0) 0 else startingPointY

        //Create cropped bitmap
        b = Bitmap.createBitmap(bitmap, startingPointX, startingPointY, width, height)
        //Create final scaled bitmap based on exact screen size
        val finalBitmap = Bitmap.createScaledBitmap(b!!, point.x, point.y, false)

        //Recycle original bitmap
        bitmap.recycle()
        //Recycle cropped bitmap
        b.recycle()

        return finalBitmap
    } // end of scaled Bitmap

    /**
     * Delete file from internal storage
     */
    private fun deleteFile() {
        val f = directory().listFiles()

        for (i in f.indices) {
            if (f[i].name == "wall" + position) {
                f[i].delete()
                break
            }
        }
    }

    override fun onErrorResponse(volleyError: VolleyError, callback: Int) {
        Log.e("Wallup", volleyError.toString())
        //Toast.makeText(mContext, "Unable To Download Images . Kindly Reapply Wallpaper !!", Toast.LENGTH_SHORT).show();
    }

    override fun onResponse(response: JSONObject, callback: Int) {
        try {
            if (response.has(Const.ERRORS)) {
                Log.e("Wallup", response.getString(Const.ERRORS))
                //randomImageCall();
            } else {
                val urlsObject = response.getJSONObject(Const.IMAGE_URLS)
                if (urlsObject.has(Const.CUSTOM)) {
                    fetchImage(shouldDrawAfterFetch, moreImagesToFetch, Uri.parse(urlsObject.getString(Const.CUSTOM)))
                } else {
                    fetchImage(shouldDrawAfterFetch, moreImagesToFetch, Uri.parse(urlsObject.getString(Const.IMAGE_REGULAR)))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun onResponse(response: JSONArray, callback: Int) {

    }

    override fun onResponse(response: String, callback: Int) {

    }

    companion object {

        //Instance of this class
        private var mInstance: WallpaperServiceSingleton? = null

        /**
         * To get a single instance of Wallpaper Service
         *
         * @return
         */
        val instance: WallpaperServiceSingleton
            get() {
                if (mInstance == null) {
                    mInstance = WallpaperServiceSingleton()
                }

                return mInstance
            }
    }
}
