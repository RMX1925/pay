package com.app.payment.check

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.app.payment.check.ui.BackgroudReceiver
import com.app.payment.check.ui.BackgroundServ
import com.app.payment.check.ui.theme.PaymentTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {

    private lateinit var databaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseRef = FirebaseDatabase.getInstance("https://connect-efd2e-default-rtdb.asia-southeast1.firebasedatabase.app").reference

        setContent {
            var paidAmount by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                databaseRef.child("paidAmount").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val newPaidAmount = dataSnapshot.value ?: "0"
                        if (newPaidAmount != paidAmount) {
                            paidAmount = newPaidAmount as String
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle errors
                    }
                })
            }

            SinglePermission()

            val serviceIntent = Intent(this, BackgroudReceiver::class.java)
            this.startService(serviceIntent)

            PaymentTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeContentPadding(),
                    color = MaterialTheme.colors.background,
                ) {
                    Text(text = "Broadcaster is running")

                }
            }
        }
    }
}


@Composable
fun CircularProgressBar(number: Float, numberStyle: TextStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
),
                        size: Dp = 180.dp,
                        indicatorThickness: Dp = 28.dp,
                        animationDuration: Int = 1000,
                        animationDelay: Int = 0,
                        foregroundIndicatorColor: Color = Color(0xFF35898f),
                        backgroundIndicatorColor: Color = Color.LightGray.copy(alpha = 0.3f)
){
    var numberR by remember { mutableStateOf(0f) }
    val animateNumber = animateFloatAsState(targetValue = numberR, animationSpec = tween(durationMillis = animationDuration, delayMillis = animationDelay))
    LaunchedEffect(Unit) {
        numberR = number
    }

    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center){
        Canvas(modifier = Modifier.size(size)){
            drawCircle(
                color = foregroundIndicatorColor,
                radius = size.toPx(),
                style = Stroke(width = indicatorThickness.toPx())
            )
            var sweepAngle = (animateNumber.value / 100 ) * 360

            drawArc(
                color = foregroundIndicatorColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(indicatorThickness.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(text = (animateNumber.value).toInt().toString(),
            style = numberStyle
        )
    }
    Spacer(modifier = Modifier.height(32.dp))

//    ButtonProgressbar {
//        numberR = (1..100).random().toFloat()
//    }
}

@Composable
private fun ButtonProgressbar(backgroundColor: Color = Color(0xFF35898f),
onClickButton: () -> Unit){
    Button(
        onClick = {onClickButton()},
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor
        )
    ) {
        Text(text = "Animate with Random Number Value", color = Color.White, fontSize = 16.sp)
    }

}

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SinglePermission() {
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.READ_SMS)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionState.launchPermissionRequest()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    when {
        permissionState.hasPermission -> {
            Text(text = "Reading SMS permission is granted")
//            ReadSMS(context = context)
        }
        permissionState.shouldShowRationale -> {
            Column {
                Text(text = "Reading SMS permission is required by this app")
            }
        }
        !permissionState.hasPermission && !permissionState.shouldShowRationale -> {
            Text(text = "Permission fully denied. Go to settings to enable")
        }
    }
}

@Composable
fun ChooseUserTypeScreen(context : Context) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Choose user type", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CircularButton(
                text = "Broadcaster",
                onClick = { /* Do nothing */ }
            )
            CircularButton(
                text = "Receiver",
                onClick = {
                    // Switch to another activity
                    val intent = Intent(context, ReceiverActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun CircularButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        modifier = Modifier.size(72.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = Color.Blue,
                    shape = CircleShape
                )
                .fillMaxSize()
        ) {
            Text(text = text)
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LayoutPreview() {

    PaymentTheme() {
        ChooseUserType(LocalContext.current)
    }

}



