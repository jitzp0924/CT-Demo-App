package com.jitendract.jitdemo

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.clevertap.android.sdk.CleverTapAPI
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.Executors

class ProgressTimer : Service() {
    var nb: NotificationCompat.Builder? = null
    var progressMax =100


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("Progress Service","background Process Start")
        startForegroundWithNotification()
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val fcmtoken = task.result
            Log.e("Progress Service","Token Generated : "+fcmtoken.toString())
        }
        val extras = intent.extras
        if (extras != null && extras.containsKey("looperTime")) {
            var looperTime = extras.getString("looperTime", 10.toString()).toInt()
            looperTimer(looperTime)
        }
        else if(extras != null && extras.containsKey("prog")){setVariable(applicationContext, extras)}
        return START_STICKY
    }

    private fun looperTimer(looperTime: Int) {
        Thread.sleep(looperTime.toLong())
        Log.e("Progress Service","Inside the looperTimer() after delay of "+looperTime.toString())
        val eventProps = hashMapOf(
            "Page" to "Background",
            "Date" to System.currentTimeMillis(),
            "delayTime" to looperTime
        )

        val ct: CleverTapAPI? = CleverTapAPI.getDefaultInstance(this)
//        val cleverTapUtils: CleverTapUtils? = CleverTapUtils.getInstance()
        ct?.pushEvent("Background",eventProps as HashMap<String, Any>)

    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setVariable(context: Context, extras: Bundle) {
        Log.d("timer extras", extras.toString())
        val title = extras.getString("pt_title")
        val msg = extras.getString("pt_msg")
        val bg = extras.getString("pt_bg")
        val dl = extras.getString("pt_dl1")
        val img = extras.getString("wzrk_bp")
        val executor = Executors.newSingleThreadExecutor()
        var image: Bitmap? = null
        executor.execute {
            try {
                val im = java.net.URL(img).openStream()
                image = BitmapFactory.decodeStream(im)
                Log.e("IMAGE TIMER2","Fectching Image")
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.e("IMAGE TIMER2","Not Able to Fectch Image")
            }
        }
        val threshold = extras.getString("pt_timer_threshold")
        val channelid = extras.getString("wzrk_cid")

        val progressIncr = threshold?.let { progressCounter(it) }
        nb = NotificationCompat.Builder(context, channelid!!)
        nb!!.setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.smicon1)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(image))
            .setProgress(100, 0, true)
        val n = nb!!.build()
        val nm = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(123, n)

        Thread {
            var progress = 0
            while (progress <= progressMax) {
                if (progressIncr != null) {
                    SystemClock.sleep(progressIncr)
                }

                //Use this to make it a Fixed-duration progress indicator notification
                progress +=1

                nb!!.setContentText(progress.toString()+"%")
                    .setProgress(progressMax, progress, false)

                nm.notify(123, nb!!.build())
            }

            nb!!.setContentText("Download complete")
                .setProgress(0, 0, false)
                .setOngoing(false)
            nm.notify(123, nb!!.build())
        }.start()
    }

    private fun progressCounter(threshold: String): Long {
        //Logic is Thread.sleep is Milliseconds (Converting threshold value to milliseconds ,i.e. multiply by 1000 and they divide by 100 to get sleep duration)
        var threshold = threshold.toLong()
        return threshold.times(10)

    }

    private fun startForegroundWithNotification() {
        val notification = NotificationCompat.Builder(this, "r2d2")
            .setContentTitle("Starting Progress Timer")
            .setContentText("Please wait...")
            .setSmallIcon(R.drawable.smicon1)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1,notification)
    }

}
