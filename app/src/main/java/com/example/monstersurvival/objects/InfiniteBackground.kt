package com.example.monstersurvival.com.example.monstersurvival.objects


import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.ceil
import kotlin.math.floor

class InfiniteBackground(
    private val gctx: GameContext,
    resId: Int,
    private val tileWidth: Float,
) : Sprite(gctx, resId) {


    private val tileHeight = tileWidth * bitmapHeight / bitmapWidth.toFloat()


    var targetX = 0f
    var targetY = 0f

    init {
        setSize(tileWidth, tileHeight)
    }

    override fun update(gctx: GameContext) {

    }

    override fun draw(canvas: Canvas) {
        val metrics = gctx.metrics
        val screen = metrics.screenRect


        val offsetX = targetX - metrics.width / 2f
        val offsetY = targetY - metrics.height / 2f

        val startCol = floor((screen.left + offsetX) / tileWidth).toInt()
        val endCol = ceil((screen.right + offsetX) / tileWidth).toInt()
        val startRow = floor((screen.top + offsetY) / tileHeight).toInt()
        val endRow = ceil((screen.bottom + offsetY) / tileHeight).toInt()


        for (row in startRow..endRow) {
            for (col in startCol..endCol) {
                val drawX = col * tileWidth - offsetX
                val drawY = row * tileHeight - offsetY

                setCenter(drawX + tileWidth / 2f, drawY + tileHeight / 2f)
                canvas.drawBitmap(bitmap, srcRect, dstRect, null)
            }
        }
    }
}