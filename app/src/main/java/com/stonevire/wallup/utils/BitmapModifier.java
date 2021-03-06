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

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

/**
 * Created by Saksham on 7/16/2017.
 */

public class BitmapModifier {

    public static Palette colorSwatch(Bitmap bitmap)
    {
        if (bitmap != null && !bitmap.isRecycled()) {
            return Palette.from(bitmap).generate();
        }
        return null;
    }
}
