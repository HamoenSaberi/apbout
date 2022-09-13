package com.example.alarmmanagerexample2

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*


class MainActivity : AppCompatActivity() {

    // create variable that can tell our program about other parts of the program later
    lateinit var context : Context
    //create the alarm manager variable which we have imported to help us
    lateinit var alarmManager: AlarmManager

    // standard code which starts our main activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //create notification channel
        createNotificationChannel()

        //setting the context variable to the context of this scope(i think)
        context = this
        // setting alarManager variable to actual imported AlarmManager so we can use it.
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // getting the variables from view.
        val createBtn: Button = findViewById(R.id.btn_create)
        val updateBtn: Button = findViewById(R.id.btn_update)
        val cancelBtn: Button = findViewById(R.id.btn_cancel)
        val timerEdt: EditText = findViewById(R.id.edt_timer)


        // getting and using the create alarm button
        createBtn.setOnClickListener {
            val second = timerEdt.text.toString().toInt() * 1000 // take away the 1000 if you want it to be seconds
            val intent = Intent(context, Receiver::class.java )
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            Log.d("MainAcitivty", "Created at: " + Date().toString())
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()  + second, pendingIntent)
        }
        // getting and using the update alarm button
        updateBtn.setOnClickListener {
            val second = timerEdt.text.toString().toInt() * 1000 // take away the 1000 if you want it to be seconds
            val intent = Intent(context, Receiver::class.java )
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            Log.d("MainAcitivty", "Updated at: " + Date().toString())
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + second, pendingIntent)
        }
        cancelBtn.setOnClickListener {
            val intent = Intent(context, Receiver::class.java )
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            Log.d("MainAcitivty", "Canceled at: " + Date().toString())
            alarmManager.cancel(pendingIntent)
        }
    }

    //creating notification method for creating a channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name : CharSequence = "SelfMadeReminderChannel"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("SelfMadeChannel", name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(channel)
        }
    }

    //creating a broadcast receiver
    class Receiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("mainActivity","Receiver : " + Date().toString())

            //building our notification
            val i = Intent(context, MainActivity::class.java)
            intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(context!!,0, i,0)

            val builder = NotificationCompat.Builder(context!!,"SelfMadeChannel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Yeah")
                .setContentText("yeah yeah yeah")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(123,builder.build())

        }

    }

}