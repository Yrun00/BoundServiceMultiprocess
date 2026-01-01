package com.github.boundservicemultiprocess

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BoundSquareService: Service() {

    private val squareBinder = object: ISquareService.Stub(){
        override fun squareNumber(number: Long): Long {
            Log.d("BoundSquareService", "Received request: square($number)")
            return number * number
        }

    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("BoundSquareService", "Service bound")
        return squareBinder
    }
}