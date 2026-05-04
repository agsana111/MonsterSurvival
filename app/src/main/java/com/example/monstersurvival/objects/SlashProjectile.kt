package com.example.monstersurvival.objects

import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import com.example.monstersurvival.R
import com.example.monstersurvival.com.example.monstersurvival.MainScene
import com.example.monstersurvival.com.example.monstersurvival.objects.Monster
import com.example.monstersurvival.com.example.monstersurvival.objects.Player


class SlashProjectile(private val gctx: GameContext) : Sprite(gctx, R.drawable.player_idle), IBoxCollidable, IRecyclable {
    var damage = 0
    var isDead = true
    private var lifeTime = 0f
    private val hitBox = RectF()
    private var playerRef: Player? = null

    val hitList = HashSet<Monster>()

    override val collisionRect: RectF
        get() { hitBox.set(dstRect); return hitBox }

    override fun onRecycle() { isDead = true; playerRef = null; hitList.clear() }

    fun init(player: Player, damage: Int, area: Float) {
        this.playerRef = player
        this.damage = damage
        this.lifeTime = 0.2f // (베기 판정)
        this.isDead = false
        setSize(300f * area, 300f * area)
    }

    override fun update(gctx: GameContext) {
        if (isDead) return
        lifeTime -= gctx.frameTime
        if (lifeTime <= 0f) isDead = true
    }

    fun updateScreenPosition(cameraX: Float, cameraY: Float, screenCenterX: Float, screenCenterY: Float) {
        if (isDead || playerRef == null) return

        setCenter(screenCenterX + (playerRef!!.worldX - cameraX), screenCenterY + (playerRef!!.worldY - cameraY))
    }
}


class WeaponSlash(private val player: Player) {
    var level = 1
    val maxLevel = 9
    var baseDamage = 15; var baseCooldown = 2.0f
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
            val proj = world.obtain(SlashProjectile::class.java) ?: SlashProjectile(gctx)
            val finalDamage = (baseDamage * player.might).toInt()
            proj.init(player, finalDamage, player.area)
            world.add(proj, MainScene.Layer.PROJECTILE)
            cooldownTimer = finalCooldown
        }
    }
}