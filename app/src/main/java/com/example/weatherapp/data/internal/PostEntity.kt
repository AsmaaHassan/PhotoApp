package com.example.weatherapp.data.internal

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "posts_table")
data class PostEntity(
    @PrimaryKey() val image_path: String,
    val date: String
)