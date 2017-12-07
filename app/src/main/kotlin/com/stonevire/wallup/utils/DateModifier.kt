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

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Saksham on 7/12/2017.
 */

object DateModifier {

    fun toDateFullMonthYear(date: String): String {
        val sdf = SimpleDateFormat("yyyy-mm-dd")
        val sdfNew = SimpleDateFormat("dd MMMM, yyyy")
        try {
            val old = sdf.parse(date)
            return sdfNew.format(old)
        } catch (e: ParseException) {
            return date
        }

    }

    fun toDateFullMonthYearHourMinutes(date: String): String {
        val sdf = SimpleDateFormat("yyyy-mm-dd")
        val sdfNew = SimpleDateFormat("dd MMMM, yyyy  HH:mm")
        try {
            val old = sdf.parse(date)
            return sdfNew.format(old)
        } catch (e: ParseException) {
            return date
        }

    }
}
