package com.erenalparslan.a7minuteworkoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.erenalparslan.a7minuteworkoutapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.flStart.setOnClickListener{
            //Toast.makeText(this,"Hello",Toast.LENGTH_SHORT).show()
            val intent =Intent(this,ExerciseActivity::class.java)
            startActivity(intent)
        }
    }
}