package com.github.boundservicemultiprocess

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.boundservicemultiprocess.ui.theme.BoundServiceMultiprocessTheme

class MainActivity : ComponentActivity() {

    private var iSquareService: ISquareService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iSquareService = ISquareService.Stub.asInterface(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            iSquareService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleUI(
                onButtonClick = {
                    number ->
                    iSquareService?.squareNumber(number) ?: 0
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        bindToService()
    }

    override fun onPause() {
        super.onPause()
        if (isBound) {
            unbindService(serviceConnection)
        }
    }

    private fun bindToService() {
        val intent = Intent(this, BoundSquareService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }


}

@Composable
fun SimpleUI(onButtonClick: (Long) -> Long) {
    var numberText by remember { mutableStateOf("") }
    var result by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = numberText,
            onValueChange = { numberText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                Log.d("BoundSquareService", "Button from activity pressed")
                result = onButtonClick(numberText.toLongOrNull() ?: 0)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Возвести в квадрат")
        }

        Text(
            text = "Результат: $result",
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}