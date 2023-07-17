package com.app.payment.check

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import com.app.payment.check.service.NotificationService
import com.app.payment.check.ui.theme.PaymentTheme
import com.app.payment.check.view.MainView

class NewViewActivity : ComponentActivity() {
    var permissionGranted: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            askForNotificationAccessPermission()
            PaymentTheme {
                val serviceIntent = Intent(this, NotificationService::class.java)
                this.startService(serviceIntent)
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        permissionGranted = NotificationManagerCompat.getEnabledListenerPackages(applicationContext)
            .contains(applicationContext.packageName)

    }

    private fun askForNotificationAccessPermission() {
        permissionGranted = NotificationManagerCompat.getEnabledListenerPackages(applicationContext)
            .contains(applicationContext.packageName)
        if (!permissionGranted) {
            startActivity(
                Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
            )
            Toast.makeText(applicationContext, "Turn on the notification access", Toast.LENGTH_LONG)
                .show()
        }

    }
}