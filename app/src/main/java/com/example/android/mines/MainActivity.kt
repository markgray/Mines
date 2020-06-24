package com.example.android.mines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. We just call our super's implementation of `onCreate`
     * then set our content view to our layout file R.layout.activity_main which contains a
     * `NavHostFragment` which handles all the UI navigation in our app using the `navigation`
     * XML file main_navigation.xml
     *
     * @param savedInstanceState We do not call [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}
