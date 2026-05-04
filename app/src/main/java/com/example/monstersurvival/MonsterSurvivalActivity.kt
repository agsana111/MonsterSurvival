package com.example.monstersurvival.com.example.monstersurvival

import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MonsterSurvivalActivity : BaseGameActivity() {

    override val drawsDebugGrid: Boolean = true
    override val drawsDebugInfo: Boolean = true

    override fun createRootScene(gctx: GameContext): Scene {

        return MainScene(gctx)
    }
}