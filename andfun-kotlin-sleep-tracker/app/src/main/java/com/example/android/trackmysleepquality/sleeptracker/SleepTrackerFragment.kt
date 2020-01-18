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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {



        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)


        // We get a reference to the current application
        // requireNotNull() is a kotlin function that throws an error
        // if the object passed is null.
        val application =  requireNotNull(this.activity).application

        // Create an instance of DAO and pass it the application as context
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        // Call the view model factory, which in turn creates a reference to the view model
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        // With the view model factory we request ViewModelProvider to create an instance of viewmodel
        val sleepTrackerViewModel =
                ViewModelProviders.of(
                        this, viewModelFactory).get(SleepTrackerViewModel::class.java)

        // Attach the live view to the varible defined as
        //        <variable
        //        name="sleepTrackerViewModel"
        //        type="com.example.android.trackmysleepquality.sleeptracker.SleepTrackerViewModel" />

        binding.sleepTrackerViewModel = sleepTrackerViewModel

        // Set the current activity as the life cycle owner of binding
        // This will allow the binding top observe live data changes
        binding.setLifecycleOwner((this))

        // When the navigateToSleepQuality quality variable changes we navigate and then
        // call doneNavigation() to reset the variable

        sleepTrackerViewModel.navigateToSleepQuality.observe(this, Observer {
            night ->
            night?.let {
                this.findNavController().navigate(
                        SleepTrackerFragmentDirections
                                .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                // Here when navigating from the sleep tracker to sleep quality fragment we pass
                // the current night id as a parameter. This will be used by the sleep quality
                // fragment to update the night's sleep quality.
                sleepTrackerViewModel.doneNavigating()
            }
        })

        sleepTrackerViewModel.showSnackbarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                        activity!!.findViewById(android.R.id.content),
                        getString(R.string.cleared_message),
                        Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                sleepTrackerViewModel.doneShowingSnackbar()
            }
        })


        return binding.root
    }
}
