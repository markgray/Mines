package com.example.android.mines.score

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mines.SharedViewModel
import com.example.android.mines.database.MinesDatum
import com.example.android.mines.databinding.MineDatumViewBinding
import com.example.android.mines.formatGameBoard
import com.example.android.mines.formatMinesDatum

/**
 * This is the [RecyclerView.Adapter] that we use to display the [MinesDatum] from our game history
 * database in our [ScoreFragment] recycler view.
 *
 * @param minesDatumListener The [MinesDatumListener] whose `reload` method the `OnLongClickListener`
 * of our [ViewHolder] views should call to reload the [MinesDatum] it displays in order for the user
 * to replay it.
 */
class MineDatumAdapter(
    private val minesDatumListener: MinesDatumListener
) : RecyclerView.Adapter<MineDatumAdapter.ViewHolder>() {

    /**
     * Our dataset. It is set in the lambda of an `Observer` of the `gameHistory` field of our
     * [SharedViewModel] which is a `LiveData` which is read from our game history ROOM database
     * and is updated everytime there is a change to the database. The setter sets our field
     * [newest] to 0 so that the dataset is once again searched for the newest [MinesDatum.gameId]
     * in order to highlight it in green.
     */
    var data: List<MinesDatum> = listOf()
        @SuppressLint("NotifyDataSetChanged") // The whole List changes so notifyDataSetChanged is needed
        set(value) {
            field = value
            newest = 0L
            notifyDataSetChanged()
        }

    /**
     * The [MinesDatum.gameId] property of the newest [MinesDatum] added to our database (valid IDs
     * start at 1).
     */
    private var newest: Long = 0L

    /**
     * Searches the [MinesDatum] in our dataset [data] for the highest [MinesDatum.gameId] property
     * and caches that value in our field [newest].
     *
     * @return the highest [MinesDatum.gameId] property of the [MinesDatum] in our dataset (ie. the
     * newest entry).
     */
    fun newestGameId(): Long {
        if (newest == 0L) {
            for ((gameId, _, _, _, _, _, _) in data) {
                if (gameId > newest) {
                    newest = gameId
                }
            }
        }
        return newest
    }

    /**
     * Called when [RecyclerView] needs a new [ViewHolder] of the given type to represent an item.
     * This new [ViewHolder] should be constructed with a new [View] that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file. The new [ViewHolder] will be used to display items of the adapter using
     * [onBindViewHolder]. (Our [ViewHolder] uses the view binding [MineDatumViewBinding] to avoid
     * [View.findViewById] calls). We just return the [ViewHolder] returned by the `from` static
     * method of our [ViewHolder] nested class when it is passed our [ViewGroup] parameter [parent].
     *
     * @param parent The [ViewGroup] into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new [ViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    /**
     * Returns the total number of items in the data set held by the adapter. We just return the
     * size of our dataset list [data].
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = data.size

    /**
     * Called by [RecyclerView] to display the data at the specified position. This method should
     * update the contents of the `ViewHolder.itemView` to reflect the item at the given position.
     * We set our [MinesDatum] variable `val item` to the [MinesDatum] at position [position] in
     * our dataset list [data], then call the `bind` method of [holder] to have it update the
     * contents of its two `TextView`'s to display the [MinesDatum] in `item`. Then if the `gameId`
     * field of `item` is equal to the game ID of the newest [MinesDatum] added to our game history
     * ROOM database we call the `highLight` method of [holder] to have it set the color of the text
     * to GREEN, otherwise we call the `highLight` method of [holder] to have it set the color of
     * the text to BLACK. Finally we call the `setMinesDatumListener` method of [holder] to have it
     * set its `listener` field to our [MinesDatumListener] field [minesDatumListener].
     *
     * @param holder The [ViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        if (item.gameId == newestGameId()) {
            holder.highLight(Color.GREEN)
        } else {
            holder.highLight(Color.BLACK)
        }
        holder.setMinesDatumListener(minesDatumListener)
    }

    /**
     * The custom [RecyclerView.ViewHolder] this adapter uses to hold our views.
     *
     * @param binding The [MineDatumViewBinding] for our layout file layout/mine_datum_view.xml
     */
    class ViewHolder private constructor(
        private val binding: MineDatumViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * The [MinesDatumListener] whose `reload` method the `OnLongClickListener` of our View
         * should call to reload the [MinesDatum] we display into the [SharedViewModel] in order
         * to allow the user to replay that game.
         */
        private lateinit var listener: MinesDatumListener

        /**
         * Updates the contents of our two `TextView`'s to reflect the [MinesDatum] parameter [item]
         * at the given position. We set the background color of the `gameBoard` `TextView` to GRAY
         * (the `TextView` with resource id `R.id.game_board`) and set its text to the [String]
         * returned by our [formatGameBoard] method for [item] (a checkerboard where a safe sector
         * is displayed as a green checkmark, and a mined sector as a red X). Then we set the text
         * of the `gameStats` `TextView` to the [String] returned by our [formatMinesDatum] method
         * for [item] (just a list of interesting properties of [item]). In addition we set the
         * `OnLongClickListener` of the `constraintViewGroup` view group holding both `TextView`s to
         * a lambda which calls the `reload` method of our [MinesDatumListener] field [listener] to
         * have it reload the [MinesDatum] parameter [item] into the `SharedViewModel` in order to
         * allow the user to replay that game, and then returns `true` to consume the event.
         *
         * @param item the [MinesDatum] our views are supposed to display.
         */
        fun bind(item: MinesDatum) {
            binding.gameBoard.setBackgroundColor(Color.GRAY)
            binding.gameBoard.text = formatGameBoard(item)
            binding.gameStats.text = formatMinesDatum(item)
            binding.constraintViewGroup.setOnLongClickListener {
                listener.reload(item)
                true
            }
        }

        /**
         * Set the TextColor of the [MineDatumViewBinding.gameStats] (resource ID `R.id.game_stats`)
         * [TextView] in [binding] to the color of our parameter [color].
         *
         * @param color a color value in the form 0xAARRGGBB.
         */
        fun highLight(color: Int) {
            binding.gameStats.setTextColor(color)
        }

        /**
         * Sets our [MinesDatumListener] field [listener] to our parameter [minesDatumListener].
         *
         * @param minesDatumListener the [MinesDatumListener] whose `reload` method our view's
         * `OnLongClickListener` should call to reload the [MinesDatum] it displays into the
         * [SharedViewModel] in order to allow the user to replay that game.
         */
        fun setMinesDatumListener(minesDatumListener: MinesDatumListener) {
            listener = minesDatumListener
        }

        companion object {
            /**
             * Static factory method used to construct a [ViewHolder] holding a [MineDatumViewBinding]
             * inflated from our layout file layout/mine_datum_view.xml into a [MineDatumViewBinding].
             * We initialize our [LayoutInflater] variable `val layoutInflater` with an instance for
             * the context of our [ViewGroup] parameter [parent]. Then we initialize our
             * [MineDatumViewBinding] variable `val binding` by using the `inflate` method of
             * [MineDatumViewBinding] to inflate its associated layout file using `layoutInflater`,
             * and our [ViewGroup] parameter [parent] for the LayoutParams without attaching to it.
             * Finally we return a [ViewHolder] instance constructed from `binding`.
             *
             * @param parent the [ViewGroup] our view will eventually be attached to.
             * @return a new instance of [ViewHolder]
             */
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    MineDatumViewBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}
