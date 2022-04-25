package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.Move
import com.github.eliascoelho911.robok.util.getAttrColor
import kotlinx.android.synthetic.main.rubik_cube_solve_player.view.current_move_txt
import kotlinx.android.synthetic.main.rubik_cube_solve_player.view.moves_txt
import kotlinx.android.synthetic.main.rubik_cube_solve_player.view.next_btn
import kotlinx.android.synthetic.main.rubik_cube_solve_player.view.play_btn
import kotlinx.android.synthetic.main.rubik_cube_solve_player.view.previous_btn
import kotlinx.android.synthetic.main.rubik_cube_solve_player.view.progress_bar

class RubikCubeSolvePlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    var previousBtnOnClickListener: () -> Unit = {}
    var nextBtnOnClickListener: () -> Unit = {}
    var playBtnOnClickListener: () -> Unit = {}
    var amountOfMoves: Int = 0
        set(value) {
            field = value
            progress_bar.max = value
        }

    init {
        inflate(context, R.layout.rubik_cube_solve_player, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupListeners()
    }

    fun changeCurrentMove(move: Move, index: Int) {
        current_move_txt.text = move
        moves_txt.text = buildMovesText(index + 1, amountOfMoves)
        progress_bar.progress = index
    }

    fun solved() {
        current_move_txt.text = ""
        moves_txt.text = context.getString(R.string.solved)
        progress_bar.progress = amountOfMoves
    }

    fun play() {
        play_btn.setImageResource(R.drawable.ic_pause_24)
    }

    fun pause() {
        play_btn.setImageResource(R.drawable.ic_play_arrow_24)
    }

    private fun setupListeners() {
        previous_btn.setOnClickListener { previousBtnOnClickListener() }
        next_btn.setOnClickListener { nextBtnOnClickListener() }
        play_btn.setOnClickListener { playBtnOnClickListener() }
    }

    private fun buildMovesText(index: Int, amountOfMoves: Int): SpannableString {
        val highlightColor = context.getAttrColor(R.attr.colorPrimary)
        val movesString = context.getString(R.string.moves, index, amountOfMoves)
        val start = movesString.indexOf(" ")
        val end = start + index.toString().length + 2
        return SpannableString(movesString).apply {
            setSpan(ForegroundColorSpan(highlightColor),
                start,
                end,
                SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }
}