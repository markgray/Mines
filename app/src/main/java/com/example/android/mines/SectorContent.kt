package com.example.android.mines

/**
 * Class which holds all the information needed to describe the features of an individual sector
 * on the game board.
 */
class SectorContent(
    /**
     * Row that this sector is in on the game board
     */
    var row: Int,
    /**
     * Column that this sector is in on the game board
     */
    var column: Int
) {
    /**
     * If true this sector has a Mine in it
     */
    var hasMine: Boolean = false

    /**
     * If true the sector has correctly been marked as "Safe" or as "Mine"
     */
    var hasBeenChecked: Boolean = false

    /**
     * The child number of this sector in the game board GridLayout
     */
    var childNum: Int = 0

    /**
     * List of the index numbers of the neighboring sectors of this sector
     */
    var neighbors: MutableList<Int> = ArrayList()
}