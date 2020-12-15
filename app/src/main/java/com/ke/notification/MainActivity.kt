package com.ke.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.view.isVisible
import com.ke.notification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private var currentPriority = NotificationCompat.PRIORITY_MAX
    private var currentPendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT
    private var currentChannel: NotificationChannel? = null
    private val priorityList = arrayOf(
        NotificationCompat.PRIORITY_DEFAULT,
        NotificationCompat.PRIORITY_LOW,
        NotificationCompat.PRIORITY_MIN,
        NotificationCompat.PRIORITY_HIGH,
        NotificationCompat.PRIORITY_MAX
    )
    private val pendingIntentFlagList = arrayOf(
        PendingIntent.FLAG_ONE_SHOT,
        PendingIntent.FLAG_NO_CREATE,
        PendingIntent.FLAG_CANCEL_CURRENT,
        PendingIntent.FLAG_UPDATE_CURRENT,
    )

    private val pendingIntentFlagNameList = arrayOf(
        "FLAG_ONE_SHOT", "FLAG_NO_CREATE", "FLAG_CANCEL_CURRENT", "FLAG_UPDATE_CURRENT"
    )

    private val priorityNameList = priorityList.map { convertPriorityToString(it) }.toTypedArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        "MainActivity onCreate $savedInstanceState".log()

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        title = "通知"
        activityMainBinding.apply {
            channel.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            channel.setOnClickListener {
                toChannelListActivity()
            }
            updateChannelText()
            updatePendingIntentFlagText()
            updatePriorityText()
            priority.setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("设置优先级")
                    .setSingleChoiceItems(
                        priorityNameList, priorityList.indexOf(currentPriority)
                    ) { dialog, which ->
                        currentPriority = priorityList[which]
                        updatePriorityText()
                        dialog.dismiss()
                    }.show()
            }
            pendingIntentFlag.setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("设置PendingIntentFlag")
                    .setSingleChoiceItems(
                        pendingIntentFlagNameList,
                        pendingIntentFlagList.indexOf(currentPendingIntentFlag)
                    ) { dialog, which ->
                        currentPendingIntentFlag = pendingIntentFlagList[which]
//                        updatePriorityText()
                        updatePendingIntentFlagText()
                        dialog.dismiss()
                    }.show()
            }

            show1.setOnClickListener {
                show1()
            }
            show2.setOnClickListener {
                show2()
            }

            show3.setOnClickListener {
                show3()
            }
            show4.setOnClickListener {
                show4()
            }
        }

        notificationManagerCompat = NotificationManagerCompat.from(this)
    }

    @MainThread
    override fun onDestroy() {
        super.onDestroy()
        "MainActivity onDestroy".log()

    }

    private fun toChannelListActivity() {
        startActivityForResult(
            Intent(this@MainActivity, ChannelListActivity::class.java),
            1001
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            currentChannel = data.getParcelableExtra("channel")
            updateChannelText()
        }
    }

    private fun show1() {
        val notificationId = 100
        val build = buildNotificationBuild() ?: return
        build.setContentIntent(
            buildNotificationPendingIntent(notificationId)
        )
        notificationManagerCompat.notify(notificationId, build.build())

    }

    private fun show2() {
        val notificationId = 101
        val build = buildNotificationBuild() ?: return
        build.addAction(
            R.drawable.baseline_add_white_24dp,
            "添加",
            buildNotificationPendingIntent(notificationId)
        )
        notificationManagerCompat.notify(notificationId, build.build())
    }

    private fun show3() {
        val notificationId = 103
        val build = buildNotificationBuild() ?: return

        val remoteInput = RemoteInput.Builder(NOTIFICATION_RESULT_KEY).setLabel("回复通知").build()
        val intent = Intent(this, NotificationReplyReceiver::class.java).apply {
            putExtra(NOTIFICATION_ID, notificationId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(NOTIFICATION_CHANNEL_ID, currentChannel!!.id)
            }
        }
        val pendingIntent =
            PendingIntent.getBroadcast(applicationContext, 0, intent, currentPendingIntentFlag)
        val action = NotificationCompat.Action.Builder(
            R.drawable.baseline_reply_black_24dp,
            "回复",
            pendingIntent
        ).addRemoteInput(remoteInput).build()
        build.addAction(action)

        notificationManagerCompat.notify(notificationId, build.build())
    }

    private fun show4() {
        val notificationId = 104
        val build = buildNotificationBuild() ?: return
        build.setSmallIcon(R.drawable.baseline_arrow_circle_down_black_24dp)
            .setContentTitle("下载图片")
            .setContentText("正在下载图片")
            .setProgress(0, 0, true)
        notificationManagerCompat.notify(notificationId, build.build())
    }

    private fun buildNotificationPendingIntent(notificationId: Int): PendingIntent {
        val extra = activityMainBinding.extra.text.toString()
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("content", extra)
            putExtra("notificationId", notificationId)

            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return PendingIntent.getActivity(this, 0, intent, currentPendingIntentFlag)
    }

    private fun buildNotificationBuild(): NotificationCompat.Builder? {
        if (currentChannel == null) {
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("请先选择通知渠道")
                .setPositiveButton("去选择") { _, _ ->
                    toChannelListActivity()
                }.setNegativeButton("取消", null)
                .show()
            return null
        }

//        val intent = Intent(this, DetailActivity::class.java).apply {
//            putExtra("content", "用户点击了通知上的操作按钮")
//            putExtra("notificationId", notificationId)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        }

//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//            TaskStackBuilder.create(this).run {
//                addNextIntentWithParentStack(intent)
//                getPendingIntent(1001, PendingIntent.FLAG_UPDATE_CURRENT)
//            }
        val title = activityMainBinding.title.text.toString()
        val content = activityMainBinding.content.text.toString()
        return NotificationCompat.Builder(
            this,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) currentChannel!!.id else ""
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(currentPriority)
            .setAutoCancel(activityMainBinding.autoCancel.isChecked)

    }

    @SuppressLint("SetTextI18n")
    private fun updatePriorityText() {
        activityMainBinding.priority.text = "当前优先级 = ${convertPriorityToString(currentPriority)}"
    }

    @SuppressLint("SetTextI18n")
    private fun updatePendingIntentFlagText() {
        activityMainBinding.pendingIntentFlag.text = "当前 Pending Intent Flag = ${
            pendingIntentFlagNameList[pendingIntentFlagList.indexOf(currentPendingIntentFlag)]
        }"
    }

    @SuppressLint("SetTextI18n")
    private fun updateChannelText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activityMainBinding.channel.text = "当前渠道 = ${currentChannel?.name}"
        }
    }

    private fun convertPriorityToString(priority: Int) = when (priority) {
        NotificationCompat.PRIORITY_DEFAULT -> "默认"
        NotificationCompat.PRIORITY_LOW -> "中"
        NotificationCompat.PRIORITY_MIN -> "低"
        NotificationCompat.PRIORITY_HIGH -> "高"
        NotificationCompat.PRIORITY_MAX -> "紧急"
        else -> "无定义"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0才有通知渠道
            menu.add(0, 0, 0, "添加渠道").setIcon(R.drawable.baseline_add_white_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        menu.add(0, 1, 0, "清除所有")

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 0) {
            startActivity(Intent(this, CreateNotificationChannelActivity::class.java))
            return true
        } else if (item.itemId == 1) {
            notificationManagerCompat.cancelAll()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val NOTIFICATION_RESULT_KEY = "NOTIFICATION_RESULT_KEY"
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    }
}