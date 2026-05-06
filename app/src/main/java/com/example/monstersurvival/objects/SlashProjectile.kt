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
    var level = 0
    val maxLevel = 5
    var baseDamage = 15; var baseCooldown = 2.0f
    private var cooldownTimer = 0f

    fun getNextUpgradeTitle(): String {
        return "광역 공격 Lv.${level + 1}"
    }

    fun getNextUpgradeDesc(): String {
        return when (level + 1) {
            1 -> "내 주변을 빙빙 도는 방어형 무기 소환"
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
        if (level == 0) return

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