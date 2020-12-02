package com.example.weatherapp.data.internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface WeatherDao {
    @Query("SELECT * FROM posts_table")
    fun getPosts(): Observable<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(postEntity: PostEntity?)
}