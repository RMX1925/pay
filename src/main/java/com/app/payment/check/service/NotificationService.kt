package com.app.payment.check.service

import android.app.Notification
import android.content.ContentValues.TAG
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log


class NotificationService : NotificationListenerService() {

    private var notification: String = ""
    lateinit var appDatabase: AppDatabase

    override fun onCreate() {
        super.onCreate()
        appDatabase = AppDatabase(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {

//        val packageNameString = "com.google.android.apps.nbu.paisa.user"
        val packageNameString = "com.google.android.apps.messaging"
//        if (packageNameString == "com.google.android.apps.nbu.paisa.user") {
            val allNotifications = this.activeNotifications.filter {
                return@filter it.packageName == packageNameString
            }.toList()
//
//            allNotifications.forEach {
////                Log.d(TAG, "onNotificationPosted: ${it.notification} ${it.notification.sortKey}")
////                print("onNotificationPosted: ${it.notification} ${it.notification.sortKey}")
//                Log.d(TAG, "onNotificationPosted: ${allNotifications.first()}")
//            }

            val extras = sbn.notification.extras
            val key = sbn.key
            if (extras != null) {
                val title = extras.get(Notification.EXTRA_TITLE)
                if (title != null && title != "Chat heads active") {
                    val text = extras.get(Notification.EXTRA_TEXT)
                    notification = "$title : $text : $key"
                    Log.d(TAG, notification)
                }
            }
//        }
        Log.d(TAG, "onNotificationPosted: Notification Captured")
        if (notification.isNotEmpty()) {
//            FirebaseService.doOnlyIfPaymentInitiated(applicationContext, notification)
        }
    }
}