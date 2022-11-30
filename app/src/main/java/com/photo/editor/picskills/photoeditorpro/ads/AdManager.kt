package com.photo.editor.picskills.photoeditorpro.ads

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.photo.editor.picskills.photoeditorpro.BuildConfig
import com.photo.editor.picskills.photoeditorpro.utils.FilterApplication

object AdManager {

    //Note! this id must b change not a Sample id interstitial Ads
    private var interstitialAdsSampleId: String = "ca-app-pub-3940256099942544/1033173712"
    private var interstitialAdsId1: String = "ca-app-pub-3043189731871157/5114547184"
    private var interstitialAdsId2: String = "ca-app-pub-3043189731871157/2488383848"

    private var mInterstitialAd: InterstitialAd? = null
    private val adRequest: AdRequest = AdRequest.Builder().build()
    private var callBackInterstitial: CallBackInterstitial? = null

    var interstitialStatusId: Boolean = false

    @JvmStatic
    fun loadInterstitialAd() {

        if (mInterstitialAd == null) {

            InterstitialAd.load(
                FilterApplication.context,
                getInterstitialID(),
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("loadInterstitialAd", adError.message)
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("loadInterstitialAd", "onAdLoaded")
                        mInterstitialAd = interstitialAd
                    }
                })

        } else {
            Log.d("loadInterstitialAd", "InterstitialAd is already loaded.")
        }

    }

    private fun getInterstitialID(): String {
        return if (BuildConfig.DEBUG) {
            interstitialAdsSampleId
        } else {
            if (interstitialStatusId) {
                interstitialStatusId = false
                return interstitialAdsId1
            } else {
                interstitialStatusId = true
                return interstitialAdsId2
            }

        }
    }

    @JvmStatic
    fun isInterstitialLoaded(): Boolean {
        return if (mInterstitialAd == null) {
            loadInterstitialAd()
            false
        } else {
            true
        }
    }

    @JvmStatic
    fun showInterstitial(activity: Activity, callBack: CallBackInterstitial) {
        if (mInterstitialAd != null) {
            callBackInterstitial = callBack
            mInterstitialAd?.show(activity)
            callBackInterstitialAd()
        } else {
            Log.d("showInterstitial", "The interstitial ad  ready yet.")
            loadInterstitialAd()
        }
    }

    private fun callBackInterstitialAd() {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                callBackInterstitial?.interstitialDismissedFullScreenContent()
                Log.d("callBackInterstitialAd", "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                callBackInterstitial?.interstitialFailedToShowFullScreenContent(adError)
                Log.d("callBackInterstitialAd", "Ad failed to show.")

            }

            override fun onAdShowedFullScreenContent() {
                Log.d("callBackInterstitialAd", "Ad showed fullscreen content.")
                callBackInterstitial?.interstitialShowedFullScreenContent()
                mInterstitialAd = null
                loadInterstitialAd()
            }
        }
    }

    interface CallBackInterstitial {
        fun interstitialDismissedFullScreenContent()
        fun interstitialFailedToShowFullScreenContent(adError: AdError?)
        fun interstitialShowedFullScreenContent()
    }

}