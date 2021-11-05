package com.example.android.mines.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.choose.ChooseFragment
import com.example.android.mines.database.MinesDatum
import com.example.android.mines.databinding.EditFragmentBinding
import com.example.android.mines.game.GameFragment
import com.example.android.mines.isOrAre

/**
 * A simple [Fragment] subclass which allows the user to view and edit the game history database. It
 * is launched by [ChooseFragment] when the user clicks on the "Edit History" button in its UI.
 */
class EditFragment : Fragment() {

    /**
     * The [ViewBinding] generated from our layout file [R.layout.edit_fragment].
     */
    private lateinit var binding: EditFragmentBinding

    /**
     * The [SharedViewModel] shared view model used by all of our fragments.
     */
    private val viewModel: SharedViewModel by activityViewModels()

    /**
     * The [RecyclerView] in our layout which displays the list of old games which is read from our
     * ROOM database.
     */
    private lateinit var recyclerView: RecyclerView

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onViewCreated]. It is recommended to ONLY inflate the layout in this method
     * and move logic that operates on the returned [View] to [onViewCreated].
     *
     * First we initialize our [EditFragmentBinding] field [binding] by having the
     * [EditFragmentBinding.inflate] method inflate its associated layout file
     * [R.layout.edit_fragment] using our [LayoutInflater] parameter [inflater], with
     * our [ViewGroup] parameter [container] supplying the layout params without the
     * view being attached to it.
     *
     * We set the [View.OnClickListener] of the [EditFragmentBinding.launchDialog] button in [binding]
     * ("Launch Dialog") to a lambda which calls the [NavController.navigate] method of the
     * [NavController] used by our fragment to navigate to the [EditHistoryDialog].
     *
     * We set the [View.OnClickListener] of the [EditFragmentBinding.abortEditHistory] button in
     * [binding] ("Abort Edit History") to a lambda which calls the [NavController.navigate] method
     * of the [NavController] used by our fragment to navigate back to the [ChooseFragment].
     *
     * We initialize our [RecyclerView] field [recyclerView] to the `RecyclerView`
     * [EditFragmentBinding.historyRecyclerView] in [binding]. We then initialize our
     * [EditHistoryAdapter] variable `val adapter` to an instance whose `deleteListener`
     * lambda calls the [SharedViewModel.deleteMinesDatum] method of [viewModel] with the
     * [MinesDatum] to be deleted, and whose `playAgainListener` calls the
     * [SharedViewModel.loadGameFromMinesDatum] method of [viewModel] with the
     * [MinesDatum] whose game is to be played again and then calls the [NavController.navigate]
     * method of the [NavController] used by our fragment to navigate to the [GameFragment].
     * We then set the adapter of [recyclerView] to `adapter`.
     *
     * We add an observer to the [SharedViewModel.gameHistory] field of [viewModel] using the
     * [LifecycleOwner] that represents this Fragment's [View] lifecycle as the [LifecycleOwner]
     * which controls the observer, and a lambda as the [Observer] which will if the value of the
     * [SharedViewModel.gameHistory] field of [viewModel] is not `null` set the [EditHistoryAdapter.data]
     * field of `adapter` to the new value of [SharedViewModel.gameHistory] then branch on whether
     * the `gameHistory` list is empty"
     *  - it IS empty: the lambda sets the visibility of the [EditFragmentBinding.textViewFiller]
     *  [TextView] of [binding] to [View.VISIBLE] ("No game history exists") and the visibility of the
     *  [EditFragmentBinding.historyRecyclerView] `RecyclerView` of [binding] to [View.GONE].
     *  - it is NOT empty: the lambda sets the visibility of the [EditFragmentBinding.textViewFiller]
     *  [TextView] of [binding] to [View.GONE] and the visibility of the
     *  [EditFragmentBinding.historyRecyclerView] `RecyclerView` of [binding] to [View.VISIBLE].
     *
     * Finally we return the outermost [View] in the layout file associated with [binding] to be used
     * as our UI.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment.
     * @param container If non-`null`, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = EditFragmentBinding.inflate(inflater, container, false)
        binding.launchDialog.setOnClickListener {
            findNavController().navigate(R.id.action_editFragment_to_editHistoryDialog)
        }
        binding.abortEditHistory.setOnClickListener {
            findNavController().navigate(R.id.action_editFragment_to_chooseFragment)
        }

        recyclerView = binding.historyRecyclerView
        val adapter = EditHistoryAdapter(
            deleteListener = { minesDatum: MinesDatum ->
                viewModel.deleteMinesDatum(minesDatum)
            },
            playAgainListener = { minesDatum: MinesDatum ->
                viewModel.loadGameFromMinesDatum(minesDatum)
                findNavController().navigate(R.id.action_editFragment_to_gameFragment)
            }
        )
        recyclerView.adapter = adapter

        viewModel.gameHistory!!.observe(viewLifecycleOwner, { listOfMinesDatum: List<MinesDatum>? ->
            listOfMinesDatum?.let {
                adapter.data = it
                if (it.isEmpty()) {
                    binding.textViewFiller.visibility = View.VISIBLE
                    binding.historyRecyclerView.visibility = View.GONE
                    viewModel.sayIt("There are no games in our history")
                } else {
                    binding.textViewFiller.visibility = View.GONE
                    binding.historyRecyclerView.visibility = View.VISIBLE
                    val grammar = isOrAre(it.size, "game", "games")
                    viewModel.sayIt("There $grammar in our history")
                }
            }
        })
        return binding.root
    }

}