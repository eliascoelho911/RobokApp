package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.databinding.RubikCubeSolvePlayerBinding
import com.github.eliascoelho911.robok.rubikcube.Move
import com.github.eliascoelho911.robok.util.getAttrColor

class RubikCubeSolvePlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = RubikCubeSolvePlayerBinding.inflate(LayoutInflater.from(context), this, true)

    var previousBtnOnClickListener: () -> Unit = {}
    var nextBtnOnClickListener: () -> Unit = {}
    var playBtnOnClickListener: () -> Unit = {}
    var amountOfMoves: Int = 0
        set(value) {
            field = value
            binding.progressBar.max = value
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupListeners()
    }

    fun changeCurrentMove(move: Move, index: Int) {
        binding.currentMoveTxt.text = move
        binding.movesTxt.text = buildMovesText(index + 1, amountOfMoves)
        binding.progressBar.progress = index
    }

    fun solved() {
        binding.currentMoveTxt.text = ""
        binding.movesTxt.text = context.getString(R.string.solved)
        binding.progressBar.progress = amountOfMoves
    }

    fun play() {
        binding.playBtn.setImageResource(R.drawable.ic_pause_24)
    }

    fun pause() {
        binding.playBtn.setImageResource(R.drawable.ic_play_arrow_24)
    }

    private fun setupListeners() {
        binding.previousBtn.setOnClickListener { previousBtnOnClickListener() }
        binding.nextBtn.setOnClickListener { nextBtnOnClickListener() }
        binding.playBtn.setOnClickListener { playBtnOnClickListener() }
    }

    private fun buildMovesText(index: Int, amountOfMoves: Int): SpannableString {
        val highlightColor = context.getAttrColor(com.google.android.material.R.attr.colorPrimary)
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