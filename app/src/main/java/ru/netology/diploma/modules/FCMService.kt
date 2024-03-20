package ru.netology.diploma.modules

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.auth.AppAuth
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val channelId = "server"

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
        // регистирируем канал, где уведомление будет отображаться:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name =
                getString(R.string.channel_remote_name)                     // название канала
            val descriptionText = getString(R.string.channel_remote_description)  // описание канала
            val importance = NotificationManager.IMPORTANCE_DEFAULT     //важность по умолчанию
            val channel = NotificationChannel(channelId, name, importance).apply {   //создаем канал
                description = descriptionText
            }
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager  //отправляем канал на регистрацию в систему
            manager.createNotificationChannel(channel)
            // и у приложения появляется свой канал для отправки уведомлений
        }
    }
}





