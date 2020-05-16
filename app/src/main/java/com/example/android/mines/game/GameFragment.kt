@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate", "unused", "RedundantOverride")

package com.example.android.mines.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.gridlayout.widget.GridLayout
import androidx.navigation.fragment.findNavController
import com.example.android.mines.R
import com.example.android.mines.SectorContent
import com.example.android.mines.SharedViewModel

class GameFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var board: GridLayout
    private lateinit var good: Button
    private lateinit var mine: Button
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.game_fragment, container, false)
        board = rootView.findViewById(R.id.board_grid)
        good = rootView.findViewById(R.id.button_good)
        mine = rootView.findViewById(R.id.button_mine)
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels - 200
        createBoard()
        good.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_scoreFragment)
        }
        mine.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_scoreFragment)
        }
        viewModel.startTime = SystemClock.elapsedRealtime()
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    @SuppressLint("SetTextI18n")
    private fun createBoard(): View {

        board.columnCount = viewModel.numColumns
        board.rowCount = viewModel.numRows

        val cellWidth = screenWidth / viewModel.numColumns
        val cellHeight = screenHeight / viewModel.numRows
        val cellSize = if (cellWidth < cellHeight) {
            cellWidth
        } else {
            cellHeight
        }

        var listIndex = 0
        for (row in 0 until viewModel.numRows) {
            for (column in 0 until viewModel.numColumns) {
                val contents = viewModel.gameState[listIndex++]
                val textView = TextView(activity)
                textView.setBackgroundResource(R.drawable.background_unselected)
                textView.tag = contents
                textView.width = cellSize
                textView.height = cellSize
                textView.setPadding(4, 4, 4, 4)
                textView.text = "($row,$column)"
                textView.setOnClickListener { view ->
                    onSectorClicked(view)
                }
                board.addView(textView)
            }
        }

        return board
    }

    fun onSectorClicked(view: View) {
        view.setBackgroundResource(R.drawable.background_selected)
        val sectorTag : SectorContent = (view.tag as SectorContent)
        Toast.makeText(
            activity,
            "Row ${sectorTag.row}, Column ${sectorTag.column} clicked",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        const val COLUMN_COUNT = 8
        const val ROW_COUNT = 8
        @Suppress("unused")
        fun newInstance() = GameFragment()
    }

}
