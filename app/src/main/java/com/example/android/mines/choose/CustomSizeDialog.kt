package com.example.android.mines.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.databinding.CustomSizeDialogBinding

class CustomSizeDialog : DialogFragment()  {
    /**
     * The [SharedViewModel] instance we share with all our other fragments.
     */
    private val viewModel: SharedViewModel by activityViewModels()

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
            val columns: Int = sane(
                binding.columnsNumber.text.toString(),
                4,
                32,
                8
            )
            val rows: Int = sane(
                binding.rowsNumber.text.toString(),
                4,
                32,
                8
            )
            val mines: Int = sane(
                binding.minesNumber.text.toString(),
                1,
                rows*columns/2,
                rows*columns/10
            )
            viewModel.randomGame(columns, rows, mines)
            dismiss()
        }
        return binding.root
    }

    private fun sane(old: String, low: Int, hi: Int, default: Int): Int {
        val new: Int = if (old.isDigitsOnly() && old != "") {
            old.toInt()
        } else {
            low
        }
        return if (new in low..hi) {
            new
        } else {
            default
        }
    }
}