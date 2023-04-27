package com.erenalparslan.a7minuteworkoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.erenalparslan.a7minuteworkoutapp.databinding.ActivityExerciseActivitiyBinding
import com.erenalparslan.a7minuteworkoutapp.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList


class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExerciseActivitiyBinding? = null

    private var restTimer: CountDownTimer? =
        null // Variable for Rest Timer and later on we will initialize it.
    private var restProgress =
        0 // Variable for timer progress. As initial value the rest progress is set to 0. As we are about to start.

    private var exerciseTimer: CountDownTimer? =
        null // Variable for Exercise Timer and later on we will initialize it.
    private var exerciseProgress =
        0 // Variable for the exercise timer progress. As initial value the exercise progress is set to 0. As we are about to start.

    private var exerciseList: ArrayList<ExerciseModel>? = null // We will initialize the list later.
    private var currentExercisePosition = -1 // Current Position of Exercise.
    private var tts: TextToSpeech? = null // Variable for Text to Speech
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseActivitiyBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }
        tts = TextToSpeech(this, this)
        exerciseList = Constants.defaultExerciseList()

        setupRestView()

        setupExerciseStatusRecyclerView()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun setupRestView() {

        try {
            val soundURI =
                Uri.parse("android.resource://com.erenalparslan.a7minuteworkoutapp/" + R.raw.app_src_main_res_raw_press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false // Sets the player to be looping or non-looping.
            player?.start() // Starts Playback.
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.upcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        binding?.tvUpcomingExerciseName?.text =
            exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()
    }


    private fun setRestProgressBar() {

        binding?.progressBar?.progress =
            restProgress // Sets the current progress to the specified value.

        // Here we have started a timer of 10 seconds so the 10000 is milliseconds is 10 seconds and the countdown interval is 1 second so it 1000.
        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++ // It is increased by 1
                binding?.progressBar?.progress =
                    10 - restProgress // Indicates progress bar progress
                binding?.tvTimer?.text =
                    (10 - restProgress).toString()  // Current progress is set to text view in terms of seconds.
            }

            override fun onFinish() {
                // When the 10 seconds will complete this will be executed.
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true) // Current Item is selected
                exerciseAdapter!!.notifyDataSetChanged() // Notified the current item to adapter class to reflect it into UI.
                setupExerciseView()
            }
        }.start()
    }

    private fun setupExerciseView() {

        // Here according to the view make it visible as this is Exercise View so exercise view is visible and rest view is not.
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.upcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE


        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()

        setExerciseProgressBar()
    }


    private fun setExerciseProgressBar() {

        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {


                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].setIsSelected(false) // exercise is completed so selection is set to false
                    exerciseList!![currentExercisePosition].setIsCompleted(true) // updating in the list that this exercise is completed
                    exerciseAdapter!!.notifyDataSetChanged() // Notifying the adapter class.
                    setupRestView()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    private fun setupExerciseStatusRecyclerView() {

        // Defining a layout manager for the recycle view
        // Here we have used a LinearLayout Manager with horizontal scroll.
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // As the adapter expects the exercises list and context so initialize it passing it.
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)

        // Adapter class is attached to recycler view
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }


    override fun onInit(status: Int) {


        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }

        } else {
            Log.e("TTS", "Initialization Failed!")
        }

    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    public override fun onDestroy() {
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        // END
        super.onDestroy()
        binding = null
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        /*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*/
        customDialog.setContentView(dialogBinding.root)
        //not dismissed when surrounding parts of the screen is clicked
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.tvYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss() // Dialog will be dismissed
        }
        dialogBinding.tvNo.setOnClickListener {
            customDialog.dismiss()
        }
        //Start the dialog and display it on screen.
        customDialog.show()
    }
}