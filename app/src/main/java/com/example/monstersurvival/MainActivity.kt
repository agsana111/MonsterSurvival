package com.example.monstersurvival

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.monstersurvival.com.example.monstersurvival.MonsterSurvivalActivity
import com.example.monstersurvival.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            val intent = Intent(this, MonsterSurvivalActivity::class.java)
            startActivity(intent)
        }

        binding.btnExit.setOnClickListener {
            finishAffinity()
        }
    }
}