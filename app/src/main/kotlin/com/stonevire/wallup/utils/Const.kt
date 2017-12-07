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

package com.stonevire.wallup.utils

/**
 * Created by Saksham on 6/28/2017.
 */

object Const {

    //----------------- API Endpoints ----------------------------
    private val DI_DOMAIN = "https://api.dawnimpulse.com/wallup/"
    val NEW_IMAGES = DI_DOMAIN + "v1/listNewImages"
    val TAGGING_IMAGES = DI_DOMAIN + "v1/taggingList"

    val UNSPLASH_ID = "?client_id=a25247a07df2c569f6f3dc129f43b0eb3b0e3ff69b00d5b84dd031255e55b961"
    val TRENDING_CLIENT_ID = "?client_id=320ecd79cf43b87639c8e40af4b04e8d4a5a2b98"
    //Dawn ID    - a25247a07df2c569f6f3dc129f43b0eb3b0e3ff69b00d5b84dd031255e55b961
    //Saksham ID - e1d86d6d0dcb8ce3eea9d16c712c0feed5f1e9a05e5a5f252cf5ea090a3ebc80
    val UNSPLASH_API = "https://api.unsplash.com/"
    val UNSPLASH_SOURCE = "https://source.unsplash.com/"
    val UNSPLASH_USER_RANDOM = UNSPLASH_SOURCE + "user/"
    val UNSPLASH_SEARCH = UNSPLASH_SOURCE + "search/photos" + UNSPLASH_ID + "&query="
    val UNSPLASH_LATEST_IMAGES = UNSPLASH_API + "photos" + UNSPLASH_ID + "&order_by=latest&per_page=30"
    val UNSPLASH_CURATED_IMAGES = UNSPLASH_API + "photos/curated" + UNSPLASH_ID + "&order_by=latest&per_page=30"
    val UNSPLASH_GET_PHOTO = UNSPLASH_API + "photos/"
    val UNSPLASH_RANDOM_CALL = UNSPLASH_API + "photos/random/" + UNSPLASH_ID
    val UNSPLASH_TRENDING_IMAGES = UNSPLASH_API + "photos" + UNSPLASH_ID + "&order_by=popular&per_page=30"
    val UNSPLASH_FEATURED_COLLECTIONS = UNSPLASH_API + "collections/featured" + UNSPLASH_ID + "&per_page=30"

    val TRENDING_API = DI_DOMAIN + "trending_images" + TRENDING_CLIENT_ID

    //----------------- Unsplash Image Properties------------------

    val IMAGE_ID = "id"
    val IMAGE_URLS = "urls"
    val IMAGE_USER = "user"
    val PROFILE_IMAGES = "profile_image"
    val IMAGE_COLOR = "color"
    val IMAGE_RAW = "raw"
    val IMAGE_USER_NAME = "name"
    val IMAGE_LIKES = "likes"
    val LOCATION_OBJECT = "location"
    val LOCATION_TITLE = "title"
    val USER_IMAGE_LARGE = "large"
    val USER_TOTAL_LIKES = "total_likes"
    val USER_TOTAL_PHOTOS = "total_photos"
    val USER_FIRST_NAME = "first_name"
    val USER_LAST_NAME = "last_name"
    val USER_PHOTOS = "photos"
    val USER_LOCATION = "location"
    val USERNAME = "username"
    val LINKS = "links"
    val ERRORS = "errors"
    val ERROR_MESSAGE = "errorMessage"
    val IMAGE_REGULAR = "regular"
    val EXIF = "exif"
    val CAMERA_MAKE = "make"
    val CAMERA_MODEL = "model"
    val CAMERA_SHUTTER_SPEED = "exposure_time"
    val CAMERA_FOCAL_LENGTH = "focal_length"
    val CAMERA_APERTURE = "aperture"
    val CAMERA_ISO = "iso"
    val IMAGE_CREATED = "created_at"
    val IMAGE_UPDATED_AT = "updated_at"
    val IMAGE_HEIGHT = "height"
    val IMAGE_WIDTH = "width"
    val CUSTOM = "custom"
    val USER_HTML = "html"
    val COVER_PHOTO = "cover_photo"
    val USER = "user"
    val COLLECTION_TITLE = "title"
    val TOTAL_PHOTOS = "total_photos"
    val FULL_IMAGE = "full"

    //------------------ API Variables ----------------------------

    val PAGE_NO = "page_no"
    val SUCCESS = "success"
    val IMAGES = "images"
    val ERRORID = "errorID"
    val DETAILS = "details"
    val TAGS = "tags"

    //------------------ Callbacks --------------------------------

    val TRENDING_CALLBACK = 1
    val LOAD_MORE_TRENDING_IMAGES_CALLBACK = 2
    val USER_IMAGES_CALLBACK = 3
    val LATEST_CALLBACK = 4
    val LATEST_LOAD_MORE = 5
    val CURATED_CALLBACK = 6
    val CURATED_LOAD_MORE = 7
    val IMAGE_PREVIEW_DETAIL_CALLBACK = 8
    val USER_IMAGES_LOADING_CALLBACK = 9
    val WALLPAPER_SERVICE_CALLBACK = 10
    val COLLECTIONS_FEATURED_CALLBACK = 11
    val COLLECTIONS_FEATURED_LOADING_CALLBACK = 12
    val CALLBACK_1 = 1
    val CALLBACK_2 = 2

    //------------------ Transition Names -------------------------
    val TRANS_NEW_TO_PROFILE = "trans1"
    val TRANS_NEW_TO_PROFILE_1 = "trans2"
    val TRANS_NEW_TO_PROFILE_2 = "trans3"
    val TRANS_NEW_TO_PREVIEW_3 = "trans4"
    val TRANS_LATEST_TO_PROFILE = "trans1"
    val TRANS_LATEST_TO_PROFILE_1 = "trans2"
    val TRANS_LATEST_TO_PROFILE_2 = "trans3"
    val TRANS_LATEST_TO_PREVIEW = "trans4"
    val TRANS_CURATED_TO_PROFILE = "trans1"
    val TRANS_CURATED_TO_PROFILE_1 = "trans2"
    val TRANS_CURATED_TO_PROFILE_2 = "trans3"
    val TRANS_CURATED_TO_PREVIEW = "trans4"
    val TRANS_USER_TO_PREVIEW = "trans4"

    //------------------ Live Images ------------------------------

    val LIVE_IMAGES_POSITION = "position"
    val IMAGES_CACHE_SIZE = "cache_size"
    val LIVE_IMAGES_COUNT = "count"
    val LIVE_IMAGES_UPCOMING_TIME = "upcoming_time"
    val LIVE_IMAGES_ROTATION = "rotation"

    //------------------ Others -----------------------------------

    val UTM_PARAMETERS = "?utm_source=wallup&utm_medium=referral&utm_campaign=api-credit"
    val IMAGE_OBJECT = "imageObject"
    val IS_DIRECT_OBJECT = "isDirectObject"


    val CURRENT_IMAGE_AS_WALLPAPER = "current_image_as_wallpaper"
    val MESSAGE = "message"
    val VOLLEY_ERROR = "volley_error"
    val CAUSE = "cause"
    val STACK_TRACE = "stack_trace"
    val WALLPAPER_URL = "wallpaper_url"
    val PER_PAGE = "&per_page="
    val NULL_BITMAP = "null_bitmap"
}
