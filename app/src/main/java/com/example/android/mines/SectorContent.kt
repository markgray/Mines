package com.example.android.mines

class SectorContent(var row: Int, var column: Int) {
    var hasMine: Boolean = false
    var childNum: Int = 0
    var neighbors: MutableList<Int> = ArrayList()
}