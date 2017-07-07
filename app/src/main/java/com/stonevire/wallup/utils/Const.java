package com.stonevire.wallup.utils;

/**
 * Created by Saksham on 6/28/2017.
 */

public class Const {

    //----------------- API Endpoints ----------------------------
    private static final String DOMAIN          = "https://api.stonevire.com/wallup/";
    public static final String NEW_IMAGES       = DOMAIN + "v1/listNewImages";
    public static final String TAGGING_IMAGES   = DOMAIN + "v1/taggingList";

    public static final String UNSPLASH_ID          = "?client_id=a25247a07df2c569f6f3dc129f43b0eb3b0e3ff69b00d5b84dd031255e55b961";
    public static final String UNSPLASH_USER_RANDOM = "https://source.unsplash.com/user/";
    public static final String UNSPLASH_SEARCH      = "https://source.unsplash.com/search/photos"+UNSPLASH_ID+"&query=";

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
    public static final String USER_BIO                 = "bio";
    public static final String USER_TOTAL_LIKES         = "total_likes";
    public static final String USER_TOTAL_PHOTOS        = "total_photos";
    public static final String USER_FIRST_NAME          = "first_name";
    public static final String USER_LAST_NAME           = "last_name";
    public static final String USER_PHOTOS              = "photos";
    public static final String USER_LOCATION            = "location";
    public static final String USERNAME                 = "username";
    public static final String LINKS                    = "links";
    public static final String ERRORS                   = "errors";


    //------------------ API Variables ----------------------------

    public static final String PAGE_NO      = "page_no";
    public static final String SUCCESS      = "success";
    public static final String IMAGES       = "images";
    public static final String ERRORID      = "errorID";
    public static final String DETAILS      = "details";

    //------------------ Callbacks --------------------------------

    public static final int NEW_IMAGES_CALLBACK         = 1;
    public static final int LOAD_MORE_IMAGES_CALLBACK   = 2;
    public static final int USER_IMAGES_CALLBACK        = 3;

}
