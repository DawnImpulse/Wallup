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
 * Created by Saksham on 9/13/2017.
 */

object StringModifier {

    /**
     * Function to convert a String to Camel Case
     * @param words  - String to convert
     * @return - Converted camel case string
     */
    fun camelCase(words: String): String {
        val parts = words.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        var camelCaseString = ""
        for (part in parts) {
            camelCaseString = camelCaseString + toProperCase(part)
        }
        return camelCaseString
    }

    /**
     * Changing case of a single word to Camel Case
     * @param s - A single word String
     * @return - Converted camel case word String
     */
    internal fun toProperCase(s: String): String {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()
    }
}
