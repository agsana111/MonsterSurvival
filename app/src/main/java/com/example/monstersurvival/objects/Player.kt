package com.example.monstersurvival.com.example.monstersurvival.objects

import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import com.example.monstersurvival.R

class Player(private val gameContext: GameContext) : AnimSprite(gameContext, R.drawable.player_idle, 8f, 4), IBoxCollidable {

    private val playerHeight = 250f
    private val speed = 500f
    var maxHp: Int = 100
    var currentHp: Int = 100

    var level = 1
    var exp = 0
    var maxExp = 100

    var might: Float = 1.0f       // 공격력 증가
    var area: Float = 1.0f        // 공격 범위/투사체 크기 증가
    var cooldownReduction: Float = 0f // 쿨타임 감소
    var isDead: Boolean = false
    var worldX: Float = 0f
    var worldY: Float = 0f

    var damageMultiplier: Float = 1.0f

    enum class State { IDLE, WALK }
    var state = State.IDLE

    private var isFacingLeft = false
    private var invincibleTimer = 0f
    private val hitBox = RectF()

    override val collisionRect: RectF
        get() {

            val marginX = dstRect.width() * 0.35f
            val marginY = dstRect.height() * 0.35f

            hitBox.set(
                dstRect.left + marginX,
                dstRect.top + marginY,
                dstRect.right - marginX,
                dstRect.bottom - marginY
            )
            return hitBox
        }

    override fun update(gctx: GameContext) {
        super.update(gctx)
        if (invincibleTimer > 0f) {
            invincibleTimer -= gctx.frameTime
        }
    }

    private fun setProperSize() {
        // 전체 너비(1774)를 프레임 수(4)로 나눈 1프레임의 진짜 너비를 구함
        val singleFrameWidth = bitmapWidth / frameCount.toFloat()

        // 세로 크기(250f)를 기준으로 1프레임의 원래 비율에 맞춰 가로 크기 결정
        this.height = 250f
        this.width = this.height * (singleFrameWidth / bitmapHeight)

        syncDstRect()
    }

    init {
        setCenter(gameContext.metrics.width / 2f, gameContext.metrics.height / 2f)
        setProperSize()
    }


    fun move(dx: Float, dy: Float, frameTime: Float) {
        if (isDead) return

        if (dx != 0f || dy != 0f) {
            changeState(State.WALK)

            if (dx < 0) {
                isFacingLeft = true
            } else if (dx > 0) {
                isFacingLeft = false
            }
        } else {
            changeState(State.IDLE)
        }
    }

    private fun changeState(newState: State) {
        if (state == newState) return
        state = newState
        when (state) {
            State.IDLE -> {
                this.bitmap = gameContext.res.getBitmap(R.drawable.player_idle)
                this.frameCount = 4
                this.fps = 8f
            }
            State.WALK -> {
                this.bitmap = gameContext.res.getBitmap(R.drawable.player_walk)
                this.frameCount = 4
                this.fps = 8f
            }
        }
        setProperSize()
    }

    fun takeDamage(damage: Int) {
        if (isDead || invincibleTimer > 0f) return

        currentHp -= damage
        if (currentHp <= 0) {
            currentHp = 0
            isDead = true
        } else {
            invincibleTimer = 0.15f
        }
    }

    override fun draw(canvas: Canvas) {

        if (invincibleTimer > 0f && (invincibleTimer * 10).toInt() % 2 == 0) return

        if (isFacingLeft) {
            canvas.save()
            canvas.scale(-1f, 1f, x, y)
            super.draw(canvas)
            canvas.restore()
        } else {
            super.draw(canvas)
        }
    }

    fun addExp(amount: Int) {
        exp += amount

        while (exp >= maxExp) {
            exp -= maxExp
            levelUp()
        }
    }

    private fun levelUp() {
        level++

        maxExp = (maxExp * 1.2f).toInt() + 50

        // TODO: 나중에 여기서 게임 일시 중지 후 강화 Scene을 위에 추가할예정
    }
}