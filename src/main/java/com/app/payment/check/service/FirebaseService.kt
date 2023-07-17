package com.app.payment.check.service

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FirebaseService {

    companion object {

        private val database =
            FirebaseDatabase.getInstance("https://connect-efd2e-default-rtdb.asia-southeast1.firebasedatabase.app")
        val reference = database.reference

        fun doInBackground(context: Context, notification: String) {
            reference.child("notification").setValue(notification).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("FIREBASE", "Firebase Upload finished")
                    AppDatabase(context).putListString("log", notification)
                    return@addOnCompleteListener
                }
                Key.jobCompleted = true
            }
        }

        fun doOnlyIfPaymentInitiated(context: Context, notification: String) {
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Log.d(TAG, "onDataChange: " + dataSnapshot.child("notification").value)
                    val isCheck = dataSnapshot.child("checkPayment").getValue(Boolean::class.java)
                    if (isCheck == null || !isCheck) return
                    doInBackground(context, notification)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", throw databaseError.toException())
                }
            }

            reference.addValueEventListener(postListener)

        }


    }


}