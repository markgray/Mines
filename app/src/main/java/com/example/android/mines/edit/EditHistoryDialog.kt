package com.example.android.mines.edit

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.database.MinesDatabaseDao
import com.example.android.mines.database.MinesDatum
import com.example.android.mines.databinding.EditHistoryDialogBinding

/**
 * This is the [DialogFragment] that is launched by clicking the [R.id.launch_dialog] "Launch Dialog"
 * button in the UI of [EditFragment]. At present it holds two buttons: "Generate Fake games" which
 * adds a "fake" random game to the game history database every time it is clicked, and a "Done"
 * button which returns to [EditFragment]. Eventually it will also allow the user to delete multiple
 * games from the game history database using the [SharedViewModel.deleteMinesDatum] method or by
 * making a list of [MinesDatum.gameId] ID's of [MinesDatum] entries that it wants to delete and
 * having a yet to be written [SharedViewModel] method call the [MinesDatabaseDao.deleteMultipleIDs]
 * method with that list.
 */
class EditHistoryDialog : DialogFragment() {
    /**
     * The [SharedViewModel] instance we share with all our other fragments.
     */
    private val viewModel: SharedViewModel by activityViewModels()

    /**
     * The [ViewBinding] generated from our layout file [R.layout.edit_history_dialog].
     */
    private lateinit var binding: EditHistoryDialogBinding

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between [onCreate] and
     * [onViewCreated]. First we call our super's implementation of `onCreateView`, then we
     * initialize our [EditHistoryDialogBinding] field [binding] by having the
     * [EditHistoryDialogBinding.inflate] method inflate its associated layout file
     * [R.layout.edit_history_dialog] using our [LayoutInflater] parameter [inflater], with
     * our [ViewGroup] parameter [container] supplying the layout params without the view being
     * attached to it. Next we set the [View.OnClickListener] of the [EditHistoryDialogBinding.done]
     * button in [binding] to a lambda which finds a [NavController] for our fragment and calls its
     * [NavController.navigate] method to navigate back to the [EditFragment]. Then we set the
     * [View.OnClickListener] of the [EditHistoryDialogBinding.generateFakeGames] button in
     * [binding] to a lambda which calls the [SharedViewModel.randomGame] method of our [viewModel]
     * field to have it generate a random game, whose [SharedViewModel.startTime] start time property
     * we set to 1,000,000 milliseconds ago, and then call the [SharedViewModel.toMinesDatum] method
     * to have it encode the game into a [MinesDatum] and write it to the game history database.
     *
     * Finally we return the outermost [View] in the layout file associated with [binding] to have it
     * used as the [View] for the fragment's UI.
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
        binding.done.setOnClickListener {
            findNavController().navigate(R.id.action_editHistoryDialog_to_editFragment)
        }
        binding.generateFakeGames.setOnClickListener {
            viewModel.randomGame(8, 8, 10)
            viewModel.startTime = SystemClock.elapsedRealtime() - 1_000_000L
            viewModel.toMinesDatum()
        }
        return binding.root
    }
}