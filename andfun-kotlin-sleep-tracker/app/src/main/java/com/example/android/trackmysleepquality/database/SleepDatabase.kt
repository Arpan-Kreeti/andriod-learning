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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


// Create a abstract class and extend RoomDatabase that will provide its implementation
// annotate the class with the @Database annotation
// The Database annotation takes an
// entity - Here you specify the classes in our database ( we have only one class)
// version - A version number, when ever you change the schema up the version number
// by default exportSchema is true and creates a schema file and keeps a version
// history of the schemas, this is helpfull for complex db that change often
@Database(entities = [SleepNight::class], version = 1,  exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    // Here we tell the database what DAO (interface) to use for communicating
    abstract val sleepDatabaseDao: SleepDatabaseDao

    companion object {

        @Volatile   // the value of a volatile variable is never cached and is guaranteed
                    // to be the same for all threads, so changes made to this variable by a certain
                    // thread are immediately visible to all threads using or accessing this varaible
        private var INSTANCE: SleepDatabase? = null

        // This method will return a instance to the sleep database, it will create one
        // if the database is not present. We want to make the SleepDatabase a SINGLETON
        // since we only want to create a single instance of the database
        fun getInstance(context: Context) : SleepDatabase {
            // Wrapping up this block of code in a synchronized block ensures that the
            // code is only accessible by a single thread at a time this avoids the case
            // when two different threads may ask for the database instance at once leaving us
            // with two connections to the database, this makes sure that the db is only initialized once
            // We pass this in synchronized so we have access to the context
            synchronized( this) {
                var instance = INSTANCE // current value of instance

                if (instance == null) {  // Check if the database is already initialized
                    instance = Room.databaseBuilder(
                            context.applicationContext, // Context
                            SleepDatabase::class.java,  // Which database to build
                            "sleep_history_database"    // Name of database
                    )

                    // The migration strategy tells us how to migrate the database schema if it
                    // changes ges without losing all the previous data
                    // Here we use a strategy to just destroy all existing data and recreate the database

                            .fallbackToDestructiveMigration()
                            .build() // build the database
                    INSTANCE = instance // Set instance to our new database
                }

                return instance
            }
        }
    }

}



