package com.example.android.mines

import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    lateinit var gameState: MutableList<SectorContent>
    var screenWidth: Int = 0
    var screenHeight: Int = 0
    var mineCount: Int = 0

    fun init(width: Int, height: Int, mines: Int) {
        screenWidth = width
        screenHeight = height
        mineCount = mines
        gameState = ArrayList(screenWidth * screenHeight)
        for (rows in 0 until screenHeight) {
            for (columns in 0 until screenWidth) {
                gameState.add(SectorContent(rows, columns))
            }
        }

    }
}