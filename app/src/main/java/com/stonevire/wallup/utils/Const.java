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

package com.stonevire.wallup.utils;

/**
 * Created by Saksham on 6/28/2017.
 */

public class Const {

    //----------------- API Endpoints ----------------------------
    private static final String DI_DOMAIN = "https://api.dawnimpulse.com/wallup/";
    public static final String NEW_IMAGES = DI_DOMAIN + "v1/listNewImages";
    public static final String TAGGING_IMAGES = DI_DOMAIN + "v1/taggingList";

    public static final String UNSPLASH_ID = "?client_id=a25247a07df2c569f6f3dc129f43b0eb3b0e3ff69b00d5b84dd031255e55b961";
    public static final String TRENDING_CLIENT_ID = "?client_id=320ecd79cf43b87639c8e40af4b04e8d4a5a2b98";
    //Dawn ID    - a25247a07df2c569f6f3dc129f43b0eb3b0e3ff69b00d5b84dd031255e55b961
    //Saksham ID - e1d86d6d0dcb8ce3eea9d16c712c0feed5f1e9a05e5a5f252cf5ea090a3ebc80
    public static final String UNSPLASH_API = "https://api.unsplash.com/";
    public static final String UNSPLASH_SOURCE = "https://source.unsplash.com/";
    public static final String UNSPLASH_USER_RANDOM = UNSPLASH_SOURCE + "user/";
    public static final String UNSPLASH_SEARCH = UNSPLASH_SOURCE + "search/photos" + UNSPLASH_ID + "&query=";
    public static final String UNSPLASH_LATEST_IMAGES = UNSPLASH_API + "photos" + UNSPLASH_ID + "&order_by=latest&per_page=30";
    public static final String UNSPLASH_CURATED_IMAGES = UNSPLASH_API + "photos/curated" + UNSPLASH_ID + "&order_by=latest&per_page=30";
    public static final String UNSPLASH_GET_PHOTO = UNSPLASH_API + "photos/";
    public static final String UNSPLASH_RANDOM_CALL = UNSPLASH_API + "photos/random/" + UNSPLASH_ID;
    public static final String UNSPLASH_TRENDING_IMAGES = UNSPLASH_API + "photos" + UNSPLASH_ID + "&order_by=popular&per_page=30";
    public static final String UNSPLASH_FEATURED_COLLECTIONS = UNSPLASH_API + "collections/featured" + UNSPLASH_ID + "&per_page=30";

    public static final String TRENDING_API = DI_DOMAIN + "trending_images" + TRENDING_CLIENT_ID;

    //----------------- Unsplash Image Properties------------------

    public static final String IMAGE_ID = "id";
    public static final String IMAGE_URLS = "urls";
    public static final String IMAGE_USER = "user";
    public static final String PROFILE_IMAGES = "profile_image";
    public static final String IMAGE_COLOR = "color";
    public static final String IMAGE_RAW = "raw";
    public static final String IMAGE_USER_NAME = "name";
    public static final String IMAGE_LIKES = "likes";
    public static final String LOCATION_OBJECT = "location";
    public static final String LOCATION_TITLE = "title";
    public static final String USER_IMAGE_LARGE = "large";
    public static final String USER_TOTAL_LIKES = "total_likes";
    public static final String USER_TOTAL_PHOTOS = "total_photos";
    public static final String USER_FIRST_NAME = "first_name";
    public static final String USER_LAST_NAME = "last_name";
    public static final String USER_PHOTOS = "photos";
    public static final String USER_LOCATION = "location";
    public static final String USERNAME = "username";
    public static final String LINKS = "links";
    public static final String ERRORS = "errors";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String IMAGE_REGULAR = "regular";
    public static final String EXIF = "exif";
    public static final String CAMERA_MAKE = "make";
    public static final String CAMERA_MODEL = "model";
    public static final String CAMERA_SHUTTER_SPEED = "exposure_time";
    public static final String CAMERA_FOCAL_LENGTH = "focal_length";
    public static final String CAMERA_APERTURE = "aperture";
    public static final String CAMERA_ISO = "iso";
    public static final String IMAGE_CREATED = "created_at";
    public static final String IMAGE_UPDATED_AT = "updated_at";
    public static final String IMAGE_HEIGHT = "height";
    public static final String IMAGE_WIDTH = "width";
    public static final String CUSTOM = "custom";
    public static final String USER_HTML = "html";
    public static final String COVER_PHOTO = "cover_photo";
    public static final String USER = "user";
    public static final String COLLECTION_TITLE = "title";
    public static final String TOTAL_PHOTOS = "total_photos";
    public static final String FULL_IMAGE = "full";

    //------------------ API Variables ----------------------------

    public static final String PAGE_NO = "page_no";
    public static final String SUCCESS = "success";
    public static final String IMAGES = "images";
    public static final String ERRORID = "errorID";
    public static final String DETAILS = "details";
    public static final String TAGS = "tags";

    //------------------ Callbacks --------------------------------

    public static final int TRENDING_CALLBACK = 1;
    public static final int LOAD_MORE_TRENDING_IMAGES_CALLBACK = 2;
    public static final int USER_IMAGES_CALLBACK = 3;
    public static final int LATEST_CALLBACK = 4;
    public static final int LATEST_LOAD_MORE = 5;
    public static final int CURATED_CALLBACK = 6;
    public static final int CURATED_LOAD_MORE = 7;
    public static final int IMAGE_PREVIEW_DETAIL_CALLBACK = 8;
    public static final int USER_IMAGES_LOADING_CALLBACK = 9;
    public static final int WALLPAPER_SERVICE_CALLBACK = 10;
    public static final int COLLECTIONS_FEATURED_CALLBACK = 11;
    public static final int COLLECTIONS_FEATURED_LOADING_CALLBACK = 12;
    public static final int CALLBACK_1 = 1;

    //------------------ Transition Names -------------------------
    public static final String TRANS_NEW_TO_PROFILE = "trans1";
    public static final String TRANS_NEW_TO_PROFILE_1 = "trans2";
    public static final String TRANS_NEW_TO_PROFILE_2 = "trans3";
    public static final String TRANS_NEW_TO_PREVIEW_3 = "trans4";
    public static final String TRANS_LATEST_TO_PROFILE = "trans1";
    public static final String TRANS_LATEST_TO_PROFILE_1 = "trans2";
    public static final String TRANS_LATEST_TO_PROFILE_2 = "trans3";
    public static final String TRANS_LATEST_TO_PREVIEW = "trans4";
    public static final String TRANS_CURATED_TO_PROFILE = "trans1";
    public static final String TRANS_CURATED_TO_PROFILE_1 = "trans2";
    public static final String TRANS_CURATED_TO_PROFILE_2 = "trans3";
    public static final String TRANS_CURATED_TO_PREVIEW = "trans4";
    public static final String TRANS_USER_TO_PREVIEW = "trans4";

    //------------------ Live Images ------------------------------

    public static final String LIVE_IMAGES_POSITION = "position";
    public static final String IMAGES_CACHE_SIZE = "cache_size";
    public static final String LIVE_IMAGES_COUNT = "count";
    public static final String LIVE_IMAGES_UPCOMING_TIME = "upcoming_time";
    public static final String LIVE_IMAGES_ROTATION = "rotation";

    //------------------ Others -----------------------------------

    public static final String UTM_PARAMETERS = "?utm_source=wallup&utm_medium=referral&utm_campaign=api-credit";
    public static final String IMAGE_OBJECT = "imageObject";
    public static final String IS_DIRECT_OBJECT = "isDirectObject";


    public static final String CURRENT_IMAGE_AS_WALLPAPER = "current_image_as_wallpaper";
    public static final String MESSAGE = "message";
    public static final String VOLLEY_ERROR = "volley_error";
    public static final String CAUSE = "cause";
    public static final String STACK_TRACE = "stack_trace";
    public static final String WALLPAPER_URL = "wallpaper_url";
    public static final String PER_PAGE = "&per_page=";
}
