/*
 * Copyright 2018, The Android Open Source Project
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

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        Log.i("GameFragment", "Called ViewModelProviders.of!")
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)



        binding.correctButton.setOnClickListener {
            viewModel.onCorrect()

        }
        binding.skipButton.setOnClickListener {
            viewModel.onSkip()
        }

        // From the viewModel get the live data score
        // Set an observer on the live data
        // observe() takes the lifecycle owner -
        // (UI controller associated with live data that
        // is GameFragment(this) in our case)
        // Next it takes a lambda to execute whenever
        // the live data (score) changes
        viewModel.score.observe(this, Observer { newScore ->
            binding.scoreText.text = newScore.toString()
        })

        viewModel.word.observe(this, Observer { newWord ->
            binding.wordText.text =  viewModel.word.value
        })

        viewModel.eventGameFinish.observe(this, Observer { hasFinished ->
           if (hasFinished) {
               gameFinished()
               viewModel.onGameFinishComplete()
               // Tell the viewModel that the game finished event is over so reset it to false
               // This ensures and handles the following case...
               // If the game is finished and the eventGameFinish = true now the
               // gameFinish() method is called to handle game finish. But now suppose
               // there is a configuration change, like the screen is rotated, the UI fragment
               // is destroyed and recreated, on recreation all observers of live data are triggered
               // thus UI observer for eventGameFinish is triggered which again triggers
               // gameFinished(), this is not what we want.
               // so for events which have been handled like game finish, etc we must reset the
               // state for the event to avoid re triggering the event due to a configuration change
               // when using live data.
           }
        })

        return binding.root
    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        val action = GameFragmentDirections.actionGameToScore(viewModel.score.value ?: 0)
        // viewModel.score.value ?: 0 says fetch the score live data from the view model
        // use the elvis operator(?:) not check if the score is null, if its null return 0
        findNavController(this).navigate(action)
    }


    /** Methods for buttons presses **/


    /** Methods for updating the UI **/

}
