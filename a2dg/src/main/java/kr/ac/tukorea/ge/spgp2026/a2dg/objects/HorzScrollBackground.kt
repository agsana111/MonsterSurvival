package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

open class HorzScrollBackground(
    gctx: GameContext,
    resId: Int,
    private val speed: Float,
) : Sprite(gctx, resId) {
    private val screenWidth = gctx.metrics.width
    private val screenHeight = gctx.metrics.height

    private val tileWidth = bitmapWidth * screenHeight / bitmapHeight.toFloat()

    init {
        setCenterProportionalHeight(screenWidth / 2f, screenHeight / 2f, tileWidth)
    }

    override fun update(gctx: GameContext) {
        x += speed * gctx.frameTime
    }

    override fun draw(canvas: Canvas) {
        var curr = x % tileWidth

        if (curr > 0f) curr -= tileWidth

        while (curr < screenWidth) {
            dstRect.set(curr, 0f, curr + tileWidth, screenHeight.toFloat())
            canvas.drawBitmap(bitmap, null, dstRect, null)
            curr += tileWidth
        }
    }
}