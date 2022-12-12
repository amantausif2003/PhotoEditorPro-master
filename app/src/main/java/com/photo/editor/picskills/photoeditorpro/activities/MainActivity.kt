package com.photo.editor.picskills.photoeditorpro.activities

import com.photo.editor.picskills.photoeditorpro.ads.AdManager.loadInterstitialAd
import com.photo.editor.picskills.photoeditorpro.adapter.MainStatusAdapter.MainStatusClickListener
import com.photo.editor.picskills.photoeditorpro.model.AppDesignModel
import com.photo.editor.picskills.photoeditorpro.adapter.MainStatusAdapter
import androidx.recyclerview.widget.RecyclerView
import com.photo.editor.picskills.photoeditorpro.R
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.photo.editor.picskills.photoeditorpro.fragments.SliderOne
import com.photo.editor.picskills.photoeditorpro.fragments.SliderTwo
import com.photo.editor.picskills.photoeditorpro.adapter.ViewPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.content.ContextCompat
import android.content.Intent
import android.app.Dialog
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass
import androidx.core.content.FileProvider
import android.graphics.Bitmap
import android.widget.Toast
import android.graphics.BitmapFactory
import android.content.DialogInterface
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.photo.editor.picskills.photoeditorpro.BuildConfig
import com.photo.editor.picskills.photoeditorpro.ads.AdManager
import com.photo.editor.picskills.photoeditorpro.utils.Constants
import com.photo.editor.picskills.photoeditorpro.utils.ImageUtils
import com.photo.editor.picskills.photoeditorpro.utils.UtilsPak
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

open class MainActivity : AppCompatActivity(), MainStatusClickListener,
    View.OnClickListener, EasyPermissions.PermissionCallbacks, AdManager.CallBackInterstitial {
    private val statusDesignList: ArrayList<AppDesignModel?> = ArrayList<AppDesignModel?>()
    private var mainStatusAdapter: MainStatusAdapter? = null
    private var statusRecycler: RecyclerView? = null
    private var adLayout:FrameLayout? = null

    //open gallery for blur activity
    var mSelectedImagePath: String? = null
    var mSelectedOutputPath: String? = null
    var mSelectedImageUri: Uri? = null
    private var isGallerySelected = 0

    //Timer variablse
    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 3000
    val PERIOD_MS: Long = 3000
    var NUM_PAGES = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this

        adLayout = findViewById(R.id.adLayout)

        AdManager.loadInterstitialAd()
        bannerAds()

        statusRecycler = findViewById(R.id.statusRecycler)



        val wingsLinear = findViewById<LinearLayout>(R.id.wings_linear)
        val spiralLinearLayout = findViewById<LinearLayout>(R.id.spiral_linear)
        val blurLinear = findViewById<LinearLayout>(R.id.blur_linear)
        val bAndWLinear = findViewById<LinearLayout>(R.id.b_and_w_linear)
        val filterLinear = findViewById<LinearLayout>(R.id.filter_linear)
        val gradientLinear = findViewById<LinearLayout>(R.id.gradient_linear)
        val galleryLinear = findViewById<LinearLayout>(R.id.gallery_linear)
        val cameraLinear = findViewById<LinearLayout>(R.id.camera_linear)
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager)

        spiralLinearLayout.setOnClickListener(this)
        wingsLinear.setOnClickListener(this)
        bAndWLinear.setOnClickListener(this)
        blurLinear.setOnClickListener(this)
        filterLinear.setOnClickListener(this)
        gradientLinear.setOnClickListener(this)
        cameraLinear.setOnClickListener(this)
        galleryLinear.setOnClickListener(this)
        setDesignListItem()
        setStatusAdapater()
        val fragment: ArrayList<Fragment?> = ArrayList<Fragment?>()
        fragment.add(SliderOne())
        fragment.add(SliderTwo())
        val adapter = ViewPagerAdapter(this, fragment)
        viewPager2.adapter = adapter

        /*After setting the adapter use the timer */
        val handler = Handler(Looper.getMainLooper())
        val Update = Runnable {
            if (currentPage == NUM_PAGES - 1) {
                currentPage = 0
            }
            viewPager2.setCurrentItem(currentPage++, true)
        }
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)
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

    fun setStatusAdapater() {
        mainStatusAdapter = MainStatusAdapter(statusDesignList, this)
        statusRecycler!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        statusRecycler!!.adapter = mainStatusAdapter
    }

    fun setDesignListItem() {
        val editPicModel1 = AppDesignModel()
        editPicModel1.drawable = ContextCompat.getDrawable(this, R.drawable.wing_status_icon)
        editPicModel1.text = "Wings"
        val editPicModel2 = AppDesignModel()
        editPicModel2.drawable = ContextCompat.getDrawable(this, R.drawable.spiral_status_icon)
        editPicModel2.text = "Spiral"
        val editPicModel3 = AppDesignModel()
        editPicModel3.drawable = ContextCompat.getDrawable(this, R.drawable.blur_status_icon)
        editPicModel3.text = "Blur"
        val editPicModel4 = AppDesignModel()
        editPicModel4.text = "B & W"
        editPicModel4.drawable = ContextCompat.getDrawable(this, R.drawable.b_and_w_icon)
        val editPicModel5 = AppDesignModel()
        editPicModel5.text = "Filter"
        editPicModel5.drawable = ContextCompat.getDrawable(this, R.drawable.simple_status_icon)
        val editPicModel6 = AppDesignModel()
        editPicModel6.text = "Gradient"
        editPicModel6.drawable = ContextCompat.getDrawable(this, R.drawable.gradient_status_icon)
        statusDesignList.add(editPicModel1)
        statusDesignList.add(editPicModel2)
        statusDesignList.add(editPicModel3)
        statusDesignList.add(editPicModel4)
        statusDesignList.add(editPicModel5)
        statusDesignList.add(editPicModel6)
    }

    override fun mainStatusClick(drawable: AppDesignModel, position: Int) {
        if (position == 0) {
            moveStatusActivity()
            isModelSelected = position
        }
        if (position == 1) {
            moveStatusActivity()
            isModelSelected = position
        }
        if (position == 2) {
            moveStatusActivity()
            isModelSelected = position
        }
        if (position == 3) {
            moveStatusActivity()
            isModelSelected = position
        }
        if (position == 4) {
            moveStatusActivity()
            isModelSelected = position
        }
        if (position == 5) {
            moveStatusActivity()
            isModelSelected = position
        }
    }

    private fun moveStatusActivity() {
        val intent = Intent(this@MainActivity, StatusActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            try {
                mSelectedImagePath = mSelectedOutputPath
                save(mSelectedImagePath)
                if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                    val fileImageClick = File(mSelectedImagePath)
                    if (fileImageClick.exists()) {
                        mSelectedImageUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            Uri.fromFile(fileImageClick)
                        } else {
                            FileProvider.getUriForFile(
                                activity!!,
                                BuildConfig.APPLICATION_ID + ".provider",
                                fileImageClick
                            )
                        }
                        val intent = Intent(activity, CropPhotoActivity::class.java)
                        intent.putExtra("cropUri", mSelectedImageUri.toString())
                        startActivityForResult(intent, REQUEST_CODE_CROPPING)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_CODE_GALLERY) {
            try {
                mSelectedImageUri = data.data
                if (mSelectedImageUri != null) {
                    mSelectedImagePath =
                        Constants.convertMediaUriToPath(activity, mSelectedImageUri)
                    val bitmap = ImageUtils.compressImage(mSelectedImageUri.toString(), activity)
                    mSelectedImageUri = getFileUri(bitmap)
                    if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                        val intent = Intent(activity, CropPhotoActivity::class.java)
                        intent.putExtra("cropUri", mSelectedImageUri.toString())
                        startActivityForResult(intent, REQUEST_CODE_CROPPING)
                    }
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.please_try_again),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CROPPING && data != null) {
            try {
                if (data.hasExtra("croppedUri")) {
                    mSelectedImageUri = data.getParcelableExtra("croppedUri")
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = Constants.getBitmapFromUriDrip(
                            activity,
                            mSelectedImageUri,
                            1024.0f,
                            1024.0f
                        )
                        mSelectedImageUri = getFileUri(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (bitmap != null) {
                        val intent = Intent(activity, EditingPicActivity::class.java)
                        intent.putExtra("bitmap", mSelectedImageUri.toString())
                        startActivity(intent)
                        overridePendingTransition(R.anim.enter, R.anim.exit)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else {
            Log.e("TAG", "")
        }
    }

    fun getFileUri(inImage: Bitmap?): Uri? {
        return try {
            var tempDir: File? = null
            tempDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            } else {
                Environment.getExternalStorageDirectory()
            }
            tempDir = File(tempDir.absolutePath + "/.temp/")
            if (!tempDir.exists()) {
                tempDir.mkdir()
            }
            val tempFile = File.createTempFile("IMG_" + System.currentTimeMillis(), ".jpg", tempDir)
            val bytes = ByteArrayOutputStream()
            inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
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

    fun resizeBitmap(photoPath: String?): Bitmap {
        Log.e("Image", "resizeBitmap()")
        val targetW = 512
        val targetH = 512
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(photoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight
        var scaleFactor = 1
        scaleFactor = Math.min(photoW / targetW, photoH / targetH)
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true //Deprecated from  API 21
        return BitmapFactory.decodeFile(photoPath, bmOptions)
    }

    fun save(outFile: String?): Boolean {
        var status = true
        try {
            val bmp = resizeBitmap(outFile)
            val file = File(outFile)
            val fOut = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fOut)
            fOut.flush()
            fOut.close()
        } catch (ex: Exception) {
            status = false
            Log.e("Error", ex.message!!)
        }
        return status
    }

    private fun openSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", "com.photo.editor.picskills.photoeditorpro", null)
        )
        startActivity(intent)
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(
            activity!!
        )
        builder.setTitle("Need Storage Permission")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog: DialogInterface, which: Int ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.cancel() }
        builder.show()
    }


    fun showIdOrPermission() {
        if (AdManager.isInterstitialLoaded()) {
            AdManager.showInterstitial(this@MainActivity, this)
        } else {
            addNewPermissionFlow()
        }
    }

    override fun onClick(v: View) {

        if (v.id == R.id.wings_linear) {
            isModelSelected = 0
            showIdOrPermission()
        }
        if (v.id == R.id.spiral_linear) {
            isModelSelected = 1
            showIdOrPermission()
        }
        if (v.id == R.id.blur_linear) {
            isModelSelected = 2
            showIdOrPermission()
        }
        if (v.id == R.id.b_and_w_linear) {
            isModelSelected = 3
            showIdOrPermission()
        }
        if (v.id == R.id.filter_linear) {
            isModelSelected = 4
            showIdOrPermission()
        }
        if (v.id == R.id.gradient_linear) {
            isModelSelected = 5
            showIdOrPermission()
        }
        if (v.id == R.id.camera_linear) {
            isModelSelected = 6
            isGallerySelected = 1
            showIdOrPermission()
        }
        if (v.id == R.id.gallery_linear) {
            isModelSelected = 7
            isGallerySelected = 0
            showIdOrPermission()
        }
    }

    fun showPicImageDialog() {
        val pixDialog = Dialog(this@MainActivity)
        pixDialog.setContentView(R.layout.dialog_select_photo)
        pixDialog.setCancelable(false)
        val window = pixDialog.window
        window!!.setLayout(
            SupportedClass.getWidth(activity) / 100 * 90,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        pixDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val camera_item = pixDialog.findViewById<LinearLayout>(R.id.camera_item)
        val gallery_item = pixDialog.findViewById<LinearLayout>(R.id.gallery_item)
        val btnDismiss = pixDialog.findViewById<ImageView>(R.id.cancel)
        gallery_item.setOnClickListener { v: View? ->
            isGallerySelected = 0
            if (pixDialog.isShowing && !isFinishing) {
                pixDialog.dismiss()
                openGallery()
            }
        }
        camera_item.setOnClickListener { v: View? ->
            isGallerySelected = 1
            if (pixDialog.isShowing && !isFinishing) {
                pixDialog.dismiss()
                openCamera()
            }
        }
        btnDismiss.setOnClickListener { pixDialog.dismiss() }
        pixDialog.show()
    }

    private fun createImageFile(): File? {
        val storageDir = File(
            Environment.getExternalStorageDirectory(),
            "Android/data/" + BuildConfig.APPLICATION_ID + "/CamPic/"
        )
        storageDir.mkdirs()
        var image: File? = null
        try {
            image = File(storageDir, getString(R.string.app_folder3))
            if (image.exists()) image.delete()
            image.createNewFile()
            mSelectedOutputPath = image.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.txt_select_picture)),
            REQUEST_CODE_GALLERY
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoURI = FileProvider.getUriForFile(
            activity!!,
            BuildConfig.APPLICATION_ID + ".provider",
            createImageFile()!!
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    companion object {
        protected const val REQUEST_CODE_CAMERA = 0x2
        protected const val REQUEST_CODE_GALLERY = 0x3
        protected const val REQUEST_CODE_CROPPING = 0x4

        val Request_Read_data = 20202

        @JvmField
        var isModelSelected = 0

        @JvmStatic
        var activity: MainActivity? = null
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        when (requestCode) {

            Request_Read_data -> {
                if (perms.size == UtilsPak.readPermissionPass.size) {
                    Log.d("myPermissionsGranted", "all Permission allow with size")
                    openGalleryNewWay()
                } else {
                    Log.d("myPermissionsGranted", "not all Permission allow with size")
                }
            }

            else -> {
                Log.d("myPermissionsGranted", "no any  Permission allow")
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("myPermission", "not allow")
        if (EasyPermissions.somePermissionPermanentlyDenied(this@MainActivity, perms)) {
            AppSettingsDialog.Builder(this@MainActivity).build().show()
        }
    }

    override fun interstitialDismissedFullScreenContent() {
        addNewPermissionFlow()
    }

    override fun interstitialFailedToShowFullScreenContent(adError: AdError?) {
        addNewPermissionFlow()
    }

    override fun interstitialShowedFullScreenContent() {

    }

    private fun addNewPermissionFlow() {

        if (EasyPermissions.hasPermissions(this@MainActivity, *UtilsPak.readPermissionPass)) {
            Log.d("myPermission", "hasPermissions allow")
            openGalleryNewWay()
        } else {
            EasyPermissions.requestPermissions(
                this@MainActivity, "Please allow permissions to proceed further",
                Request_Read_data, *UtilsPak.readPermissionPass
            )
        }

    }

    private fun openGalleryNewWay() {

        Log.d("mySelection", "${isModelSelected}")

        when (isModelSelected) {
            0, 1, 2, 3, 4, 5 -> {
                showPicImageDialog()
            }
            6 -> {
                openCamera()
            }
            7 -> {
                openGallery()
            }
        }
    }

}