package com.example.android.mines.score

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.choose.ChooseFragment
import com.example.android.mines.database.MinesDatum
import com.example.android.mines.databinding.ScoreFragmentBinding
import com.example.android.mines.formatMinesDatum
import com.example.android.mines.game.GameFragment
import com.example.android.mines.isOrAre

/**
 * This is the [Fragment] which handles the UI for the display of the statistics of the last game
 * played, as well as a [RecyclerView] displaying all of the games currently stored in our ROOM
 * database sorted with the fastest time first.
 */
class ScoreFragment : Fragment() {

    companion object {
        /**
         * TAG used for logging
         */
        const val TAG = "ScoreFragment"
        /**
         * Boilerplate factory method added by Android Studio when we created this [Fragment]
         */
        @Suppress("unused")
        fun newInstance() = ScoreFragment()
    }

    /**
     * The [SharedViewModel] shared view model used by all of our fragments.
     */
    private val viewModel: SharedViewModel by activityViewModels()

    /**
     * The "New Game" [Button] that the user clicks to navigate back to the [ChooseFragment] in
     * order to choose game board size and play another random game.
     */
    private lateinit var buttonNewGame: Button

    /**
     * The [TextView] we use to display the statistics about the latest game played.
     */
    private lateinit var textView: TextView

    /**
     * The [MinesDatum] created from the latest game played, whose statistics we display in our
     * [TextView] field [textView]. It is set in our [onCreateView] override to the contents of
     * the [SharedViewModel.latestDatum] field of [viewModel]
     */
    private lateinit var latestDatum: MinesDatum

    /**
     * The [RecyclerView] in our layout which displays the list of old games which is read from our
     * ROOM database.
     */
    private lateinit var recyclerView: RecyclerView

    /**
     * Called to have the fragment instantiate its user interface view. First we initialize our
     * [ScoreFragmentBinding] variable `val binding` by using the `inflate` method of
     * [ScoreFragmentBinding] to have it inflate the layout file layout/score_fragment.xml which
     * it was generated from using our [LayoutInflater] parameter [inflater], and our [ViewGroup]
     * parameter [container] for the LayoutParams of the views without attaching to it. We initialize
     * our [Button] field [buttonNewGame] to the `buttonNewGame` property of `binding` (resource ID
     * [R.id.button_new_game]) then set its `OnClickListener` to a lambda which navigates to the
     * [ChooseFragment]. We initialize our [TextView] field [textView] to the `textViewScore`
     * property of `binding` (resource ID [R.id.textViewScore]), and we then set [latestDatum] to the
     * value of the [SharedViewModel.latestDatum] field of [viewModel] and append the [String] result
     * of calling our [formatMinesDatum] method for [latestDatum] to the text of [textView].
     *
     * We initialize our [RecyclerView] field [recyclerView] to the `gameHistoryList` property of
     * `binding` (resource ID [R.id.game_history_list] in our layout file). We initialize our
     * [MineDatumAdapter] variable `val adapter` to an instance constructed to use as its
     * [MinesDatumListener] a lambda which calls the `loadGameFromMinesDatum` method of our
     * [SharedViewModel] field [viewModel] to load the [MinesDatum] of the old game that was long
     * clicked and the to navigate to the [GameFragment] to replay the game. We set the adapter of
     * [recyclerView] to `adapter`. We then add an observer to the `gameHistory` field of
     * [viewModel] whose lambda sets the `data` dataset field of `adapter` to the observed [List]
     * of [MinesDatum] whenever ROOM refreshes the `LiveData` of the `gameHistory` list from the
     * database. Finally we return the outermost [View] in the layout file associated with the
     * [ScoreFragmentBinding] variable `binding` (its `root` [View]) to use as our UI.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment.
     * @param container If non-`null`, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or null.
     */
    @Suppress("RedundantNullableReturnType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ScoreFragmentBinding = ScoreFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        buttonNewGame = binding.buttonNewGame
        buttonNewGame.setOnClickListener {
            findNavController().navigate(R.id.action_scoreFragment_to_chooseFragment)
        }

        textView = binding.textViewScore
        latestDatum = viewModel.latestDatum
        textView.append(formatMinesDatum(latestDatum))

        recyclerView = binding.gameHistoryList
        val adapter = MineDatumAdapter(MinesDatumListener { minesDatum ->
            viewModel.loadGameFromMinesDatum(minesDatum)
            findNavController().navigate(R.id.action_scoreFragment_to_gameFragment)
        })
        recyclerView.adapter = adapter
        viewModel.gameHistory!!.observe(viewLifecycleOwner, {
            it?.let {
                adapter.data = it
                viewModel.saveNewestId(adapter.newestGameId())
                val grammar = isOrAre(adapter.itemCount, "game", "games")
                viewModel.sayIt("There $grammar in our history")
            }
        })

        viewModel.newestID.observe(viewLifecycleOwner, { latestGameID: Long ->
            Log.i(TAG,"The latest gameID is $latestGameID")
        })
        return binding.root
    }
}

/**
 * This Listener class redirects a call to its [reload] method to the lambda which is used when it
 * is constructed. (A bit of indirection which seemed called for at the time, but may not be all
 * that necessary after all, although it does still make the code look cleaner.)
 *
 * @param listen the lambda which should be called when our [reload] method is called with a
 * [MinesDatum] object.
 */
class MinesDatumListener(val listen: (minesDatum: MinesDatum) -> Unit) {
    fun reload(minesDatum: MinesDatum) {
        listen(minesDatum)
    }
}
