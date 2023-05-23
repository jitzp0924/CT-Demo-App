package com.jitendract.jitdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CleverTapAPI

class inbox_app : AppCompatActivity() , CTInboxListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox_app)
        val cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(this@inbox_app)

        cleverTapDefaultInstance?.apply {
            ctNotificationInboxListener = this@inbox_app
            initializeInbox()
        }
    }

    override fun inboxDidInitialize() {
        TODO("Not yet implemented")
    }

    override fun inboxMessagesDidUpdate() {
        TODO("Not yet implemented")
    }
}