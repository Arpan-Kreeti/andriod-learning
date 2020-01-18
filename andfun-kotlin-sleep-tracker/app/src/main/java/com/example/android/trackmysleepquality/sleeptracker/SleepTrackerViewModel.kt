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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.provider.SyncStateContract.Helpers.insert
import android.provider.SyncStateContract.Helpers.update
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao, // Our view model needs access to the db so we pass this
        application: Application) : AndroidViewModel(application) { // Our view model needs the application context to get access to strings and styles so we pass this

    // This is a view model job used to manage all our coroutines
    // This will help us to cancel all coroutines when our view model is destroyed
    private var viewModelJob = Job()

    override fun onCleared() {  // This will be called when our view model is destroyed
        super.onCleared()
        viewModelJob.cancel()   // We use a job to cancel all coroutines when the model is destroyed,
                                // thus avoiding running coroutines which don't know where to return
    }

    // Scope determines what thread the coroutine will run on
    // The scope also needs to know about the job, so to create a scope
    // we pass in a dispatcher and the job
    // Our Dispatcher is "Dispatchers.Main" that means coroutines launched
    // on the ui scope will run on the main thread
    // (this does not mean the coroutine itself will run on the main thread and block it), it means
    // We launch coroutines on UI scope since the result affects the UI

    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    // This will hold a reference to the current night
    // we make it mutable live data, so that we can change it
    private var tonight = MutableLiveData<SleepNight?>()

    private val nights = database.getAllNights()    // Returns us live data

    // This Transformation map will get executed every time nights changes
    // that is night s receive new data from the database
    val nightsString = Transformations.map(nights) { nights ->
        // we supply nights and a resource object which will give us access to our string resources
        formatNights(nights, application.resources)
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    val showSnackbarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }


    // This variable will determine when to display the start button due to code
    // android:enabled="@{sleepTrackerViewModel.startButtonVisible}"
    // Using a Transformation.map we say to change the state of the variable whenever the
    // state of tonight changes, we want the start button visible when tonight is null so
    // we do null == it (it is the first parameter to a lambda)
    val startButtonVisible = Transformations.map(tonight) {
        null == it
    }

    // cleart button is visible only when their is tonight (!=null)
    // tonight is reset when we press the stop button
    val stopButtonVisible = Transformations.map(tonight) {
        null != it
    }

    // clear button is visible only when their are nights to clear
    val clearButtonVisible = Transformations.map(nights) {
        it?.isNotEmpty()
    }

    // Set to true when user clicks stop tracking button which triggers the observer of this varible to
    // navigate to sleep quality fragment
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()

    val navigateToSleepQuality : LiveData<SleepNight>
        get() = _navigateToSleepQuality


    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    init {
        // As soon as our view model is initialized get
        // we need tonight set so we do it in an init block
        initializedTonight()
    }

    private fun initializedTonight() {
        // launching a coroutine creates the coroutine without blocking the current thread
        // in the context defined by the scope
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    // We mark it as suspend since we want to call it from inside the coroutine
    private suspend fun getTonightFromDatabase(): SleepNight? {
        // Here we define another coroutine in the IO context using the IO dispatcher
        // We switch to the IO context to utilize a thread pool for Io work
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()   // Using the DAO we fetch tonight from the DB

            // startTime and endTime are same then we are continuing with an existing night
            // otherwise if startTime != endTime then no night was started so we return null
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            // we return night
            night
        }
    }

    // User click to start tracking sleep
    // Make db call to insert new night
    fun onStartTracking() {

        uiScope.launch {

            // Get a new sleep night object,
            // this captures the current time as the start time
            val newNight = SleepNight()

            // insert into db (this will be a suspend function)
            insert(newNight)

            // update tonight variable
            tonight.value = getTonightFromDatabase()
        }

    }

    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)  // DAO method to insert
        }
    }

    fun onStopTracking() {
        uiScope.launch {
            // The return @launch annotation is used to specify the function from
            // which a return statement returns from among several nested ones.
            // Here we specify if tonight's value is nil this means the user did not
            // start sleep tracking so there's no point of stopping tracking
            // thus we return from the launch not from the lambda
            // using the return@launch annotation
            val oldNight = tonight.value ?: return@launch

            // set end time
            oldNight.endTimeMilli = System.currentTimeMillis()

            // update end time to be the current time
            update(oldNight)

            // When user clicks stop tracking update the night's end time and set it up in
            // _navigateToSleepQuality variable, this variable is a live data which is observed
            // by the fragment when this variable changes then our sleep
            // tracker fragment navigates to sleep quality fragment
            _navigateToSleepQuality.value = oldNight

        }
    }

    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)  // DAO method to insert
        }
    }

    // User wants to clear all records
    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
        }
        _showSnackbarEvent.value = true // trigger a snackbar message
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }
}

