@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate")

package com.example.android.mines.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.mines.R
import com.example.android.mines.SectorContent

class GameFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var board: GridLayout
    private lateinit var good: Button
    private lateinit var mine: Button
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private lateinit var viewModel: GameViewModel

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
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)
        // TODO: Use the ViewModel
    }

    @SuppressLint("SetTextI18n")
    private fun createBoard(): View {

        board.columnCount = COLUMN_COUNT
        board.rowCount = ROW_COUNT

        val cellWidth = screenWidth / COLUMN_COUNT
        val cellHeight = screenHeight / ROW_COUNT
        val cellSize = if (cellWidth < cellHeight) {
            cellWidth
        } else {
            cellHeight
        }

        for (row in 1..ROW_COUNT) {
            for (column in 1..COLUMN_COUNT) {
                val contents = SectorContent(row, column)
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
