package com.erenalparslan.a7minuteworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.erenalparslan.a7minuteworkoutapp.databinding.ActivityFinishBinding

class FinishActivity : AppCompatActivity() {


    private var binding: ActivityFinishBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarFinishActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarFinishActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnFinish?.setOnClickListener {
            finish()
        }
    }
}