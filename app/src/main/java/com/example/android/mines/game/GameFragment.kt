@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate", "unused", "RedundantOverride")

package com.example.android.mines.game

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.gridlayout.widget.GridLayout
import androidx.navigation.fragment.findNavController
import com.example.android.mines.R
import com.example.android.mines.SectorContent
import com.example.android.mines.SharedViewModel
import com.example.android.mines.databinding.GameFragmentBinding

class GameFragment : Fragment() {

    private lateinit var board: GridLayout
    private lateinit var safe: Button
    private lateinit var mine: Button
    private var boardWidth: Int = 0
    private var boardHeight: Int = 0
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: GameFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.game_fragment, container, false)

        board = binding.boardGrid
        safe = binding.buttonSafe
        mine = binding.buttonMine
        boardWidth = resources.displayMetrics.widthPixels
        boardHeight = viewModel.buttonTop
        createBoard()
        safe.setBackgroundColor(Color.RED)
        safe.setOnClickListener { view ->
            onSafeClicked(view)
        }
        mine.setOnClickListener { view ->
            onMineClicked(view)
        }
        viewModel.startTime = SystemClock.elapsedRealtime()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    @SuppressLint("SetTextI18n")
    private fun createBoard(): View {

        board.columnCount = viewModel.numColumns
        board.rowCount = viewModel.numRows

        val cellWidth = boardWidth / viewModel.numColumns
        val cellHeight = boardHeight / viewModel.numRows
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
                textView.setBackgroundResource(R.drawable.background_dark)
                textView.tag = contents
                textView.width = cellSize
                textView.height = cellSize
                textView.gravity = Gravity.CENTER
                textView.textSize = cellSize.toFloat()/resources.displayMetrics.density - 8.0f
                textView.setPadding(4, 4, 4, 4)
//              textView.text = "\u2705\u274c\u2734"
                textView.setOnClickListener { view ->
                    onSectorClicked(view)
                }
                board.addView(textView)
            }
        }

        return board
    }

    fun onSectorClicked(view: View) {
        val sectorTag : SectorContent = (view.tag as SectorContent)
        val textView : TextView = view as TextView
        if (viewModel.modeSafe) {
            if (sectorTag.hasMine) {
                textView.setBackgroundResource(R.drawable.bomb_icon)
                textView.text = "\u274c"
            } else if(!sectorTag.hasBeenChecked) {
                if (markSectorAsSafe(sectorTag, textView) == 0) {
                    markNeighborsAsSafe(sectorTag.neighbors)
                }
            }
        } else if (viewModel.modeMine) {
            if (sectorTag.hasMine) {
                if (!sectorTag.hasBeenChecked) {
                    sectorTag.hasBeenChecked = true
                    viewModel.numCheckedMine++
                }
                textView.setBackgroundResource(R.drawable.background_light)
                textView.text = "\u274c"
            } else {
                if (!sectorTag.hasBeenChecked) {
                    textView.setBackgroundResource(R.drawable.background_dark)
                }
            }
        }

        viewModel.numChecked = viewModel.numCheckedSafe + viewModel.numCheckedMine
        Log.i(
            "GameFragment",
            "${sectorTag.column} ${sectorTag.row} " +
            "${viewModel.numCheckedSafe} safe ${viewModel.numCheckedMine} mine"
        )
        if (viewModel.numChecked == viewModel.numSectors) {
            Toast.makeText(
                activity,
                "${viewModel.numChecked} checked out of ${viewModel.numSectors}",
                Toast.LENGTH_SHORT
            ).show()
            onFlippedAll()
        }
    }

    private fun markSectorAsSafe(
        sectorTag: SectorContent,
        textView: TextView
    ) : Int {
        sectorTag.hasBeenChecked = true
        viewModel.numCheckedSafe++
        textView.setBackgroundResource(R.drawable.background_light)
        val neighborList = sectorTag.neighbors
        var numberWithMines = 0
        for (index: Int in neighborList) {
            if (viewModel.haveMines[index]) {
                numberWithMines++
            }
        }
        textView.text = numberWithMines.toString()

        return numberWithMines
    }

    private fun markNeighborsAsSafe(listOfNeighbors: MutableList<Int>) {
        for (sectorIndex in listOfNeighbors) {
            val sector = viewModel.gameState[sectorIndex]
            if (!sector.hasBeenChecked) {
                val view: TextView = board.getChildAt(sectorIndex) as TextView
                markSectorAsSafe(sector, view)
            }
        }
    }

    fun onSafeClicked(view: View) {
        viewModel.modeSafe = true
        viewModel.modeMine = false
        view.setBackgroundColor(Color.RED)
        mine.setBackgroundColor(Color.GRAY)
    }

    fun onMineClicked(view: View) {
        viewModel.modeSafe = false
        viewModel.modeMine = true
        view.setBackgroundColor(Color.RED)
        safe.setBackgroundColor(Color.GRAY)
    }

    fun onFlippedAll() {
        viewModel.toMinesDatum()
        findNavController().navigate(R.id.action_gameFragment_to_scoreFragment)
    }

    companion object {
        @Suppress("unused")
        fun newInstance() = GameFragment()
    }

}
