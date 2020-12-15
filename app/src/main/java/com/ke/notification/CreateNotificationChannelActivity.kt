package com.ke.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ke.notification.databinding.ActivityCreateNotificationChannelBinding
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class CreateNotificationChannelActivity : AppCompatActivity() {
    private var currentImportance = -1

    private lateinit var binding: ActivityCreateNotificationChannelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_create_notification_channel)
        binding = ActivityCreateNotificationChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "创建通知渠道"

//        actionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP
        binding.apply {
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                currentImportance = when (checkedId) {
                    R.id.none -> NotificationManager.IMPORTANCE_NONE
                    R.id.min -> NotificationManager.IMPORTANCE_MIN
                    R.id.low -> NotificationManager.IMPORTANCE_LOW
                    R.id.def -> NotificationManager.IMPORTANCE_DEFAULT
                    R.id.high -> NotificationManager.IMPORTANCE_HIGH
                    else -> -1
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menu.add(0, 1, 1, "添加").setIcon(R.drawable.baseline_done_white_24dp)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    @SuppressLint("WrongConstant")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            if (currentImportance == -1) {
                AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("请选择渠道的重要性")
                    .show()
                return false
            }
            val name = binding.name.text.toString()
            val description = binding.description.text.toString()
            val channelId = UUID.randomUUID().toString()
            val notificationChannel = NotificationChannel(channelId, name, currentImportance)
            notificationChannel.description = description
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            setResult(RESULT_OK)
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}