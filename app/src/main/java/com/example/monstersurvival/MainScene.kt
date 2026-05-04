// MainScene.kt
package com.example.monstersurvival.com.example.monstersurvival

import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.JoyStick
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.math.PI
import com.example.monstersurvival.R
import com.example.monstersurvival.com.example.monstersurvival.objects.InfiniteBackground
import com.example.monstersurvival.com.example.monstersurvival.objects.Monster
import com.example.monstersurvival.com.example.monstersurvival.objects.Player
import com.example.monstersurvival.com.example.monstersurvival.objects.MonsterPool
import com.example.monstersurvival.com.example.monstersurvival.objects.MonsterType
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith

class MainScene(gctx: GameContext) : Scene(gctx) {
    enum class Layer {
        BACKGROUND,
        MONSTER,   // 적 캐릭터
        PROJECTILE, // 플레이어의 무기
        PLAYER,    // 주인공
        UI;        // 경험치 바, 타이머 등

        companion object {
            val values = values()
        }
    }

    override val world = World(Layer.values)
    private val player = Player(gctx)
    private val background = InfiniteBackground(gctx, R.drawable.bg_meadow, 512f)
    private val joystick = JoyStick(
        gctx,
        R.drawable.joystick_bg,
        R.drawable.joystick_thumb,
        200f,
        gctx.metrics.height - 200f,
        150f,
        60f
    )

    init {
        world.add(background, Layer.BACKGROUND)
        world.add(player, Layer.PLAYER)
        world.add(joystick, Layer.UI)
    }

    private var worldX = 0f
    private var worldY = 0f

    private var playTime = 0f
    private var monsterSpawnTimer = 0f

    override fun update(gctx: GameContext) {
        super.update(gctx)

        playTime += gctx.frameTime

        val power = joystick.power
        val angle = joystick.angle

        val screenCenterX = gctx.metrics.width / 2f
        val screenCenterY = gctx.metrics.height / 2f

        if (power > 0f) {
            val dx = cos(angle) * power
            val dy = sin(angle) * power

            worldX += dx * 500f * gctx.frameTime
            worldY += dy * 500f * gctx.frameTime

            player.move(dx, dy, gctx.frameTime)
        } else {
            player.move(0f, 0f, gctx.frameTime)
        }
        background.targetX = worldX
        background.targetY = worldY

        val currentSpawnInterval = kotlin.math.max(0.2f, 2.0f - (playTime / 10f) * 0.1f)
        monsterSpawnTimer += gctx.frameTime

        if (monsterSpawnTimer >= currentSpawnInterval) {
            spawnMonster(gctx, playTime)
            monsterSpawnTimer = 0f
        }

        val monsters = world.objectsAt(Layer.MONSTER)
        val monsterCount = monsters.size
        for (obj in monsters) {
            if (obj is Monster) {
                // 플레이어의 월드좌표(worldX, worldY)를 향해 이동
                obj.moveTowards(worldX, worldY, gctx.frameTime)
                // 바뀐 월드좌표를 바탕으로 화면 위치 업데이트
                obj.updateScreenPosition(worldX, worldY, screenCenterX, screenCenterY)
            }
        }

        val overlapDist = 60f
        val overlapDistSq = overlapDist * overlapDist

        for (i in 0 until monsterCount) {
            val m1 = monsters[i] as? Monster ?: continue
            if (!m1.isAlive) continue

            for (j in i + 1 until monsterCount) {
                val m2 = monsters[j] as? Monster ?: continue
                if (!m2.isAlive) continue

                val dx = m1.worldX - m2.worldX
                val dy = m1.worldY - m2.worldY
                val distSq = dx * dx + dy * dy

                if (distSq < overlapDistSq && distSq > 0.1f) {
                    val pushForce = 2.0f
                    m1.worldX += dx * 0.05f * pushForce
                    m1.worldY += dy * 0.05f * pushForce
                    m2.worldX -= dx * 0.05f * pushForce
                    m2.worldY -= dy * 0.05f * pushForce
                }
            }
        }

        var totalPlayerPushX = 0f
        var totalPlayerPushY = 0f
        var isColliding = false

        for (i in 0 until monsterCount) {
            val monster = monsters[i] as? Monster ?: continue

            if (monster.isAlive && player.collidesWith(monster)) {
                isColliding = true
                player.takeDamage(5)

                var dx = worldX - monster.worldX
                var dy = worldY - monster.worldY

                val dist = kotlin.math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                if (dist > 0.1f) {
                    dx /= dist
                    dy /= dist
                } else {
                    dx = 1f; dy = 1f
                }

                val monsterPushForce = 150f * gctx.frameTime
                monster.worldX -= dx * monsterPushForce
                monster.worldY -= dy * monsterPushForce

                totalPlayerPushX += dx
                totalPlayerPushY += dy
            }
        }

        if (isColliding) {
            val pushDist = kotlin.math.sqrt((totalPlayerPushX * totalPlayerPushX + totalPlayerPushY * totalPlayerPushY).toDouble()).toFloat()
            if (pushDist > 0.1f) {
                totalPlayerPushX /= pushDist
                totalPlayerPushY /= pushDist
            }
            val maxResistanceForce = 350f * gctx.frameTime
            worldX += totalPlayerPushX * maxResistanceForce
            worldY += totalPlayerPushY * maxResistanceForce
        }
    }

    private fun spawnMonster(gctx: GameContext, playTime: Float) {
        val randomAngle = kotlin.random.Random.nextDouble(0.0, kotlin.math.PI * 2).toFloat()
        val distance = 1200f
        val spawnX = worldX + kotlin.math.cos(randomAngle) * distance
        val spawnY = worldY + kotlin.math.sin(randomAngle) * distance

        val randomValue = kotlin.random.Random.nextInt(100)
        val spawnType = when {
            randomValue < 60 -> MonsterType.NORMAL
            randomValue < 80 -> MonsterType.FAST
            else -> MonsterType.HEAVY
        }

        val monster = world.obtain(Monster::class.java) ?: Monster(gctx)
        monster.init(spawnType, spawnX, spawnY, playTime)

        world.add(monster, Layer.MONSTER)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return joystick.onTouchEvent(event)
    }
}