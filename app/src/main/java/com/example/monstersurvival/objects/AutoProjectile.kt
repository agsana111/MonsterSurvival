package com.example.monstersurvival.objects

import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import com.example.monstersurvival.R
import com.example.monstersurvival.com.example.monstersurvival.MainScene
import com.example.monstersurvival.com.example.monstersurvival.objects.Monster
import com.example.monstersurvival.com.example.monstersurvival.objects.Player


class AutoProjectile(private val gctx: GameContext) : Sprite(gctx, R.drawable.player_idle), IBoxCollidable, IRecyclable {
    var worldX = 0f
    var worldY = 0f
    var damage = 0
    var isDead = true
    private var dx = 0f; private var dy = 0f; private var speed = 0f; private var lifeTime = 0f
    private val hitBox = RectF()

    override val collisionRect: RectF
        get() { hitBox.set(dstRect); return hitBox }

    override fun onRecycle() { isDead = true }

    fun init(startX: Float, startY: Float, targetX: Float, targetY: Float, damage: Int, speed: Float, area: Float) {
        this.worldX = startX; this.worldY = startY; this.damage = damage
        this.speed = speed; this.lifeTime = 3f; this.isDead = false
        val angle = atan2(targetY - startY, targetX - startX)
        this.dx = cos(angle); this.dy = sin(angle)
        setSize(40f * area, 40f * area)
    }

    override fun update(gctx: GameContext) {
        if (isDead) return
        lifeTime -= gctx.frameTime
        if (lifeTime <= 0f) isDead = true
        worldX += dx * speed * gctx.frameTime; worldY += dy * speed * gctx.frameTime
    }

    fun updateScreenPosition(cameraX: Float, cameraY: Float, screenCenterX: Float, screenCenterY: Float) {
        if (!isDead) setCenter(screenCenterX + (worldX - cameraX), screenCenterY + (worldY - cameraY))
    }
}


class WeaponAuto(private val player: Player) {
    var level = 1
    val maxLevel = 5
    var baseDamage = 10; var baseCooldown = 1.0f; var baseSpeed = 600f; var projectileCount = 1
    private var cooldownTimer = 0f

    fun getNextUpgradeTitle(): String {
        return "마법 탄환 Lv.${level + 1}"
    }

    fun getNextUpgradeDesc(): String {
        return when (level + 1) {
            2 -> "발사체 개수 +1"
            3 -> "피해량 +15 증가"
            4 -> "쿨타임 10% 감소"
            5 -> "적 관통 효과 추가 (MAX)"
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
        val finalCooldown = baseCooldown * (1.0f - player.cooldownReduction).coerceAtLeast(0.1f)
        cooldownTimer -= gctx.frameTime
        if (cooldownTimer <= 0f) {
            fire(gctx, world)
            cooldownTimer = finalCooldown
        }
    }

    private fun fire(gctx: GameContext, world: World<MainScene.Layer>) {
        val monsters = world.objectsAt(MainScene.Layer.MONSTER)
        var closestMonster: Monster? = null
        var minDistSq = Float.MAX_VALUE

        for (m in monsters) {
            val monster = m as? Monster ?: continue
            if (!monster.isAlive) continue
            val distSq = (monster.worldX - player.worldX) * (monster.worldX - player.worldX) + (monster.worldY - player.worldY) * (monster.worldY - player.worldY)
            if (distSq < minDistSq) { minDistSq = distSq; closestMonster = monster }
        }

        if (closestMonster != null) {
            for (i in 0 until projectileCount) {
                val proj = world.obtain(AutoProjectile::class.java) ?: AutoProjectile(gctx)
                val finalDamage = (baseDamage * player.might).toInt()
                proj.init(player.worldX, player.worldY, closestMonster.worldX, closestMonster.worldY, finalDamage, baseSpeed, player.area)
                world.add(proj, MainScene.Layer.PROJECTILE)
            }
        }
    }
}