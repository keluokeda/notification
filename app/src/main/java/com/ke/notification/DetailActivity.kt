package com.ke.notification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import com.ke.notification.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detail)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "详情"
        binding.content.text = intent.getStringExtra("content") ?: "什么也没有接收到"
        binding.button.setOnClickListener {
            startActivity(Intent(this, CreateNotificationChannelActivity::class.java))
        }

        val notificationId = intent.getIntExtra("notificationId", -1)
        if (notificationId != -1) {
            NotificationManagerCompat.from(this).cancel(notificationId)
        }
    }
}