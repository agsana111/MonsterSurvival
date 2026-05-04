package com.example.monstersurvival.com.example.monstersurvival.objects

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext


class MonsterPool {
    private val inactiveMonsters = mutableListOf<Monster>()

    fun get(gctx: GameContext, type: MonsterType, x: Float, y: Float, playTime: Float): Monster {
        val monster = if (inactiveMonsters.isNotEmpty()) {
            inactiveMonsters.removeLast()
        } else {
            Monster(gctx)
        }

        monster.init(type, x, y, playTime)
        return monster
    }

    fun release(monster: Monster) {
        monster.isAlive = false
        inactiveMonsters.add(monster)
    }
}