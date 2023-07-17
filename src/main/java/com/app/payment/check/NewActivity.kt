package com.app.payment.check

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.payment.check.ui.theme.PaymentTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.temporal.TemporalAmount

class ReceiverActivity : ComponentActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var amount : String
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseRef = FirebaseDatabase.getInstance("https://connect-efd2e-default-rtdb.asia-southeast1.firebasedatabase.app").reference

        setContent {

            SinglePermission()

            var paidAmount by remember { mutableStateOf("") }
            amount = paidAmount
            LaunchedEffect(Unit) {
                databaseRef.child("paidAmount").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val newPaidAmount = dataSnapshot.value ?: "0"
                        if (newPaidAmount != paidAmount) {
                            paidAmount = newPaidAmount as String
                            amount = paidAmount
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle errors
                    }
                })
            }

            PaymentTheme {
                // on below line we are specifying background color for our application
                Surface(
                    // on below line we are specifying modifier and color for our app
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background,

                ) {
                    ChooseUserTypes(context = this, paidAmount, onClickItem = {
                        this.finish()
                    })
                    // on the below line we are specifying
                    // the theme as the scaffold.
                }
            }
        }
    }
}


@Composable
fun ChooseUserType(context: Context) {
    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.safeContentPadding()) {
        CircularButton(text = "Broadcaster", onClickItem = {
            val intent = Intent(context, ReceiverActivity::class.java)
            context.startActivity(intent)
        })
        Text(text = "Choose user type to continue", style = TextStyle(fontSize = 20.sp))
        CircularButton(text = "Receiver", onClickItem = {

        })
    }

}

@Composable
fun ChooseUserTypes(context: Context, paidAmount: String, onClickItem: () -> Unit) {
    val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)
    val isBroadcast = prefs.getBoolean("isBroadcast", false)
    if (isFirstLaunch) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularButton(text = "Broadcaster", onClickItem = {
                with(prefs.edit()) {
                    putBoolean("isFirstLaunch", false)
                    putBoolean("isBroadcast", true)
                    apply()
                }
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                onClickItem()

                // Save a flag indicating that the user has chosen their type



            })
            Text(
                text = "Choose user type to continue",
                style = TextStyle(fontSize = 20.sp)
            )
            CircularButton(text = "Receiver", onClickItem = {

                // Save a flag indicating that the user has chosen their type
                with(prefs.edit()) {
                    this.putBoolean("isFirstLaunch", false)
                    this.putBoolean("isBroadcast", false)
                    apply()
                }
            })
        }
    } else {
        // Navigate to the appropriate screen based on the user's choice
        if (prefs.getBoolean("isBroadcast", false)) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            onClickItem()
        } else {
            var showPayment by remember {
                mutableStateOf(false)
            }
            PulseLoading(headingText = if(paidAmount.isNotEmpty() && paidAmount.contains("is credited")) "Payment Received" else "Confirming Payment", helperText = paidAmount, checkPayment = {
                if(showPayment){}
            })

        }

    }
}


@Composable
fun CircularButton(radius: Dp = 90.dp, text: String, onClickItem: () -> Unit){
    Box(modifier = Modifier
        .wrapContentSize()
        .clickable { onClickItem() }, contentAlignment = Alignment.Center){
        Canvas(modifier = Modifier.wrapContentSize()){
            drawCircle(
                color = Color.Blue.copy(alpha = 0.3f),
                radius = radius.toPx(),
            )
        }

        Text(text = text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.Blue.copy(alpha = 0.8f)
            ),
            softWrap = false,
        )
    }
}


@Composable
fun PulseLoading(
    durationMillis:Int = 1000,
    maxPulseSize:Float = 300f,
    minPulseSize:Float = 50f,
    pulseColor:Color = Color(234,240,246),
    centreColor:Color =  Color(66,133,244),
    headingText: String = "Confirming Payment",
    helperText: String = "Please wait",
    showHelper: Boolean = true,
    checkPayment: () -> Unit
){
    val infiniteTransition = rememberInfiniteTransition()
    val size by infiniteTransition.animateFloat(
        initialValue = minPulseSize,
        targetValue = maxPulseSize,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    Box(contentAlignment = Alignment.Center,modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .size(size.dp)
                .align(Alignment.Center)
                .alpha(alpha),
            backgroundColor = pulseColor,
            elevation = 0.dp
        ){

        }
        Card(modifier = Modifier
            .size(minPulseSize.dp)
            .align(Alignment.Center),
            shape = CircleShape,
            backgroundColor = centreColor){

            if(headingText == "Payment Received") Icon(Icons.Filled.Check, contentDescription = "Check", modifier = Modifier.size(18.dp), tint = Color.White)
        }

        Text(
            text = headingText,
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 150.dp)
        )

        Text(
            text = helperText,
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 200.dp)
        )



    }
}

@Preview(showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPulse() {
    PaymentTheme() {
        PulseLoading(checkPayment = {})
    }

}
