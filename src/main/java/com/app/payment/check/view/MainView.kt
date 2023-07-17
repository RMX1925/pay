package com.app.payment.check.view

import android.app.Notification.Action
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.Image
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Device
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.app.payment.check.CircularProgressBar
import com.app.payment.check.R
import com.app.payment.check.service.FirebaseService
import com.app.payment.check.ui.theme.ColorText
import com.app.payment.check.ui.theme.PaymentTheme
import com.app.payment.check.ui.theme.Shapes
import com.app.payment.check.ui.theme.Success
import com.app.payment.check.ui.theme.proxima
import com.app.payment.check.ui.theme.roundedCornerShape
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.newSingleThreadContext
import org.intellij.lang.annotations.Language
import org.w3c.dom.Text
import java.util.Locale


@Composable
fun MainView(){

    var visibleSuccess by remember { mutableStateOf(false) }
    var receivedPaymentInfo by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

//    val postListener = object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            val isCheck = dataSnapshot.child("checkPayment").getValue(Boolean::class.java)
//            if(isCheck == null || !isCheck) return
//            val value = dataSnapshot.child("notification").getValue(String::class.java)
//            if (!value.isNullOrEmpty()){
//                if (value != receivedPaymentInfo){
//                    receivedPaymentInfo = value.split(":").first()
//                    FirebaseService.reference.child("notification").setValue("").addOnCompleteListener {
//
//                    }
//                    FirebaseService.reference.child("checkPayment").setValue(false).addOnCompleteListener {
//                        if(it.isSuccessful) {
//                            isLoading = false
//                            visibleSuccess = true
//                        }
//                    }
//                }
//
//            }
//        }
//
//        override fun onCancelled(databaseError: DatabaseError) {
//            // Getting Post failed, log a message
//            Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
//        }
//    }
//
//    FirebaseService.reference.addValueEventListener(postListener)


    PaymentTheme() {

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

            Spacer(modifier = Modifier.weight(weight = 1F))

            Text(text = stringResource(id = R.string.store_name), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, color = ColorText, fontFamily = proxima)

            Text(text = stringResource(id = R.string.scan_help_text), style = MaterialTheme.typography.bodyLarge, color = Color.Gray, fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(top = 18.dp),
                fontFamily = proxima

                )

            Spacer(modifier = Modifier.weight(weight = 1F))

            Image(painter = painterResource(id = R.drawable.qr_code), contentDescription = "qr code given to scan and pay",
                    contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size = 220.dp)
                    .clip(shape = Shapes.large)
                    .shadow(
                        elevation = 10.dp,
                        shape = Shapes.large,
                        spotColor = Color.Blue,
                        ambientColor = Color.Green,
                        clip = true
                    )
                )

            TextButton(
                enabled = !isLoading,
                onClick = {
//                    FirebaseService.reference.child("checkPayment").setValue(true).addOnCompleteListener {
//                        if (it.isSuccessful) isLoading = true
//                    }
//                    visibleSuccess = false

            }) {
                Text(text = "Check Payment", color = ColorText, fontFamily = proxima, textAlign = TextAlign.Center)
            }


            if(isLoading) ActionIndicator(iconElement = {
                CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = Color.DarkGray
            ) }, text = "Checking Payment", color = Color.Gray)

            Spacer(modifier = Modifier.height(height = 10.dp))

            if(visibleSuccess && !isLoading) ActionIndicator(iconElement = {
                Image(painter = painterResource(id = R.drawable.baseline_check_24), contentDescription = "Tick mark icon",
                    colorFilter = ColorFilter.tint(Success)

                    ) }, text = "Confirmed", )

            Spacer(modifier = Modifier.height(height = 10.dp))

            if(visibleSuccess || receivedPaymentInfo.isNotEmpty() && !isLoading ) Text(text = receivedPaymentInfo, style = MaterialTheme.typography.bodyMedium, color = Color.Black, textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(color = Color.LightGray.copy(alpha = 0.3F), shape = Shapes.medium)
                    .padding(horizontal = 60.dp, vertical = 14.dp),
                fontFamily = proxima
            )

            Spacer(modifier = Modifier.weight(weight = 1F))

            Image(painter = painterResource(id = R.drawable.outline_info_24), contentDescription = "Info icon")
            Text(text = stringResource(id = R.string.info_text), style = MaterialTheme.typography.bodySmall, color = Color.Gray, textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 60.dp, vertical = 14.dp),
                fontFamily = proxima
            )


            Spacer(modifier = Modifier.weight(weight = 0.5F))

        }
    }
}

fun textToSpeech(text: String, context: Context){
    val speech = TextToSpeech(context) {
        if (it != TextToSpeech.ERROR) {

        }
    }

    speech.language = Locale.ENGLISH
}

@Composable
fun ActionIndicator(iconElement: @Composable () -> Unit, text: String, color: Color = Success){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        content = {

            iconElement.invoke()

            Text(text = text, color = color,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                fontFamily = proxima,
                modifier = Modifier
                    .padding(all = 8.dp)
            )
        },
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.15F),
                shape = roundedCornerShape
            )
            .padding(horizontal = 20.dp, vertical = 2.dp)
    )
}
