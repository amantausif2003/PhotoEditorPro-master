package com.photo.editor.picskills.photoeditorpro.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Looper
import android.os.Bundle
import com.photo.editor.picskills.photoeditorpro.R
import com.google.android.gms.ads.MobileAds
import android.content.Intent
import android.os.Handler
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

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