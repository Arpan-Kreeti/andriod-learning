package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {


    // MutableLiveData allows us to both read and write
    // where as only LiveData only allows us to perform reads
    // Since we want out LiveData as read only in our GameFragment
    // but as both Readable and writable in GameViewModel
    // This is because following the architecture the UI Fragment
    // should be able to write or mutate the live data directly
    // To achieve this we use a backing property here...
    // _score is our MutableLiveData to be used in GameViewModel
    // whereas private score is a read only LiveData that will be
    // used by the UI that is GameFragment
    private val _score = MutableLiveData<Int>()
    // We can set it has val beacuse be never change the
    // MutableLiveData object we change its properties like score or word

    // Backing property defined as public but readonly not mutable to be
    // used in the UI that is GameFragment
    val score: LiveData<Int>
        get() = _score



    // The current word defined as mutable live data
    private val _word = MutableLiveData<String>()

    val word: LiveData<String>
        get() = _word

    // Set to true when game is finished, holds game finish state
    private val _eventGameFinish = MutableLiveData<Boolean>()

    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        resetList()
        nextWord()
        _score.value = 0 // Initialize score in live data to 0, using score =
                        // automatically calls setValue()
                        // to set the value to the live data
        _word.value = ""
        Log.i("GameViewModel", "GameViewModel created !")
    }

    // Called when the associated fragment is finally permanently destroyed(like on back button press)
    // (not when its destoyed due to some configuration change like phone rotation)
    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed!")
    }



    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        if (wordList.isEmpty()) {
            // No more words to display means game has finished
            _eventGameFinish.value = true
        } else {
            // Select and remove a word from the list
            _word.value = wordList.removeAt(0)
        }
    }

    fun onSkip() {
        _score.value = (_score.value)?.minus(1)
        // we did not do score.value = score.value - 1 here as
        // the score is a nullable field, so we must have a null check
        // If score is null minus() is not called due to the '?' null check
        nextWord()
    }

     fun onCorrect() {
        _score.value = (_score.value)?.plus(1)
        nextWord()
    }

    // When the game has finished, the UI will call this method to reset the
    // game finish state to say that "the game finish event has been handled to reset it"
    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }
}