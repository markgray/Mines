package com.example.android.mines

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

@Suppress("MemberVisibilityCanBePrivate")
@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    lateinit var viewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model: SharedViewModel by viewModels<SharedViewModel>()
        viewModel = model
        setContentView(R.layout.activity_main)
    }

}
