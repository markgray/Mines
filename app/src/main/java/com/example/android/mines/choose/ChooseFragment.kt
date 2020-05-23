@file:Suppress("unused", "RedundantOverride", "UNUSED_PARAMETER")

package com.example.android.mines.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel

class ChooseFragment : Fragment() {

    companion object {
        const val COLUMN_COUNT = 8
        const val ROW_COUNT = 8
        const val MINE_COUNT = 10
        fun newInstance() = ChooseFragment()
    }

    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var uiView: View
    private lateinit var radioGroup: RadioGroup
    private var boardSizeChosen: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uiView = inflater.inflate(R.layout.choose_fragment, container, false)
        uiView.findViewById<Button>(R.id.button_play).setOnClickListener { view ->
            onPlayClicked(view)
        }
        radioGroup = uiView.findViewById(R.id.boardChoice)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectBoardSize(checkedId)
        }

        return uiView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    private fun onPlayClicked(view: View) {
        if (!boardSizeChosen) viewModel.init(COLUMN_COUNT, ROW_COUNT, MINE_COUNT)
        findNavController().navigate(R.id.action_chooseFragment_to_gameFragment)
    }

    private fun selectBoardSize(checkedId: Int) {
        boardSizeChosen = true
        when (checkedId) {
            R.id.board8by8 -> {
                viewModel.init(8, 8, 10)
            }
            R.id.board12by16 -> {
                viewModel.init(12, 16, 30)
            }
            R.id.board16by16 -> {
                viewModel.init(16, 16, 40)
            }
            R.id.boardCustom -> {
                viewModel.init(8, 8, 10)
            }
        }
    }

}
