package com.example.android.mines

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

/**
 * This is the main activity of our Mine Sweeper game. It just sets our content view to a layout
 * file containing a `FragmentContainerView` which hosts the fragments which implement the game.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. We just call our super's implementation of `onCreate`
     * then set our content view to our layout file [R.layout.activity_main] which contains a
     * `NavHostFragment` which handles all the UI navigation in our app using the `navigation`
     * XML file main_navigation.xml
     *
     * @param savedInstanceState We do not call [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = window.decorView.findViewById<ContentFrameLayout>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
                topMargin = insets.top
                bottomMargin = insets.bottom
            }
            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

    }

}
