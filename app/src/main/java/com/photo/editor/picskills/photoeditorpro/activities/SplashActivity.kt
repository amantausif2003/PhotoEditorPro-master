package com.photo.editor.picskills.photoeditorpro.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.firebase.FirebaseApp
import com.photo.editor.picskills.photoeditorpro.BuildConfig
import com.photo.editor.picskills.photoeditorpro.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (!BuildConfig.DEBUG) {
            FirebaseApp.initializeApp(this@SplashActivity)
        }

        MobileAds.initialize(this@SplashActivity, object : OnInitializationCompleteListener{
            override fun onInitializationComplete(p0: InitializationStatus) {

            }
        })

        handler.postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 2000)

    }
}