package com.example.imagessubredditviewer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageloader.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),IImageApiResult {
    private var mImagesPresenter: IImagePresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }
    companion object {
        private const val WRITE_EXTERNAL_STORAGE = 0x01
    }
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
            println("👁"+"permission not given")
        }
        else
        {
            callAPI()
            println("👁"+"permission already given")
        }
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE)
    }
    override fun onRequestPermissionsResult(requestCode: Int,
              permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    callAPI()
                }
            }
        }
    }
     private fun hideSystemUI() {
    // Enables regular immersive mode.
    // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
    // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
            // Set the content to appear under the system bars so that the
            // content doesn't resize when the system bars hide and show.
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // Hide the nav bar and status bar
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}
        private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
    private fun initUI() {
       mImagesPresenter=ImagesPresenter(this)
        setupPermissions()//ask for storage permission
    }
    //show full screen view
    private fun showfullscreenview() {
        hideSystemUI()
        main_layout.visibility=View.GONE
        fullscreenlayout.visibility=View.VISIBLE
        fullscreenlayout.setOnClickListener {
                main_layout.visibility=View.VISIBLE
                fullscreenlayout.visibility=View.GONE
                expanded_image.setImageBitmap(null)
                showSystemUI()
            }
        }
    private fun isNetworkConnected(): Boolean {
      val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val activeNetwork = connectivityManager.activeNetwork
      val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
      return networkCapabilities != null &&
          networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    private fun callAPI()
    {
        println("✌️ api call");
        if (isNetworkConnected()) {
          mImagesPresenter!!.callApi()
        } else {
          AlertDialog.Builder(this).setTitle("No Internet Connection")
              .setMessage("Please check your internet connection and try again")
              .setPositiveButton(android.R.string.ok) { _, which -> finish() }
              .setIcon(android.R.drawable.ic_dialog_alert).show()
        }
    }
    override fun onApiCallback(imagesUrlList: MutableList<String>) {
        println("✌️ api call response $imagesUrlList");
         imagerecycler.adapter=ImageViewAdapter(this,imagesUrlList)
          progressbar.visibility=View.GONE
          main_layout.visibility=View.VISIBLE

        imagerecycler.setOnItemClickListener { parent, view, position, id ->
            var imageLoader=ImageLoader(this)
            imageLoader.DisplayImage(imagesUrlList[position],expanded_image)
            showfullscreenview()
        }
    }


}