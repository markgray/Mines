@file:Suppress("MemberVisibilityCanBePrivate")

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.gridlayout.widget.GridLayout
import androidx.navigation.fragment.findNavController
import com.example.android.mines.R
import com.example.android.mines.SectorContent
import com.example.android.mines.SharedViewModel
import com.example.android.mines.databinding.GameFragmentBinding

/**
 * This is the [Fragment] which handles the UI for the actual playing of a game.
 */
class GameFragment : Fragment() {

    /**
     * The [GridLayout] with binding id `boardGrid` (resourse ID R.id.board_grid) which holds the
     * [TextView]'s which represent our game board.
     */
    private lateinit var board: GridLayout

    /**
     * The [Button] with binding id `buttonSafe` (resourse ID R.id.button_safe) which the user uses
     * to transition to "Safe" mode (clicking a sector on the game board in this mode marks the
     * sector as a safe sector).
     */
    private lateinit var safe: Button

    /**
     * The [Button] with binding id `buttonMine` (resourse ID R.id.button_mine) which the user uses
     * to transition to "Mine" mode (clicking a sector on the game board in this mode marks the
     * sector as a mined sector).
     */
    private lateinit var mine: Button

    /**
     * The width of the game board in pixels. We use the absolute width of the available display size
     * in pixels. Our AndroidManifest uses android:screenOrientation="portrait" in order to avoid
     * wasting screen space which would be hard to avoid for our app in "landscape" orientation.
     */
    private var boardWidth: Int = 0

    /**
     * The height of the game board in pixels. We use the `buttonTop` property of our [SharedViewModel]
     * field [viewModel]. It is set to the top Y coordinate of its "Play" button by `ChooseFragment`
     * and we assume that that button is the same height as our "Safe" and "Mine" buttons since we
     * cannot guess their height until after the View is drawn but need the value before our View is
     * drawn.
     */
    private var boardHeight: Int = 0

    /**
     * The [SharedViewModel] shared view model used by all of our fragments.
     */
    private val viewModel: SharedViewModel by activityViewModels()

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. A default View can be returned by calling [Fragment]
     * in your constructor. It is recommended to only inflate the layout in this method and move
     * logic that operates on the returned View to [onViewCreated]. First we initialize our variable
     * `val binding` to the [GameFragmentBinding] that its `inflate` method returns when  it uses
     * our [LayoutInflater] parameter [inflater] to inflate our layout file [R.layout.game_fragment]
     * using our [ViewGroup] parameter [container] for its LayoutParams without attaching to it. We
     * set our [GridLayout] field [board] to the `binding` property `boardGrid` (resource ID
     * [R.id.board_grid] in our layout file), set our [Button] field [safe] to the `binding` property
     * `buttonSafe` (resource ID [R.id.button_safe] in our layout file), and set our [Button] field
     * [mine] to the `binding` property `buttonMine` (resource ID [R.id.button_mine] in our layout
     * file). We set our [Int] field [boardWidth] to the absolute width of the available display size
     * in pixels of the current display metrics that are in effect for our activity, and set our [Int]
     * field [boardHeight] to the `buttonTop` property of our [SharedViewModel] field [viewModel]
     * (which should have been set to the top Y coordinate of its "Play" button by `ChooseFragment`).
     * We then call our [createBoard] method to fill our [GridLayout] game board with TextView's that
     * correspond to the game board state which exists in the `gameState` list of [viewModel]. We
     * set the background color of our [Button] field [safe] to RED, set the `OnClickListener` of
     * [safe] to a lambda which calls our [onSafeClicked] method, and set the `OnClickListener` of
     * [mine] to a lambda which calls our [onMineClicked] method. We initialize the `startTime`
     * property of [viewModel] to the milliseconds since boot (it will be used to calculate the
     * elapsed time when the game is finished). Finally we return the outermost View in the layout
     * file associated with the [GameFragmentBinding] variable `binding` (its `root` [View]).
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate any views
     * @param container If non-null, this is the parent view that the fragment's UI will be attached
     * to. The fragment should not add the view itself, but this can be used to generate the
     * LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: GameFragmentBinding = GameFragmentBinding.inflate(
            inflater,
            container,
            false
        )

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

    /**
     * Configures and fills our [GridLayout] game board field [board] so that it represents the game
     * state which is present in our [SharedViewModel] field [viewModel]. First we set the number of
     * columns of [board] (its `columnCount` property) to the `numColumns` property of [viewModel]
     * and the number of rows of [board] to the `numRows` property of [viewModel]. We initialize our
     * [Int] variable `val cellWidth` to [boardWidth] divided by the `numColumns` property of
     * [viewModel], our [Int] variable `val cellHeight` to [boardHeight] divided by the `numRows`
     * property of [viewModel], and our [Int] variable `val cellSize` to the smaller of `cellWidth`
     * and `cellHeight`. We initialize our [Int] variable `var listIndex` to 0 then with an outer
     * loop over `row` from 0 until the `numRows` property of [viewModel], and an inner loop over
     * `column` from 0 until the `numColumns` property of [viewModel] we:
     *
     *  - Initialize our [SectorContent] variable `val contents` to the `listIndex` entry in the
     *  `gameState` field of [viewModel] (post incrementing `listIndex` while we are at it)
     *
     *  - Initialize our [TextView] variable `val textView` to a new instance.
     *
     *  - Set the background of `textView` to the drawable [R.drawable.background_dark] (a gray
     *  "rectangle")
     *
     *  - Set the `tag` of `textView` to `contents`, its `width` and `height` to `cellSize`, its
     *  gravity to [Gravity.CENTER], its `textSize` to `cellSize` divided by the logical density of
     *  the display minus 8.0f, and its padding to 4 pixel on each side.
     *
     *  - We then set the [View.OnClickListener] of `textView` to a lambda which calls our method
     *  [onSectorClicked] with the [View] that was clicked.
     *
     *  - Finally we add `textView` to [board] and loop around for the next sector.
     *
     * When done adding all the sectors in our game board we return [board] to the caller (which is
     * ignored at the moment).
     *
     * @return the [GridLayout] field [board] which we have filled with [TextView]'s
     */
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
                textView.setOnClickListener { view ->
                    onSectorClicked(view)
                }
                board.addView(textView)
            }
        }

        return board
    }

    /**
     * Called when one of the [TextView]'s in our [GridLayout] field [board] game board has been
     * clicked with the [View] parameter [view] that was clicked.
     *
     * @param view the [View] that was clicked.
     */
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
