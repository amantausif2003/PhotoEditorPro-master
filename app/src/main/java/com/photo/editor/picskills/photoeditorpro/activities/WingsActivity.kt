package com.photo.editor.picskills.photoeditorpro.activities

import com.photo.editor.picskills.photoeditorpro.ads.AdManager.isInterstitialLoaded
import com.photo.editor.picskills.photoeditorpro.callback.MenuItemClickLister
import android.graphics.Bitmap
import android.view.animation.Animation
import com.photo.editor.picskills.photoeditorpro.adapter.SpiralEffectListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photo.editor.picskills.photoeditorpro.custom.StickerView.StickerView
import com.photo.editor.picskills.photoeditorpro.custom.StickerView.Sticker
import com.google.android.material.tabs.TabLayout
import com.photo.editor.picskills.photoeditorpro.R
import com.photo.editor.picskills.photoeditorpro.utils.support.MyExceptionHandlerPix
import com.photo.editor.picskills.photoeditorpro.utils.support.FastBlur
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.SeekBar.OnSeekBarChangeListener
import com.photo.editor.picskills.photoeditorpro.adapter.StickerAdapter
import com.photo.editor.picskills.photoeditorpro.custom.StickerView.DrawableSticker
import android.app.Dialog
import android.content.*
import android.graphics.Canvas
import android.net.Uri
import android.os.*
import android.view.LayoutInflater
import com.photo.editor.picskills.photoeditorpro.custom.CustomTextView
import com.photo.editor.picskills.photoeditorpro.custom.StickerView.StickerView.OnStickerOperationListener
import android.view.ViewGroup
import com.photo.editor.picskills.photoeditorpro.custom.DHANVINE_MultiTouchListener
import com.photo.editor.picskills.photoeditorpro.crop_img.newCrop.MLCropAsyncTask
import androidx.palette.graphics.Palette
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass
import com.photo.editor.picskills.photoeditorpro.ads.AdManager.CallBackInterstitial
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.photo.editor.picskills.photoeditorpro.ads.AdManager
import com.photo.editor.picskills.photoeditorpro.utils.Constants
import com.photo.editor.picskills.photoeditorpro.utils.ImageUtils
import com.photo.editor.picskills.photoeditorpro.utils.UtilsPak
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*

class WingsActivity : ParentActivity(), MenuItemClickLister, View.OnClickListener {
    var mCount = 0
    var isFirstTime = true
    var sbBackgroundOpacity: SeekBar? = null
    private var selectedBit: Bitmap? = null
    private var cutBit: Bitmap? = null
    private var mContext: Context? = null
    private var slideUpAnimation: Animation? = null
    private var slideDownAnimation: Animation? = null
    private var spiralEffectListAdapter: SpiralEffectListAdapter? = null
    private val wing = 37
    private var recyclerNeonEffect: RecyclerView? = null
    private var recyclerSticker: RecyclerView? = null
    private val wingsEffect = ArrayList<String>()
    private var iv_face: ImageView? = null
    private var setback: ImageView? = null
    private var setimg: ImageView? = null
    private var mContentRootView: RelativeLayout? = null
    private var savedImageUri: Uri? = null
    private var stickerView: StickerView? = null
    private var currentSticker: Sticker? = null
    private var linThirdDivisionOption: LinearLayout? = null
    private var linEffect: LinearLayout? = null
    private var linBackgroundBlur: LinearLayout? = null
    private var tabLayout: TabLayout? = null
    private var oldSavedFileName: String? = null

    private var adLayout:FrameLayout? = null

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_wings)

        adLayout = findViewById(R.id.adLayout)
        bannerAds()

        EditingPicActivity.stopAnim()
        Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandlerPix(this@WingsActivity))
        mContext = this
        selectedBit = faceBitmap
        Handler(Looper.getMainLooper()).postDelayed({
            setimg!!.post {
                if (isFirstTime && selectedBit != null) {
                    isFirstTime = false
                    //create bitmap for image
                    initBMPNew()
                }
            }
        }, 1000)

        //wings thum list created
        wingsEffect.add("none")
        for (i in 1..wing) {
            wingsEffect.add("w_$i")
        }
        Init()
        setTollbarData()
        AdManager.loadInterstitialAd()
    }

    /**************************************Banner Ads *********************************/
    fun bannerAds() {
        adLayout?.visibility = View.VISIBLE
        adLayout?.post { loadBanner() }
    }

    private fun loadBanner() {
        Log.e("myTag", "BannerAds")
        val adView = AdView(this)
        adView.adUnitId = UtilsPak.getBannerID()
        val adSize = adSize
        adView.setAdSize(adSize)
        adLayout?.addView(adView)
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
            var adWidthPixels = adLayout!!.width.toFloat()
            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }


    private fun initBMPNew() {
        if (faceBitmap != null) {
            selectedBit =
                ImageUtils.getBitmapResize(mContext, faceBitmap, setimg!!.width, setimg!!.height)
            mContentRootView!!.layoutParams =
                LinearLayout.LayoutParams(selectedBit!!.getWidth(), selectedBit!!.getHeight())
            if (selectedBit != null && iv_face != null) {
                iv_face!!.setImageBitmap(
                    FastBlur().processBlur(
                        selectedBit,
                        1f,
                        if (sbBackgroundOpacity!!.progress == 0) 1 else sbBackgroundOpacity!!.progress
                    )
                )
            }
            cutmaskNew()
        }
    }

    private fun Init() {
        findViewById<View>(R.id.ivShowHomeOption).visibility = View.GONE
        stickerView = findViewById<View>(R.id.sticker_view) as StickerView
        mContentRootView = findViewById(R.id.mContentRootView)
        setfront = findViewById(R.id.setfront)
        setback = findViewById(R.id.setback)
        iv_face = findViewById(R.id.iv_face)
        setimg = findViewById(R.id.setimg)
        iv_face?.setOnClickListener(View.OnClickListener {
            if (stickerView!!.currentSticker != null) {
                stickerView!!.currentSticker!!.release()
            }
        })
        linEffect = findViewById<View>(R.id.linEffect) as LinearLayout
        tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        createTabIcons()
        tabLayout!!.getTabAt(0)
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                onBottomTabSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                onBottomTabSelected(tab)
            }
        })
        recyclerNeonEffect = findViewById<View>(R.id.recyclerNeonEffect) as RecyclerView
        recyclerNeonEffect!!.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        linBackgroundBlur = findViewById(R.id.linBackgroundBlur)
        setUpBottomList()
        val ivCheckMark = findViewById<View>(R.id.ivCheckMark) as AppCompatImageView
        ivCheckMark.setOnClickListener(this)
        val ivClose = findViewById<View>(R.id.ivClose) as AppCompatImageView
        ivClose.setOnClickListener(this)
        recyclerSticker = findViewById<View>(R.id.recyclerSticker) as RecyclerView
        recyclerSticker!!.layoutManager = GridLayoutManager(this, 3)
        linThirdDivisionOption = findViewById<View>(R.id.linThirdDivisionOption) as LinearLayout
        initMainStickerViewMan()
        //sticker list create
        setStickerImages(30)
        tabLayout!!.visibility = View.VISIBLE
        linEffect!!.visibility = View.GONE
        linBackgroundBlur?.setVisibility(View.GONE)
        findViewById<View>(R.id.ivShowHomeOption).setOnClickListener { onBackPressed() }
        spiralEffectListAdapter!!.addData(wingsEffect)
        iv_face?.setRotationY(0.0f)
        setimg?.post(Runnable { initBMPNew() })
        val sbFrameOpacity = findViewById<SeekBar>(R.id.sbFrameOpacity)
        sbFrameOpacity.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, i: Int, z: Boolean) {
                if (setback != null && setfront != null) {
                    val f = i.toFloat() * 0.01f
                    setback!!.alpha = f
                    setfront!!.alpha = f
                }
            }
        })
        sbBackgroundOpacity = findViewById(R.id.sbBackgroundOpacity)
        sbBackgroundOpacity?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (selectedBit != null && iv_face != null) {
                    iv_face!!.setImageBitmap(
                        FastBlur().processBlur(
                            selectedBit,
                            1f,
                            if (seekBar.progress == 0) 1 else seekBar.progress
                        )
                    )
                }
            }

            override fun onProgressChanged(seekBar: SeekBar, i: Int, z: Boolean) {}
        })
    }

    fun setStickerImages(size: Int) {
        val stickerArrayList = ArrayList<Int>()
        for (i in 1..size) {
            val resources = resources
            stickerArrayList.add(
                Integer.valueOf(
                    resources.getIdentifier(
                        "sticker_n$i",
                        "drawable",
                        packageName
                    )
                )
            )
        }
        recyclerSticker = findViewById(R.id.recyclerSticker)
        recyclerSticker?.setLayoutManager(GridLayoutManager(this, 3))
        recyclerSticker?.setAdapter(StickerAdapter(this, stickerArrayList) { v, position ->
            itemSelectFromList(linThirdDivisionOption, recyclerSticker, false)
            val drawable = resources.getDrawable(stickerArrayList[position])
            stickerView!!.addSticker(DrawableSticker(drawable))
        })
    }

    private fun onBottomTabSelected(tab: TabLayout.Tab) {
        if (tab.position == 0) {
            findViewById<View>(R.id.ivShowHomeOption).visibility = View.VISIBLE
            viewSlideUpDown(linEffect, tabLayout)
        } else if (tab.position == 1) {
            itemSelectFromList(linThirdDivisionOption, recyclerSticker, true)
        } else if (tab.position == 2) {
            findViewById<View>(R.id.ivShowHomeOption).visibility = View.VISIBLE
            viewSlideUpDown(linBackgroundBlur, tabLayout)
        } else if (tab.position == 3) {
            /*StickerEraseActivity.b = cutBit;
            Intent intent = new Intent(this, StickerEraseActivity.class);
            intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_WINGS);
            startActivityForResult(intent, 1024);*/
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1024) {
            if (eraserResultBmp != null) {
                cutBit = eraserResultBmp
                setimg!!.setImageBitmap(cutBit)
            }
        }
    }

    private fun createTabIcons() {
        val view = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null)
        val textOne = view.findViewById<View>(R.id.text) as CustomTextView
        val ImageOne = view.findViewById<View>(R.id.image) as ImageView
        textOne.text = getString(R.string.txt_effect)
        ImageOne.setImageResource(R.drawable.ic_neon_effect_svg)
        tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(view))
        val view3 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null)
        val text3 = view3.findViewById<View>(R.id.text) as CustomTextView
        val Image3 = view3.findViewById<View>(R.id.image) as ImageView
        text3.text = getString(R.string.txt_stickers)
        Image3.setImageResource(R.drawable.ic_stickers)
        tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(view3))

        /* View view2 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView textTwo = (CustomTextView) view2.findViewById(R.id.text);
        ImageView ImageTwo = (ImageView) view2.findViewById(R.id.image);
        textTwo.setText(getString(R.string.txt_erase));
        ImageTwo.setImageResource(R.drawable.ic_erase);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));*/
        val view4 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null)
        val text4 = view4.findViewById<View>(R.id.text) as CustomTextView
        val Image4 = view4.findViewById<View>(R.id.image) as ImageView
        text4.text = getString(R.string.txt_background)
        Image4.setImageResource(R.drawable.ic_backchange)
        tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(view4))
    }

    fun itemSelectFromList(
        linLayout: LinearLayout?,
        recyclerView: RecyclerView?,
        upAnimation: Boolean
    ) {
        //recyclerView.setVisibility(View.VISIBLE);
        if (upAnimation) {
            linLayout!!.visibility = View.VISIBLE
            slideUpAnimation = AnimationUtils.loadAnimation(
                applicationContext, R.anim.slide_up
            )
            linLayout.startAnimation(slideUpAnimation)
            recyclerView!!.scrollToPosition(0)
        } else {
            slideDownAnimation = AnimationUtils.loadAnimation(
                applicationContext, R.anim.slide_down
            )
            linLayout!!.startAnimation(slideDownAnimation)
            slideDownAnimation?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    linLayout.visibility = View.GONE
                    // recyclerView.setVisibility(View.GONE);
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
    }

    fun viewSlideUpDown(showLayout: View?, hideLayout: View?) {
        showLayout!!.visibility = View.VISIBLE
        slideUpAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)
        showLayout.startAnimation(slideUpAnimation)
        slideDownAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        hideLayout!!.startAnimation(slideDownAnimation)
        slideDownAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                hideLayout.visibility = View.GONE
                // recyclerView.setVisibility(View.GONE);
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun initMainStickerViewMan() {
        stickerView!!.isLocked = false
        stickerView!!.isConstrained = true
        stickerView!!.onStickerOperationListener = object : OnStickerOperationListener {
            override fun onStickerAdded(sticker: Sticker) {
                Log.e("TAG", "onStickerAdded")
            }

            override fun onStickerClicked(sticker: Sticker) {
                Log.e("TAG", "onStickerClicked")
            }

            override fun onStickerDeleted(sticker: Sticker) {
                removeStickerWithDeleteIcon()
                Log.e("TAG", "onStickerDeleted")
            }

            override fun onStickerDragFinished(sticker: Sticker) {
                Log.e("TAG", "onStickerDragFinished")
            }

            override fun onStickerTouchedDown(sticker: Sticker) {
                stickerOptionTaskPerformMan(sticker)
            }

            override fun onStickerZoomFinished(sticker: Sticker) {
                Log.e("TAG", "onStickerZoomFinished")
            }

            override fun onStickerFlipped(sticker: Sticker) {
                Log.e("TAG", "onStickerFlipped")
            }

            override fun onStickerDoubleTapped(sticker: Sticker) {
                Log.e("TAG", "onDoubleTapped: double tap will be with two click")
            }
        }
    }

    override fun onBackPressed() {
        findViewById<View>(R.id.ivShowHomeOption).visibility = View.GONE
        if (linEffect!!.visibility == View.VISIBLE) {
            viewSlideUpDown(tabLayout, linEffect)
        } else if (linBackgroundBlur!!.visibility == View.VISIBLE) {
            viewSlideUpDown(tabLayout, linBackgroundBlur)
        } else if (linThirdDivisionOption!!.visibility == View.VISIBLE) {
            findViewById<View>(R.id.ivClose).performClick()
        } else {
            showBackDialog()
        }
    }

    fun stickerOptionTaskPerformMan(sticker: Sticker?) {
        stickerView!!.isLocked = false
        currentSticker = sticker
        stickerView!!.sendToLayer(stickerView!!.getStickerPosition(currentSticker))
        Log.e("TAG", "onStickerTouchedDown")
    }

    private fun removeStickerWithDeleteIcon() {
        stickerView!!.remove(currentSticker)
        currentSticker = null
        if (stickerView!!.stickerCount == 0) {
        } else {
            currentSticker = stickerView!!.lastSticker
        }
    }

    fun setTollbarData() {
        val textView = findViewById<TextView>(R.id.tv_title)
        textView.text = resources.getString(R.string.wings)
        findViewById<View>(R.id.iv_back).setOnClickListener { showBackDialog() }
        findViewById<View>(R.id.tv_applay).setOnClickListener { showSaveDialog() }
    }

    private fun showSaveDialog() {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_saved)
        val window = dialog.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val button = dialog.findViewById<View>(R.id.btn_continue) as TextView
        val button2 = dialog.findViewById<View>(R.id.btn_download) as TextView
        button.setOnClickListener {
            val bitmap: Bitmap = saveImageTaskMaking().getBitmapFromView(mContentRootView)
            val uri = getFileUri(bitmap)!!
            EditingPicActivity.bitmapReturnString = uri.toString()
            finish()
            dialog.dismiss()
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
        }
        button2.setOnClickListener {
            saveImageTaskMaking().execute()
            dialog.dismiss()
        }
        dialog.setOnDismissListener { }
        dialog.show()
    }

    private fun showBackDialog() {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_leave)
        val window = dialog.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val button = dialog.findViewById<View>(R.id.btn_yes) as TextView
        val button2 = dialog.findViewById<View>(R.id.btn_no) as TextView
        button.setOnClickListener {
            finish()
            dialog.dismiss()
        }
        button2.setOnClickListener {
            val bitmap: Bitmap = saveImageTaskMaking().getBitmapFromView(mContentRootView)
            val uri = getFileUri(bitmap)!!
            EditingPicActivity.bitmapReturnString = uri.toString()
            finish()
            dialog.dismiss()
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
        }
        dialog.setOnDismissListener { }
        dialog.show()
    }

    fun openShareActivity() {
        val intent = Intent(this@WingsActivity, ShareSerpActivity::class.java)
        intent.putExtra(Constants.KEY_URI_IMAGE, savedImageUri.toString())
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    fun setUpBottomList() {
        spiralEffectListAdapter = SpiralEffectListAdapter(mContext)
        spiralEffectListAdapter!!.setClickListener(this)
        recyclerNeonEffect!!.adapter = spiralEffectListAdapter
        spiralEffectListAdapter!!.addData(wingsEffect)
    }

    override fun onMenuListClick(view: View, i: Int) {
        if (i != 0) {
            val backspiral = ImageUtils.getBitmapFromAsset(
                mContext, "spiral/back/" + spiralEffectListAdapter!!.itemList[i]
                        + if (spiralEffectListAdapter!!.itemList[i].startsWith("b")) "_back.png" else ".png"
            )
            val fronspiral = ImageUtils.getBitmapFromAsset(
                mContext,
                "spiral/front/" + spiralEffectListAdapter!!.itemList[i] + "_front.png"
            )
            setback!!.setImageBitmap(backspiral)
            setfront!!.setImageBitmap(fronspiral)
        } else {
            setback!!.setImageResource(0)
            setfront!!.setImageResource(0)
        }
        setback!!.setOnTouchListener(DHANVINE_MultiTouchListener(this, true))
    }

    fun cutmaskNew() {
        val progressBar = findViewById<View>(R.id.crop_progress_bar) as ProgressBar
        progressBar.visibility = View.VISIBLE
        object : CountDownTimer(5000, 1000) {
            override fun onFinish() {}
            override fun onTick(j: Long) {
                mCount = mCount + 1
                val unused = mCount
                if (progressBar.progress <= 90) {
                    progressBar.progress = mCount * 5
                }
            }
        }.start()
        MLCropAsyncTask({ bitmap, bitmap2, left, top ->
            val iArr = intArrayOf(0, 0, selectedBit!!.width, selectedBit!!.height)
            val width = selectedBit!!.width
            val height = selectedBit!!.height
            val i = width * height
            selectedBit!!.getPixels(IntArray(i), 0, width, 0, 0, width, height)
            val iArr2 = IntArray(i)
            val createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            createBitmap.setPixels(iArr2, 0, width, 0, 0, width, height)
            cutBit = ImageUtils.getMask(mContext, selectedBit, createBitmap, width, height)
            val resizedBitmap = Bitmap.createScaledBitmap(
                bitmap, cutBit!!.getWidth(), cutBit!!.getHeight(), false
            )
            cutBit = resizedBitmap
            runOnUiThread {
                val p = Palette.from(cutBit!!).generate()
                if (p.dominantSwatch == null) {
                    Toast.makeText(
                        this@WingsActivity,
                        getString(R.string.txt_not_detect_human),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setimg!!.setImageBitmap(cutBit)
            }
        }, this, progressBar).execute(*arrayOfNulls(0))
    }

    private fun getFileUri(inImage: Bitmap): Uri? {
        return try {
            var tempDir: File? = null
            tempDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            } else {
                Environment.getExternalStorageDirectory()
            }
            tempDir = File(tempDir.absolutePath + "/.temp/")
            tempDir.mkdir()
            val tempFile = File.createTempFile("IMG_" + System.currentTimeMillis(), ".jpg", tempDir)
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val bitmapData = bytes.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(tempFile)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
            Uri.fromFile(tempFile)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivCheckMark, R.id.ivClose -> if (linThirdDivisionOption!!.visibility == View.VISIBLE) {
                if (currentSticker == null) currentSticker = stickerView!!.currentSticker
                if (recyclerSticker!!.visibility == View.VISIBLE) {
                    itemSelectFromList(linThirdDivisionOption, recyclerSticker, false)
                }
            }
        }
    }

    private inner class saveImageTaskMaking : AsyncTask<String?, String?, Exception?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<View>(R.id.ivShowHomeOption).visibility = View.GONE
            stickerView!!.isLocked = true
        }

        fun getBitmapFromView(view: View?): Bitmap {
            val bitmap = Bitmap.createBitmap(view!!.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }

        override fun onPostExecute(e: Exception?) {
            super.onPostExecute(e)
            findViewById<View>(R.id.ivShowHomeOption).visibility = View.VISIBLE
            if (e == null) {
                showInterstitial()
            } else {
                Toast.makeText(this@WingsActivity, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        override fun doInBackground(vararg params: String?): Exception? {
            mContentRootView!!.isDrawingCacheEnabled = true
            //            Bitmap bitmap = mContentRootView.getDrawingCache();
            val bitmap = getBitmapFromView(mContentRootView)
            val fileName =
                getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentResolver = contentResolver
                    val contentValues = ContentValues()
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    contentValues.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_folder2)
                    )
                    val uri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    val fos =
                        Objects.requireNonNull(uri)
                            ?.let { contentResolver.openOutputStream(it) } as FileOutputStream?
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    Objects.requireNonNull(fos)
                    if (uri != null) savedImageUri = uri
                    FilterLabActivity.notifyMediaScannerService(this@WingsActivity, uri!!.path)
                } else {
                    val myDir = File(
                        Environment.getExternalStorageDirectory()
                            .toString() + getString(R.string.app_folder)
                    )
                    if (!myDir.exists()) myDir.mkdirs()
                    val file = File(myDir, fileName)
                    if (oldSavedFileName != null) {
                        val oldFile = File(myDir, oldSavedFileName)
                        if (oldFile.exists()) oldFile.delete()
                    }
                    oldSavedFileName = fileName
                    try {
                        val out = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.flush()
                        out.close()
                        val uri =
                            SupportedClass.addImageToGallery(this@WingsActivity, file.absolutePath)
                        if (uri != null) savedImageUri = uri
                        FilterLabActivity.notifyMediaScannerService(
                            this@WingsActivity,
                            myDir.absolutePath
                        )
                    } catch (e: Exception) {
                        return e
                    } finally {
                        mContentRootView!!.isDrawingCacheEnabled = false
                    }
                }
            } catch (e: Exception) {
                return e
            } finally {
                mContentRootView!!.isDrawingCacheEnabled = false
            }
            return null
        }
    }

    private fun showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (isInterstitialLoaded()) {
            AdManager.showInterstitial(this@WingsActivity, object : CallBackInterstitial {
                override fun interstitialShowedFullScreenContent() {}
                override fun interstitialFailedToShowFullScreenContent(adError: AdError?) {
                    openShareActivity()
                }

                override fun interstitialDismissedFullScreenContent() {
                    openShareActivity()
                }
            })
        } else {
            if (savedImageUri != null) {
                Log.e("Ad", "Ad did not load")
                openShareActivity()
            }
        }
    }

    companion object {
        var setfront: ImageView? = null
        var eraserResultBmp: Bitmap? = null
        private var faceBitmap: Bitmap? = null

        //ads variables
        private const val TAG = "WingsActivity"

        @JvmStatic
        fun setFaceBitmap(bitmap: Bitmap?) {
            faceBitmap = bitmap
        }
    }
}