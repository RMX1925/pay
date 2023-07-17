package com.app.payment.check.ui

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import androidx.compose.ui.text.toLowerCase
import androidx.core.app.NotificationCompat
import com.app.payment.check.MainActivity
import com.app.payment.check.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class BackgroundServ : Service() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var notificationManager: NotificationManager

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "my_channel_id"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mDatabase = FirebaseDatabase.getInstance("https://connect-efd2e-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val checkPayment = dataSnapshot.child("checkPayment").getValue(Boolean::class.java) ?: false
                if (checkPayment) {
                    readLatestSMS(dataSnapshot)
                    dataSnapshot.child("checkPayment").ref.setValue(true)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        }

        mDatabase.addValueEventListener(valueEventListener)

        startForeground(NOTIFICATION_ID, buildNotification())

        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("My App")
            .setContentText("Running in background")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        return notification
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "My App Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun readLatestSMS(snapshot : DataSnapshot) {
        val cursor = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            null,
            null,
            null,
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                var steps = 10
                do {
                    val smsBody = it.getString(it.getColumnIndexOrThrow("body")) ?: "No Payment"
                    steps--
                    print(smsBody)
                    if(smsBody.lowercase(Locale.ENGLISH).contains("credited")){
                        snapshot.child("paidAmount").ref.setValue(smsBody)
                        break
                    }
                } while (it.moveToNext() && steps > 0)
                steps = 10
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
