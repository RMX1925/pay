package com.app.payment.check.ui

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FireSMSService : Service() {

    private lateinit var mDatabase: DatabaseReference

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mDatabase = FirebaseDatabase.getInstance("https://connect-efd2e-default-rtdb.asia-southeast1.firebasedatabase.app").reference

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val checkPayment = dataSnapshot.child("checkPayment").getValue(Boolean::class.java) ?: false
                if (checkPayment) {
                    readLatestSMS(dataSnapshot)
//                    mDatabase.setValue(false)
                    dataSnapshot.child("checkPayment").ref.setValue(true)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        }

        mDatabase.addValueEventListener(valueEventListener)

        return START_STICKY
    }

    private fun readLatestSMS(snapshot: DataSnapshot) {
        val SMS_FILTER = "address='AB-AIRBNK' AND body='is credited with Rs.'"
        val sortOrder = "date DESC"
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
                    if(smsBody.contains("is credited")){
                        snapshot.child("paidAmount").ref.setValue(smsBody)
                        break
                    }
                } while (it.moveToNext() && steps > 0)
                // Do something with the SMS body
                steps = 10
            }

        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
