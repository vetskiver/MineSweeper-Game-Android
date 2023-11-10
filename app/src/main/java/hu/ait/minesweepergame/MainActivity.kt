package hu.ait.minesweepergame

import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import hu.ait.minesweepergame.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var isChecked = false
    private var isGameOver = false
    private var mineCount = 0
    private var winCount = 0

    private lateinit var mineCountTextView: TextView
    private lateinit var toggleButton: ToggleButton
    private lateinit var binding: ActivityMainBinding


    private val buttonIds = arrayOf(
        intArrayOf(R.id.a1, R.id.a2, R.id.a3, R.id.a4, R.id.a5),
        intArrayOf(R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5),
        intArrayOf(R.id.c1, R.id.c2, R.id.c3, R.id.c4, R.id.c5),
        intArrayOf(R.id.d1, R.id.d2, R.id.d3, R.id.d4, R.id.d5),
        intArrayOf(R.id.e1, R.id.e2, R.id.e3, R.id.e4, R.id.e5)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        toggleButton = findViewById<ToggleButton>(R.id.toggleButton)
        MineSweeperModel.initGameArea(5)
        setupToggleButton()

        mineCountTextView = findViewById(R.id.mineCountTextView)
        placeMines(3)
        mineCountTextView.text = "Mine Count: $mineCount"

    }

    private fun setupToggleButton() {
        val toggleButton = findViewById<ToggleButton>(R.id.toggleButton)
        for (row in buttonIds) {
            for (buttonId in row) {
                val button = findViewById<Button>(buttonId)
                setTryFieldClickListener(button)
            }

        }
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            this.isChecked = isChecked

        }
    }

    private fun initializeButtons() {
        var x = 0
        var y = 0
        for (row in buttonIds) {
            for (buttonId in row) {
                val button = findViewById<Button>(buttonId)
                button.text = getString(R.string.empty)
                button.tag = Pair(x, y)
                setButtonClickListener(button)
                y++
            }
            x++
            y = 0
        }
    }


    private fun placeMines(numMines: Int) {
        initializeButtons()

        val minesPlaced = mutableSetOf<Pair<Int, Int>>()
        val random = Random

        while (minesPlaced.size < numMines) {
            val randomRow = random.nextInt(MineSweeperModel.fieldMatrix.size)
            val randomCol = random.nextInt(MineSweeperModel.fieldMatrix[0].size)

            if (MineSweeperModel.fieldMatrix[randomRow][randomCol].type == 0) {
                MineSweeperModel.fieldMatrix[randomRow][randomCol] = Field(1, 0, false, false)
                minesPlaced.add(Pair(randomRow, randomCol))
                mineCount++  // Increment the mine count
            }
        }
        mineCountTextView.text = "Mine Count: $mineCount"  // Update mine count display
    }


    private fun calculateNearbyMines(button: Button): Int {
        val tag = button.tag as Pair<Int, Int>
        val row = tag.first
        val col = tag.second

        val nearbyOffsets = arrayOf(-1, 0, 1)
        var nearbyMines = 0

        for (i in nearbyOffsets) {
            for (j in nearbyOffsets) {
                val newRow = row + i
                val newCol = col + j

                if (newRow in 0 until MineSweeperModel.fieldMatrix.size &&
                    newCol in 0 until MineSweeperModel.fieldMatrix[0].size) {
                    val field = MineSweeperModel.fieldMatrix[newRow][newCol]
                    if (field.type == 1) {
                        nearbyMines++
                    }
                }
            }
        }
        return nearbyMines
    }

    private fun setButtonClickListener(button: Button) {
        button.setOnClickListener {
            clickField(button)
        }
    }
    private fun clickField(button: Button){
        val tag = button.tag as Pair<Int, Int>
        val row = tag.first
        val col = tag.second

        val field = MineSweeperModel.fieldMatrix[row][col]

        if (!isChecked) {
            if (field.type == 1) {
                button.text = getString(R.string.x)
                gameOver()
            } else {
                val nearbyMines = calculateNearbyMines(button)
                button.text = nearbyMines.toString()
            }
            field.wasClicked = true
        }
        else{
            if (field.type == 0) {
                button.text = getString(R.string.f)
                gameOver()
            }
            if(field.type == 1){
                button.text = getString(R.string.m)
                winCount++
                if(winCount==3){
                    gameWin()
                }
            }

        }
    }
    private fun gameWin(){
        isGameOver = true
        toggleButton.setEnabled(false)

        val snackbar = Snackbar.make(
            binding.root,
            getString(R.string.won),
            Snackbar.LENGTH_INDEFINITE
        )

        snackbar.setAction(getString(R.string.restart)) {
            clearMines()
            initializeButtons()
            toggleButton.setEnabled(true)
            mineCount = 0
            placeMines(3)
            isGameOver = false
        }
        snackbar.show()
        winCount = 0
    }
    private fun setTryFieldClickListener(button: Button) {
        button.setOnClickListener {
            if (!isGameOver) {
                val text = button.text.toString()


                if (text != "X") {
                        tryField(button)
                    } else {
                        gameOver()
                    }

            }
        }
    }

    private fun tryField(button: Button) {
        if(isChecked) {
            val nearbyMines = calculateNearbyMines(button)
            button.text = nearbyMines.toString()
            val tag = button.tag as Pair<Int, Int>
            val row = tag.first
            val col = tag.second
            val field = MineSweeperModel.fieldMatrix[row][col]
            field.wasClicked = true
        }
    }

    private fun clearMines() {
        for (row in MineSweeperModel.fieldMatrix) {
            for (field in row) {
                if (field.type == 1) {
                    field.type = 0
                }
            }
        }
    }

    private fun gameOver() {
        isGameOver = true
        toggleButton.isEnabled = false

        for (row in buttonIds) {
            for (buttonId in row) {
                val button = findViewById<Button>(buttonId)
                button.isEnabled = false
            }
        }

        val snackbar = Snackbar.make(
            binding.root,
            getString(R.string.game_over),
            Snackbar.LENGTH_INDEFINITE
        )

        snackbar.setAction(R.string.restart) {
            clearMines()
            initializeButtons()

            for (row in buttonIds) {
                for (buttonId in row) {
                    val button = findViewById<Button>(buttonId)
                    button.isEnabled = true
                }
            }

            toggleButton.isEnabled = true
            mineCount = 0
            placeMines(3)
            isGameOver = false
        }

        snackbar.show()
    }


}