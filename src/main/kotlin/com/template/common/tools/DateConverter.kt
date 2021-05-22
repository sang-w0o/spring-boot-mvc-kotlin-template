package com.template.common.tools

import java.time.LocalDateTime

class DateConverter {
    companion object {
        fun convertDateWithTime(localDateTime: LocalDateTime): String {
            return "${localDateTime.year}-${localDateTime.monthValue}-${localDateTime.dayOfMonth} ${localDateTime.hour}:${localDateTime.minute}"
        }
    }
}