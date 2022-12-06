package com.jitendract.jitdemo

import android.app.Application
import android.os.Bundle
import android.app.NotificationManager
import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat

class ProgressTimer : Application() {


    var nb: NotificationCompat.Builder? = null
    var progressMax =100
    fun createT(context: Context, extras: Bundle) {
        setVariable(context, extras)
        //        notifCreate();
    }

    private fun setVariable(context: Context, extras: Bundle) {
        Log.d("timer extras", extras.toString())
        val title = extras.getString("pt_title")
        val msg = extras.getString("pt_msg")
        val bg = extras.getString("pt_bg")
        val dl = extras.getString("pt_dl1")
        val threshold = extras.getString("pt_timer_threshold")
        val channelid = extras.getString("wzrk_cid")

        val progressIncr = threshold?.let { progressCounter(it) }
        nb = NotificationCompat.Builder(context, channelid!!)
        nb!!.setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.smicon)
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
}