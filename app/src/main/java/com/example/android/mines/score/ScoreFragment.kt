package com.example.android.mines.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.database.MinesDatum
import com.example.android.mines.databinding.ScoreFragmentBinding
import com.example.android.mines.formatMinesDatum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This is the [Fragment] which handles the UI for the display of the statistics of the last game
 * played, as well as a [RecyclerView] displaying all of the games currently stored in our ROOM
 * database sorted with the fastest time first.
 */
class ScoreFragment : Fragment() {

    companion object {
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
     * The "Again" [Button] that the user clicks to navigate back to the `ChooseFragment` in order
     * to choose game board size and play another random game.
     */
    private lateinit var buttonAgain: Button
    private lateinit var textView: TextView
    private lateinit var latestDatum: MinesDatum
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ScoreFragmentBinding = ScoreFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        buttonAgain = binding.buttonAgain
        buttonAgain.setOnClickListener {
            findNavController().navigate(R.id.action_scoreFragment_to_chooseFragment)
        }
        textView = binding.textViewScore
        recyclerView = binding.gameHistoryList
        val adapter = MineDatumAdapter(MinesDatumListener{ minesDatum ->
            viewModel.loadGameFromMinesDatum(minesDatum)
            findNavController().navigate(R.id.action_scoreFragment_to_gameFragment)
        })
        recyclerView.adapter = adapter
        viewModel.gameHistory!!.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        CoroutineScope(Dispatchers.Main).launch {
            latestDatum = viewModel.retrieveLatestDatum()
            textView.append(formatMinesDatum(latestDatum))
            // TODO: save gameId of latestDatum in ViewModel for use by ViewHolder bind method
        }
        return binding.root
    }
}

class MinesDatumListener(val listen: (minesDatum: MinesDatum) -> Unit){
    fun reload(minesDatum: MinesDatum) {
        listen(minesDatum)
    }
}
