package hu.ait.minesweepergame

object MineSweeperModel {
    lateinit var fieldMatrix: Array<Array<Field>>

    fun initGameArea(size: Int) {
        fieldMatrix = Array(size) { Array(size) { Field(0, 0, false, false) } }
    }
}