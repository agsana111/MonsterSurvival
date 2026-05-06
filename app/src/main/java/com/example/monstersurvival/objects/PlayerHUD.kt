package com.example.monstersurvival.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.monstersurvival.com.example.monstersurvival.objects.Player
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Gauge
import kr.ac.tukorea.ge.spgp2026.a2dg.util.LabelUtil
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class PlayerHUD(
    private val gctx: GameContext,
    private val player: Player
) : IGameObject {

    private val expGauge = Gauge(0.03f, Color.CYAN, Color.DKGRAY)

    private val levelLabel = LabelUtil(100f, Color.CYAN, Paint.Align.LEFT)

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        val screenWidth = gctx.metrics.width

        val progress = player.exp.toFloat() / player.maxExp.toFloat()

        expGauge.draw(canvas, 0f, 1700f, screenWidth, progress)

        levelLabel.draw(canvas, "Lv. ${player.level}", 20f, 1650f)
    }
}