package com.example.android.mines.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.android.mines.R
import com.example.android.mines.databinding.CustomSizeDialogBinding

class CustomSizeDialog : DialogFragment()  {
    /**
     * Called to do initial creation of a `DialogFragment`. This is called after `onAttach(Activity)`
     * and before `onCreateView(LayoutInflater, ViewGroup, Bundle)`.
     *
     * First we call our super's implementation of `onCreate`, then we fetch from our arguments
     * the [String] stored under the index "label" and save it in our field [mLabel], and we fetch
     * the [String] stored under the index "text" and save it it our field [mText]. Finally we set
     * the style of our `BibleDialog` `DialogFragment` to STYLE_NORMAL.
     *
     * @param savedInstanceState We do not override `onSaveInstanceState` so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. It is recommended to **only** inflate the layout in this
     * method and move logic that operates on the returned View to [onViewCreated].
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: CustomSizeDialogBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.custom_size_dialog,
            container,
            false
        )
        binding.dismissButton.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
}