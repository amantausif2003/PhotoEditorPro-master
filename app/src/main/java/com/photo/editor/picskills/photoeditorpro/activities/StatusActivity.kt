package com.photo.editor.picskills.photoeditorpro.activities

import com.photo.editor.picskills.photoeditorpro.activities.MainActivity.Companion.activity
import androidx.appcompat.app.AppCompatActivity
import jp.shts.android.storiesprogressview.StoriesProgressView.StoriesListener
import android.view.View.OnTouchListener
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import com.photo.editor.picskills.photoeditorpro.R
import androidx.core.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.Window
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.photo.editor.picskills.photoeditorpro.databinding.ActivityStatusBinding
import com.photo.editor.picskills.photoeditorpro.utils.UtilsPak
import java.lang.Exception

class StatusActivity : AppCompatActivity(), StoriesListener, OnTouchListener, View.OnClickListener {

    private var pressTime = 0L
    var limit = 500L
    lateinit var mainBinding: ActivityStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        mainBinding = ActivityStatusBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        bannerAds()

        mainBinding.tryNow.setOnClickListener(this)
        mainBinding.arrowTop.setOnClickListener(this)
        setModelStory()
        mainBinding.stories.setStoriesCount(1) // <- set stories
        mainBinding.stories.setStoryDuration(10000) // <- set a story duration  1200L
        mainBinding.stories.setStoriesListener(this) // <- set listener
        mainBinding.stories.startStories() // <- start progress
        mainBinding.statusImage.setOnTouchListener(this)
        mainBinding.arrowTop.setOnClickListener(this)
    }

    private fun setModelStory() {
        if (MainActivity.isModelSelected == 0) {
            mainBinding.statusImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.wings_status
                )
            )
        }
        if (MainActivity.isModelSelected == 1) {
            mainBinding.statusImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.spiral_status
                )
            )
        }
        if (MainActivity.isModelSelected == 2) {
            mainBinding.statusImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.blur_status
                )
            )
        }
        if (MainActivity.isModelSelected == 3) {
            mainBinding.statusImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.b_and_w_status
                )
            )
        }
        if (MainActivity.isModelSelected == 4) {
            mainBinding.statusImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.filter_status
                )
            )
        }
        if (MainActivity.isModelSelected == 5) {
            mainBinding.statusImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.gradient_status
                )
            )
        }
    }

    override fun onNext() {
        Log.i("onNext", "next")
    }

    override fun onPrev() {
        Log.i("onPrev", "previous")
    }

    override fun onComplete() {
        finish()
    }

    override fun onDestroy() {
        // Very important !
        mainBinding.stories.destroy()
        super.onDestroy()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v.id == R.id.status_image) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    mainBinding.stories.pause()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    mainBinding.stories.resume()
                    return limit < now - pressTime
                }
            }
        }
        return false
    }

    override fun onClick(v: View) {
        if (v.id == R.id.try_now) {
            itemClick()
        }
        if (v.id == R.id.arrow_top) {
            itemClick()
        }
    }

    private fun itemClick() {
        if (MainActivity.isModelSelected == 0) {
            finish()
            activity!!.showIdOrPermission()
        }
        if (MainActivity.isModelSelected == 1) {
            finish()
            activity!!.showIdOrPermission()
        }
        if (MainActivity.isModelSelected == 2) {
            finish()
            activity!!.showIdOrPermission()
        }
        if (MainActivity.isModelSelected == 3) {
            finish()
            activity!!.showIdOrPermission()
        }
        if (MainActivity.isModelSelected == 4) {
            finish()
            activity!!.showIdOrPermission()
        }
        if (MainActivity.isModelSelected == 5) {
            finish()
            activity!!.showIdOrPermission()
        }
    }

    /**************************************Banner Ads *********************************/
    fun bannerAds() {
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
}