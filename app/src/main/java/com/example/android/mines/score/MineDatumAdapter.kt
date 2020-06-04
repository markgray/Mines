@file:Suppress("MemberVisibilityCanBePrivate")

package com.example.android.mines.score

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mines.R
import com.example.android.mines.database.MinesDatum
import com.example.android.mines.formatGameBoard
import com.example.android.mines.formatMinesDatum

class MineDatumAdapter: RecyclerView.Adapter<MineDatumAdapter.ViewHolder>() {

    var data = listOf<MinesDatum>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new `ViewHolder` of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     * The new ViewHolder will be used to display items of the adapter using [onBindViewHolder].
     * Since it will be re-used to display different items in the data set, it is a good idea to
     * cache references to sub views of the View to avoid unnecessary `View.findViewById` calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = data.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the `ViewHolder.itemView` to reflect the item at the given position.
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method again if
     * the position of the item changes in the data set unless the item itself is invalidated or
     * the new position cannot be determined. For this reason, you should only use the `position`
     * parameter while acquiring the related data item inside this method and should not keep a
     * copy of it. If you need the position of an item later on (e.g. in a click listener), use
     * `ViewHolder.getAdapterPosition` which will have the updated adapter position.
     *
     * Override `onBindViewHolder (VH holder, int position, List<Object> payloads)` instead if
     * Adapter can handle efficient partial bind. The payloads List parameter is a merge list from
     * notifyItemChanged(int, Object) or notifyItemRangeChanged(int, int, Object). If the payloads
     * list is not empty, the ViewHolder is currently bound to old data and Adapter may run an
     * efficient partial update using the payload info. If the payload is empty, Adapter must run
     * a full bind. Adapter should not assume that the payload passed in notify methods will be
     * received by onBindViewHolder(). For example when the view is not attached to the screen,
     * the payload in notifyItemChange() will be simply dropped.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        val gameStats: TextView = itemView.findViewById(R.id.game_stats)
        val gameBoard: TextView = itemView.findViewById(R.id.game_board)

        fun bind(item: MinesDatum) {
            gameBoard.setBackgroundColor(Color.GRAY)
            gameStats.text = formatMinesDatum(item)
            gameBoard.text = formatGameBoard(item)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.mine_datum_view, parent, false)

                return ViewHolder(view)
            }
        }
    }

}