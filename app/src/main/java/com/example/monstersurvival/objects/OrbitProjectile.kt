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
    var level = 1
    val maxLevel = 9
    var baseDamage = 8; var baseCooldown = 3.0f; var duration = 2.5f
    var projectileCount = 2; var distance = 150f; var orbitSpeed = 3.0f
    private var cooldownTimer = 0f

    fun upgrade() {
        if (level >= maxLevel) return
        level++
        when (level) { 2 -> { /* TODO */ } }
    }

    fun update(gctx: GameContext, world: World<MainScene.Layer>) {
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