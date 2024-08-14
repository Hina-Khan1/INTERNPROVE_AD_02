package com.test_1


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Handle boot completed event here
            Toast.makeText(context, "Device boot completed!", Toast.LENGTH_SHORT).show()
            // Optionally, reschedule your notifications or alarms
        }
    }
}
