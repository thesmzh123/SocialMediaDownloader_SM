package en.all.social.downloader.app.online.activities

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import en.all.social.downloader.app.online.utils.SharedPrefUtils
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.utils.Constants.TAGI

class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)


        loadInterstial()

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAGI, "on R")
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            if (!SharedPrefUtils.getBooleanData(this, "isFirst")) {
                startNewActivty(MainActivity())
            } else {
                startNewActivtyAds(MainActivity())
            }
        }, 3000)
    }

    override fun onBackPressed() {
        finish()

    }


}
