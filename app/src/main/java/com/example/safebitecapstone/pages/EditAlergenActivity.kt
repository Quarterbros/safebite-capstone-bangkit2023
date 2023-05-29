package com.example.safebitecapstone.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.safebitecapstone.databinding.ActivityEditAlergenBinding
import com.example.safebitecapstone.databinding.ActivityMainBinding

class EditAlergenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAlergenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditAlergenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "My Alergens"
    }
}