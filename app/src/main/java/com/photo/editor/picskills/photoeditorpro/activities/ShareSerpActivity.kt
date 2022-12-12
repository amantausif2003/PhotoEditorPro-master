package com.photo.editor.picskills.photoeditorpro.activities

import android.os.Bundle
import com.photo.editor.picskills.photoeditorpro.R
import com.photo.editor.picskills.photoeditorpro.utils.support.MyExceptionHandlerPix
import android.content.Intent
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import android.view.animation.AnimationUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.photo.editor.picskills.photoeditorpro.custom.MyBounceInterpolator
import com.photo.editor.picskills.photoeditorpro.databinding.ActivityShareBinding
import com.photo.editor.picskills.photoeditorpro.utils.Constants
import com.photo.editor.picskills.photoeditorpro.utils.UtilsPak
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass
import java.lang.Exception

class ShareSerpActivity : ParentActivity() {

    private var mImgUri: Uri? = null

    private lateinit var mainBinding:ActivityShareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandlerPix(this@ShareSerpActivity))

        bannerAds()

        init()

        mainBinding.ivHome.setOnClickListener {
            val finishIntent = Intent(this@ShareSerpActivity, MainActivity::class.java)
            finishIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(finishIntent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
        }
        mainBinding.ivBack.setOnClickListener { onBackPressed() }
        mainBinding.ivShare.setOnClickListener { shareOnOthersApp() }
    }

    /**************************************Banner Ads *********************************/
    private fun bannerAds() {
        mainBinding.adLayout.visibility = View.VISIBLE
        mainBinding.adLayout.post { loadBanner() }
    }

    private fun loadBanner() {
        Log.e("myTag", "BannerAds")
        val adView = AdView(this)
        adView.adUnitId = UtilsPak.getBannerID()
        val adSize = adSize
        adView.setAdSize(adSize)
        mainBinding.adLayout.addView(adView)
        val adRequest = AdRequest.Builder().build()
        // Start loading the ad in the background.
        try {
            adView.loadAd(adRequest)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    // Determine the screen width (less decorations) to use for the ad width.
    @Suppress("DEPRECATION")
    private val adSize: AdSize
        get() {
            // Determine the screen width (less decorations) to use for the ad width.
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val density = outMetrics.density
            var adWidthPixels = mainBinding.adLayout.width.toFloat()
            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    private fun init() {

        mImgUri = Uri.parse(intent.getStringExtra(Constants.KEY_URI_IMAGE))

        Glide.with(this@ShareSerpActivity)
            .asBitmap()
            .load(mImgUri)
            .into(mainBinding.ivCreate)
        val myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val interpolator = MyBounceInterpolator(0.2, 20.0)
        myAnim.interpolator = interpolator
        mainBinding.ivCreate.startAnimation(myAnim)
    }

    private fun shareOnOthersApp() {
        SupportedClass.shareWithOther(
            this@ShareSerpActivity,
            getString(R.string.txt_app_share_info),
            mImgUri
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }
}