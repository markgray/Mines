package com.example.android.mines.edit

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mines.SharedViewModel
import com.example.android.mines.database.MinesDatum
import com.example.android.mines.databinding.MineEditDatumViewBinding
import com.example.android.mines.edit.EditHistoryAdapter.ViewHolder
import com.example.android.mines.formatGameBoard
import com.example.android.mines.formatMinesDatum

/**
 * This is the [RecyclerView.Adapter] that we use to display the [MinesDatum] from our game history
 * database in our [EditFragment] recycler view.
 */
class EditHistoryAdapter(
    /**
     * This is the listener which should be called with the [MinesDatum] it is displaying when the
     * view of a [ViewHolder] is long clicked. In our case it is a lambda which calls the method
     * [SharedViewModel.deleteMinesDatum] with the [MinesDatum] it is passed to have the viewmodel
     * delete the entry from the database.
     */
    private val deleteListener: (minesDatum: MinesDatum) -> Unit,
    private val playAgainListener: (minesDatum: MinesDatum) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    /**
     * Our dataset. It is set in the lambda of an `Observer` of the `gameHistory` field of our
     * `SharedViewModel` which is a `LiveData` which is read from our game history ROOM database.
     */
    var data = listOf<MinesDatum>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new `ViewHolder` of the given type to represent an item.
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file. The new ViewHolder will be used to display items of the adapter using
     * [onBindViewHolder]. Since it will be re-used to display different items in the data set,
     * it is a good idea to cache references to sub views of the View to avoid unnecessary
     * `View.findViewById` calls. We just return the [ViewHolder] returned by the `from` static
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
    override fun getItemCount() = data.size

    /**
     * Called by [RecyclerView] to display the data at the specified position. This method should
     * update the contents of the `ViewHolder.itemView` to reflect the item at the given position.
     * We set our [MinesDatum] variable `val item` to the [MinesDatum] at position [position] in
     * our dataset list [data], then call the `bind` method of [holder] to have it update the
     * contents of its two `TextView`'s to display the [MinesDatum] in `item`. Finally we set the
     * [ViewHolder.deleteListener] property to our [deleteListener] field.
     *
     * @param holder The [ViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.deleteListener = this.deleteListener
        holder.playAgainListener = this.playAgainListener
    }


    /**
     * The custom [RecyclerView.ViewHolder] this adapter uses to hold our views.
     */
    class ViewHolder private constructor(
        /**
         * The [MineEditDatumViewBinding] for our layout file layout/mine_datum_view.xml
         */
        val binding: MineEditDatumViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * The listener which is called with the [MinesDatum] we are holding when our view is
         * long clicked.
         */
        lateinit var deleteListener: (MinesDatum) -> Unit
        lateinit var playAgainListener: (MinesDatum) -> Unit

        /**
         * Updates the contents of our two `TextView`'s to reflect the [MinesDatum] parameter [item].
         * at the given position. We set the background color of the `gameBoard` `TextView` to GRAY
         * (the `TextView` with resource id `R.id.game_board`) and set its text to the [String]
         * returned by our [formatGameBoard] method for [item] (a checkerboard where a safe sector
         * is displayed as a green checkmark, and a mined sector as a red X). Then we set the text
         * of the `gameStats` `TextView` to the [String] returned by our [formatMinesDatum] method
         * for [item] (just a list of interesting properties of [item]). In addition we set the
         * `OnLongClickListener` of the `constraintViewGroup` view group holding both `TextView`s to
         * a lambda which calls our [deleteListener] field with the [MinesDatum] parameter [item] and
         * returns `true` to consume the event.
         *
         * @param item the [MinesDatum] our views are supposed to display.
         */
        fun bind(item: MinesDatum) {
            binding.gameBoard.setBackgroundColor(Color.GRAY)
            binding.gameBoard.text = formatGameBoard(item)
            binding.gameStats.text = formatMinesDatum(item)
            binding.rootView.setOnLongClickListener {
                binding.buttonGroup.visibility = View.VISIBLE
                true
            }
            binding.rootView.setOnClickListener {
                if (binding.buttonGroup.visibility == View.VISIBLE) {
                    binding.buttonGroup.visibility = View.GONE
                }
            }
            binding.deleteButton.setOnClickListener {
                if (binding.buttonGroup.visibility == View.VISIBLE) {
                    binding.buttonGroup.visibility = View.GONE
                }
                deleteListener(item)
            }
            binding.playAgainButton.setOnClickListener {
                playAgainListener(item)
            }
        }

        companion object {
            /**
             * Static factory method used to construct a [ViewHolder] using a view inflated from our
             * layout file layout/mine_datum_view.xml into a [MineEditDatumViewBinding]. We initialize
             * our [LayoutInflater] variable `val layoutInflater` with an instance for the context
             * of our [ViewGroup] parameter [parent]. Then we initialize our [MineEditDatumViewBinding]
             * variable `val binding` by using the `inflate` method of [MineEditDatumViewBinding] to
             * inflate itself using `layoutInflater`, and our [ViewGroup] parameter [parent] for
             * the LayoutParams without attaching to it. Finally we return a [ViewHolder] instance
             * constructed from `binding`.
             *
             * @param parent the [ViewGroup] our view will eventually be attached to.
             */
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    MineEditDatumViewBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}