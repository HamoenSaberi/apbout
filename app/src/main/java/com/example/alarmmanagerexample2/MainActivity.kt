package com.example.alarmmanagerexample2

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*


//make REQUEST_CODE for camera application (arbitrary number 42 used. Meaning of life.)
private const val REQUEST_CODE = 42

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
        val plecoBtn: Button = findViewById(R.id.btn_pleco)
        val cameraBtn: Button = findViewById(R.id.btn_camera) //camera button
        val timerEdt: EditText = findViewById(R.id.edt_timer)

        // the event listener that allows us to click and go to pleco immediatly.
        plecoBtn.setOnClickListener {
            val uri = Uri.parse("plecoapi://x-callback-url/s?q=")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        //Camera: when button is pressed it should open up a camera app
        cameraBtn.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "unable to open Camera", Toast.LENGTH_SHORT).show()
            }
        }

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
            Log.d("MainActivity", "Canceled at: " + Date().toString())
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
                .setContentTitle("It's time")
                .setContentText("Thou must suffer")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(123,builder.build())

        }

    }

    //Camera: dealing with the picture that is captured


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val imageView: ImageView = findViewById(R.id.imageView) //camera preview element grab
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val takenImage = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(takenImage)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}