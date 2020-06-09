@file:Suppress("unused", "RedundantOverride", "UNUSED_PARAMETER")

package com.example.android.mines.choose

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.database.MinesDataBase
import com.example.android.mines.databinding.ChooseFragmentBinding

/**
 * This is the "app:startDestination" for our app and allows the user to choose the size of the
 * game board which it then configures the [SharedViewModel] to represent before navigating to the
 * `GameFragment` to begin playing the game.
 * TODO: add Button to navigate to the ScoreFragment to replay an old game instead.
 */
class ChooseFragment : Fragment() {

    companion object {
        /**
         * Default number of columns if the user does not choose a game board size
         */
        const val COLUMN_COUNT = 8

        /**
         * Default number of row if the user does not choose a game board size
         */
        const val ROW_COUNT = 8

        /**
         * Default number of Mined sectors if the user does not choose a game board size
         */
        const val MINE_COUNT = 10

        /**
         * Unused [ChooseFragment] factory method.
         */
        fun newInstance() = ChooseFragment()
    }

    /**
     * The [SharedViewModel] instance we share with all our other fragments.
     */
    private val viewModel: SharedViewModel by activityViewModels()

    /**
     * Our [Application] which we need to use as the context for our database when we first open or
     * create it.
     */
    private lateinit var application: Application

    /**
     * The [MinesDataBase] we store our game history in.
     */
    private lateinit var minesDataBase: MinesDataBase
    private lateinit var radioGroup: RadioGroup
    private var boardSizeChosen: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ChooseFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.choose_fragment, container, false)

        binding.buttonPlay.setOnClickListener { view ->
            onPlayClicked(view)
        }
        radioGroup = binding.boardChoice
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectBoardSize(checkedId)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        application = requireNotNull(this.activity).application
        minesDataBase  = MinesDataBase.getInstance(application)
        if (viewModel.minesDatabaseDao == null) {
            viewModel.minesDatabaseDao = minesDataBase.minesDatabaseDao
        }
        if (viewModel.gameHistory == null) {
            viewModel.gameHistory = viewModel.minesDatabaseDao!!.getAllGames()
        }
    }

    private fun onPlayClicked(view: View) {
        viewModel.buttonTop = view.top
        if (!boardSizeChosen) viewModel.randomGame(COLUMN_COUNT, ROW_COUNT, MINE_COUNT)
        findNavController().navigate(R.id.action_chooseFragment_to_gameFragment)
    }

    private fun selectBoardSize(checkedId: Int) {
        boardSizeChosen = true
        when (checkedId) {
            R.id.board8by8 -> {
                viewModel.randomGame(8, 8, 10)
            }
            R.id.board12by16 -> {
                viewModel.randomGame(12, 16, 30)
            }
            R.id.board16by16 -> {
                viewModel.randomGame(16, 16, 40)
            }
            R.id.board16by30 -> {
                viewModel.randomGame(16, 30, 99)
            }
            // TODO: add a custom choice which launches a dialog to allow user to specify size
        }
    }

}
