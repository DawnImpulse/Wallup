package com.stonevire.wallup.utils;

/**
 * Created by Saksham on 6/28/2017.
 */

public class Const {

    //----------------- API Endpoints ----------------------------
    private static final String DOMAIN          = "https://api.stonevire.com/wallup/";
    public static final String NEW_IMAGES       = DOMAIN + "v1/listNewImages";
    public static final String TAGGING_IMAGES   = DOMAIN + "v1/taggingList";

    //----------------- Unsplash Image Properties------------------

    public static final String IMAGE_URLS               = "urls";
    public static final String IMAGE_USER               = "user";
    public static final String IMAGE_USER_IMAGES        = "profile_image";
    public static final String IMAGE_COLOR              = "color";
    public static final String IMAGE_RAW                = "raw";
    public static final String IMAGE_USER_IMAGE_MEDIUM  = "medium";
    public static final String IMAGE_USER_NAME          = "name";
    public static final String IMAGE_LIKES              = "likes";
    public static final String LOCATION_OBJECT          = "location";
    public static final String LOCATION_TITLE           = "title";

    //------------------ API Variables ----------------------------

    public static final String PAGE_NO      = "page_no";
    public static final String SUCCESS      = "success";
    public static final String IMAGES       = "images";
    public static final String ERRORID      = "errorID";
    public static final String DETAILS      = "details";

    //------------------ Callbacks --------------------------------

    public static final int NEW_IMAGES_CALLBACK         = 1 ;
    public static final int LOAD_MORE_IMAGES_CALLBACK   = 2;

}
