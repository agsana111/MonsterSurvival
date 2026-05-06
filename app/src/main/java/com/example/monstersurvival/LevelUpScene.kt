package com.example.monstersurvival

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.util.LabelUtil
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

enum class UpgradeType {
    WEAPON_AUTO, WEAPON_SLASH, WEAPON_ORBIT,
    STAT_MIGHT, STAT_AREA, STAT_COOLDOWN, HP_RECOVERY
}

data class UpgradeOption(
    val type: UpgradeType,
    val title: String,
    val desc: String
)

class LevelUpScene(
    gctx: GameContext,
    private val previousScene: Scene,
    private val options: List<UpgradeOption>,
    private val onSelect: (UpgradeOption) -> Unit
) : Scene(gctx) {
    private val bgPaint = Paint().apply { color = Color.parseColor("#D9000000") }
    private val cardPaint = Paint().apply { color = Color.parseColor("#444455") }
    private val borderPaint = Paint().apply { color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = 5f }

    private val titleLabel = LabelUtil(60f, Color.YELLOW, Paint.Align.CENTER)
    private val descLabel = LabelUtil(35f, Color.WHITE, Paint.Align.CENTER)

    private val cards = Array(3) { RectF() }

    init {
        val cx = gctx.metrics.width / 2f
        val cy = gctx.metrics.height / 2f
        val cardW = gctx.metrics.width * 0.8f
        val cardH = 250f
        val spacing = 40f

        val startY = cy - cardH - spacing - (cardH / 2f)
        for (i in 0 until 3) {
            val top = startY + i * (cardH + spacing)
            cards[i] = RectF(cx - cardW / 2f, top, cx + cardW / 2f, top + cardH)
        }
    }

    override fun update(gctx: GameContext) {
        super.update(gctx)
    }

    override fun draw(canvas: Canvas) {
        previousScene.draw(canvas)

        canvas.drawPaint(bgPaint)

        val w = gctx.metrics.width
        val h = gctx.metrics.height

        titleLabel.draw(canvas, "레벨 업! 보상을 선택하세요", w / 2f, 250f)

        for (i in 0 until options.size) {
            val rect = cards[i]
            canvas.drawRoundRect(rect, 20f, 20f, cardPaint)
            canvas.drawRoundRect(rect, 20f, 20f, borderPaint)

            val option = options[i]
            titleLabel.draw(canvas, option.title, rect.centerX(), rect.top + 100f)
            descLabel.draw(canvas, option.desc, rect.centerX(), rect.top + 180f)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            for (i in 0 until options.size) {
                if (cards[i].contains(event.x, event.y)) {
                    onSelect(options[i])
                    return true
                }
            }
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        return true
    }
}