package com.example.android.guesstheword.screens.game

import android.os.Build
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.service.autofill.Transformation
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)


class GameViewModel : ViewModel() {

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }


    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 15000L
        // Panic buzz starting when time is almost up
        const val COUNTDOWN_PANIC_SECONDS = 5000L
    }


    private val timer: CountDownTimer

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
    // We can set it has val because be never change the
    // MutableLiveData object we change its properties like score or word

    // Backing property defined as public but readonly not mutable to be
    // used in the UI that is GameFragment
    val score: LiveData<Int>
        get() = _score



    // The current word defined as mutable live data
    private val _word = MutableLiveData<String>()

    val word: LiveData<String>
        get() = _word

    private val _timeRemaining = MutableLiveData<Long>()

    val timeRemainingString: LiveData<String> = Transformations.map(_timeRemaining) { time ->
        DateUtils.formatElapsedTime(time)
    }

    // Set to true when game is finished, holds game finish state
    private val _eventGameFinish = MutableLiveData<Boolean>()

    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish


    private val _eventBuzz = MutableLiveData<BuzzType>()

    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        _score.value = 0 // Initialize score in live data to 0, using score =
                        // automatically calls setValue()
                        // to set the value to the live data
        _word.value = ""

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _timeRemaining.value = millisUntilFinished/ONE_SECOND

                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                _timeRemaining.value = DONE
                _eventBuzz.value = BuzzType.GAME_OVER
                _eventGameFinish.value = true
            }
        }

        timer.start()

        resetList()
        nextWord()

        Log.i("GameViewModel", "GameViewModel created !")
    }

    // Called when the associated fragment is finally permanently destroyed(like on back button press)
    // (not when its destoyed due to some configuration change like phone rotation)
    override fun onCleared() {
        super.onCleared()
        timer.cancel() // To avoid memory leaks, you should always cancel a CountDownTimer if you no longer need it.
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
            resetList()
        }
        // Select and remove a word from the list
        _word.value = wordList.removeAt(0)

    }

    fun onSkip() {
        _eventBuzz.value = BuzzType.NO_BUZZ
        _score.value = (_score.value)?.minus(1)
        // we did not do score.value = score.value - 1 here as
        // the score is a nullable field, so we must have a null check
        // If score is null minus() is not called due to the '?' null check
        nextWord()
    }

     fun onCorrect() {
         _eventBuzz.value = BuzzType.CORRECT
        _score.value = (_score.value)?.plus(1)
        nextWord()
    }

    // When the game has finished, the UI will call this method to reset the
    // game finish state to say that "the game finish event has been handled to reset it"
    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

    // reset buzzing state after buzzing event is complete
    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }

}