package com.example.monstersurvival.com.example.monstersurvival.objects

import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import com.example.monstersurvival.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Monster(private val gameContext: GameContext) :
    AnimSprite(gameContext, R.drawable.player_walk, 10f, 4),
    IBoxCollidable, IRecyclable {

    var type: MonsterType = MonsterType.NORMAL
    var worldX: Float = 0f
    var worldY: Float = 0f
    var isAlive: Boolean = false
    var maxHp: Int = 10
    var hp: Int = 10
    var speed: Float = 150f
    private var isFacingLeft = false

    private val hitBox = RectF()
    override val collisionRect: RectF
        get() {

            val marginX = dstRect.width() * 0.2f
            val marginY = dstRect.height() * 0.2f

            hitBox.set(
                dstRect.left + marginX,
                dstRect.top + marginY,
                dstRect.right - marginX,
                dstRect.bottom - marginY
            )
            return hitBox
        }

    override fun onRecycle() {
        isAlive = false
    }


    fun init(spawnType: MonsterType, startX: Float, startY: Float, playTime: Float) {
        this.type = spawnType
        this.worldX = startX
        this.worldY = startY
        this.isAlive = true
        this.isFacingLeft = false

        var baseMaxHp = 10
        when (type) {
            MonsterType.NORMAL -> { baseMaxHp = 30; speed = 150f }
            MonsterType.FAST -> { baseMaxHp = 15; speed = 300f }
            MonsterType.HEAVY -> { baseMaxHp = 100; speed = 80f }
        }

        val hpMultiplier = 1.0f + ((playTime/30f) * 1.5f)
        this.maxHp = (baseMaxHp * hpMultiplier).toInt()
        this.hp = this.maxHp

        val singleFrameWidth = bitmapWidth / frameCount.toFloat()
        this.height = when(type) {
            MonsterType.FAST -> 150f
            MonsterType.HEAVY -> 350f
            else -> 250f
        }
        this.width = this.height * (singleFrameWidth / bitmapHeight)
        syncDstRect()
    }

    fun takeDamage(damage: Int): Boolean{
        if (!isAlive) return false
        hp -= damage
        if (hp <= 0) {
            hp = 0
            isAlive = false
        }
        return true
    }

    fun moveTowards(targetWorldX: Float, targetWorldY: Float, frameTime: Float) {
        if (!isAlive) return
        val dx = targetWorldX - worldX
        val dy = targetWorldY - worldY

        if (dx < 0) isFacingLeft = true
        else if (dx > 0) isFacingLeft = false

        val angle = atan2(dy, dx)
        worldX += cos(angle) * speed * frameTime
        worldY += sin(angle) * speed * frameTime
    }

    fun updateScreenPosition(cameraX: Float, cameraY: Float, screenCenterX: Float, screenCenterY: Float) {
        if (!isAlive) return
        this.x = screenCenterX + (worldX - cameraX)
        this.y = screenCenterY + (worldY - cameraY)
        syncDstRect()
    }

    override fun draw(canvas: Canvas) {
        if (!isAlive) return
        if (isFacingLeft) {
            canvas.save()
            canvas.scale(-1f, 1f, x, y)
            super.draw(canvas)
            canvas.restore()
        } else {
            super.draw(canvas)
        }
    }
}