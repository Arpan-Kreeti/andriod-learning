/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.*

// This interface enables us the map our SleepNight entity instances to
// database objects, to do so we have annotated our interface to be a DAO
@Dao
interface SleepDatabaseDao {

    // Here be define a method to insert a SleepNight data, for this we annotate our
    // insert() function with a @Insert annotation.Room will generate all
    // the necessary code to insert the passed-in SleepNight into the database.
    // Note that you can call the function anything you want
    @Insert
    fun insert(night: SleepNight)

    // During update room automatically checks the old SleepNight data in the db
    // and generated the update query for the columns or attributes that have changed
    // in the new SleepNight entity instance that is sent to update()
    @Update
    fun update(night: SleepNight)

    // To run a custom query we use the query annotation with the query string
    // Dynamic values in the query can be set by passing them to the corresponding function
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    fun get(key: Long): SleepNight?

    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    // Here room gives us a live data this means if the data gets changed in the db
    // room automatically updates the ui which is observing this live data without
    // us having to handle all the complexity
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>

    // We get the most recent data by sorting and then setting a limit 1
    // we might recieve null if the table is empty so we specify the return value
    // of getTonight() as a nullable SleepNight?
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): SleepNight?
}
