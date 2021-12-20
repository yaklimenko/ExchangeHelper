package com.example.habitsexchangehelper.net

import com.example.habitsexchangehelper.entity.DateFromMilliseconds
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.joda.time.DateTime

class DateFromMillisecondsAdapter: TypeAdapter<DateFromMilliseconds>() {
    override fun write(out: JsonWriter?, value: DateFromMilliseconds?) {
        if (value != null && out != null) {
            out.name("date")
                .value(value.date.millis)
        }
    }

    override fun read(reader: JsonReader?): DateFromMilliseconds {
        return if (reader != null) {
            val millis = reader.nextLong()
            DateFromMilliseconds(
                DateTime(millis)
            )
        } else DateFromMilliseconds(DateTime(0L))
    }

}