package com.stonevire.wallup.utils;

/**
 * Created by Saksham on 6/28/2017.
 */

public class Const {

    private static final String DOMAIN          = "https://api.stonevire.com/wallup/";
    public static final String NEW_IMAGES      = DOMAIN + "v1/listNewImages";
    public static final String TAGGING_IMAGES  = DOMAIN + "v1/taggingList";

    //------------------ API Variables ----------------------------

    public static final String PAGE_NO      = "pageNo";
    public static final String SUCCESS      = "success";
    public static final String DETAILS      = "details";
    public static final String IMAGES       = "images";
    public static final String ERRORID      = "errorID";

    //------------------ Callbacks --------------------------------

    public static final int NEW_IMAGES_CALLBACK         = 1 ;
    public static final int LOAD_MORE_IMAGES_CALLBACK   = 2;

}
