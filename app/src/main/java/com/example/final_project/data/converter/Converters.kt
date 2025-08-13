package com.example.final_project.data.converter

import androidx.room.TypeConverter
import com.example.final_project.model.Comment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCommentsList(comments: List<Comment>): String {
        return gson.toJson(comments)
    }

    @TypeConverter
    fun toCommentsList(data: String): List<Comment> {
        if (data.isEmpty()) return emptyList()
        val listType = object : TypeToken<List<Comment>>() {}.type
        return gson.fromJson(data, listType)
    }
}
