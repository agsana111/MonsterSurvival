package com.example.monstersurvival.objects

import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.cos
import kotlin.math.sin
import com.example.monstersurvival.R
import com.example.monstersurvival.com.example.monstersurvival.MainScene
import com.example.monstersurvival.com.example.monstersurvival.objects.Monster
import com.example.monstersurvival.com.example.monstersurvival.objects.Player


class OrbitProjectile(private val gctx: GameContext) : Sprite(gctx, R.drawable.player_idle), IBoxCollidable, IRecyclable {
    var damage = 0
    var isDead = true
    private var lifeTime = 0f
    private var angle = 0f
    private var distance = 0f
    private var orbitSpeed = 0f
    private var playerRef: Player? = null
    var worldX = 0f
    var worldY = 0f
    private val hitBox = RectF()

    val lastHitTimeMap = HashMap<Monster, Float>()

    override val collisionRect: RectF
        get() { hitBox.set(dstRect); return hitBox }

    override fun onRecycle() { isDead = true; playerRef = null; lastHitTimeMap.clear() }

    fun init(player: Player, startAngle: Float, distance: Float, orbitSpeed: Float, damage: Int, duration: Float, area: Float) {
        this.playerRef = player
        this.angle = startAngle
        this.distance = distance
        this.orbitSpeed = orbitSpeed
        this.damage = damage
        this.lifeTime = duration
        this.isDead = false
        setSize(50f * area, 50f * area)
    }

    override fun update(gctx: GameContext) {
        if (isDead || playerRef == null) return
        lifeTime -= gctx.frameTime
        if (lifeTime <= 0f) {
            isDead = true
            return
        }
        // 플레이어 중심으로 회전
        angle += orbitSpeed * gctx.frameTime
        worldX = playerRef!!.worldX + cos(angle) * distance
        worldY = playerRef!!.worldY + sin(angle) * distance
    }

    fun updateScreenPosition(cameraX: Float, cameraY: Float, screenCenterX: Float, screenCenterY: Float) {
        if (!isDead) setCenter(screenCenterX + (worldX - cameraX), screenCenterY + (worldY - cameraY))
    }
}


class WeaponOrbit(private val player: Player) {
    var level = 0
    val maxLevel = 5
    var baseDamage = 8; var baseCooldown = 3.0f; var duration = 2.5f
    var projectileCount = 2; var distance = 150f; var orbitSpeed = 3.0f
    private var cooldownTimer = 0f

    fun getNextUpgradeTitle(): String {
        return "책 Lv.${level + 1}"
    }

    fun getNextUpgradeDesc(): String {
        return when (level + 1) {
            1 -> "내 주변을 빙빙 도는 방어형 무기 소환"
            2 -> "회전 속도 증가"
            3 -> "책 개수 +1"
            4 -> "범위 20% 증가"
            5 -> "피해량 2배 증가 (MAX)"
            else -> "최대 레벨입니다."
        }
    }

    fun upgrade() {
        if (level < maxLevel) {
            level++
            // 추가로 데미지를 올리거나 쿨타임을 줄이는 로직을 여기에 넣으시면 됩니다!
        }
    }

    fun update(gctx: GameContext, world: World<MainScene.Layer>) {
        if (level == 0) return

        val finalCooldown = baseCooldown * (1.0f - player.cooldownReduction).coerceAtLeast(0.1f)
        cooldownTimer -= gctx.frameTime
        if (cooldownTimer <= 0f) {
            val finalDamage = (baseDamage * player.might).toInt()
            val angleStep = (Math.PI * 2) / projectileCount

            for (i in 0 until projectileCount) {
                val proj = world.obtain(OrbitProjectile::class.java) ?: OrbitProjectile(gctx)
                val startAngle = (angleStep * i).toFloat()
                proj.init(player, startAngle, distance * player.area, orbitSpeed, finalDamage, duration, player.area)
                world.add(proj, MainScene.Layer.PROJECTILE)
            }
            cooldownTimer = finalCooldown
        }
    }
}