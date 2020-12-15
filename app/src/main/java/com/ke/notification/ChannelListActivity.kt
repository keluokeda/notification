package com.ke.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ke.notification.databinding.ActivityChannelListBinding

@RequiresApi(api = Build.VERSION_CODES.O)
class ChannelListActivity : AppCompatActivity() {
    private val adapter =
        object : BaseQuickAdapter<NotificationChannel, BaseViewHolder>(R.layout.item_channel) {
            override fun convert(holder: BaseViewHolder, item: NotificationChannel) {
                holder.setText(R.id.name, item.name)
                    .setText(R.id.description, item.description)
                    .setText(R.id.importance, importanceToString(item.importance))
            }

        }

    private fun importanceToString(
        importance: Int
    ) = when (importance) {
        NotificationManager.IMPORTANCE_NONE -> "无"
        NotificationManager.IMPORTANCE_MIN -> "低"
        NotificationManager.IMPORTANCE_LOW -> "中"
        NotificationManager.IMPORTANCE_DEFAULT -> "高"
        NotificationManager.IMPORTANCE_HIGH -> "紧急"
        else -> "未定义"
    }

    private lateinit var binding: ActivityChannelListBinding
    private lateinit var notificationManager: NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChannelListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        setContentView(R.layout.activity_channel_list)
        title = "渠道列表"

        adapter.setOnItemClickListener { _, _, position ->
            val channel = adapter.getItem(position)
            val intent = Intent()
            intent.putExtra("channel", channel)
            setResult(RESULT_OK, intent)
            finish()
        }
        adapter.setOnItemLongClickListener { _, _, position ->
            val channel = adapter.getItem(position)
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否删除名称为 ${channel.name} 的通知渠道")
                .setPositiveButton("删除") { _, _ ->
                    notificationManager.deleteNotificationChannel(channel.id)
                    adapter.setNewInstance(notificationManager.notificationChannels)
                }.setNegativeButton("取消", null)
                .show()
            return@setOnItemLongClickListener true
        }
        binding.recyclerView.apply {
            adapter = this@ChannelListActivity.adapter
            addItemDecoration(
                DividerItemDecoration(
                    this@ChannelListActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        adapter.setNewInstance(notificationManager.notificationChannels)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1, 1, "添加").setIcon(R.drawable.baseline_add_white_24dp)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            adapter.setNewInstance(notificationManager.notificationChannels)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            startActivityForResult(Intent(this, CreateNotificationChannelActivity::class.java), 101)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}