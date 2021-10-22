package com.example.android.mines.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.android.mines.SharedViewModel
import com.example.android.mines.databinding.EditHistoryDialogBinding

class EditHistoryDialog : DialogFragment() {
    /**
     * The [SharedViewModel] instance we share with all our other fragments.
     */
    private val viewModel: SharedViewModel by activityViewModels()

    private lateinit var binding: EditHistoryDialogBinding

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between [onCreate] and
     * [onViewCreated].
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-`null`, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or `null`.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = EditHistoryDialogBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
}