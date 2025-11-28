package com.example.android.mines

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.NavHostFragment

/**
 * This is the main activity of our Mine Sweeper game. It just sets our content view to a layout
 * file containing a `FragmentContainerView` which hosts the fragments which implement the game.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to
     * edge display, then we call our super's implementation of `onCreate`, and set our content
     * view to our layout file `R.layout.activity_main` which contains a [NavHostFragment] which
     * handles all the UI navigation in our app using the `navigation` XML file main_navigation.xml.
     * We initialize our [ContentFrameLayout] variable rootView to the view with ID
     * `android.R.id.content` then call [ViewCompat.setOnApplyWindowInsetsListener] to take over
     * the policy for applying window insets to `rootView`, with the listener argument
     * a lambda that accepts the [View] passed the lambda in variable `v` and
     * the [WindowInsetsCompat] passed the lambda in variable `windowInsets`. It
     * initializes its [Insets] variable `insets` to the [WindowInsetsCompat.getInsets]
     * of `windowInsets` with [WindowInsetsCompat.Type.systemBars] as the
     * argument, then it updates the layout parameters of v to be a
     * [ViewGroup.MarginLayoutParams] with the left margin set to `insets.left`,
     * the right margin set to `insets.right`, the top margin set to `insets.top`,
     * and the bottom margin set to `insets.bottom`. Finally it returns
     * [WindowInsetsCompat.CONSUMED] to the caller (so that the window insets
     * will not keep passing down to descendant views).
     *
     * @param savedInstanceState We do not call [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = window.decorView.findViewById<ContentFrameLayout>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v: View, windowInsets: WindowInsetsCompat ->
            val insets: Insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
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
