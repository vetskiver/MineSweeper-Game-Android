package hu.ait.minesweepergame

data class Field(
    var type: Int,
    var minesAround: Int,
    var isFlagged: Boolean,
    var wasClicked: Boolean
)