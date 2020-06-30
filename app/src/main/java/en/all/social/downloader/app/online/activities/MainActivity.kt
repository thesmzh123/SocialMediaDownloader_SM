package en.all.social.downloader.app.online.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.anjlab.android.iab.v3.BillingProcessor
import en.all.social.downloader.app.online.utils.SharedPrefUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import en.all.social.downloader.app.online.utils.PermissionsUtils
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.utils.Constants.PRODUCT_KEY
import en.all.social.downloader.app.online.utils.InternetConnection


@Suppress("DEPRECATION")
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            this.window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
            )
            setContentView(R.layout.activity_main)
            if (!SharedPrefUtils.getBooleanData(this@MainActivity, "isTerms")) {
                startActivity(Intent(applicationContext, TermsAndConditionsActivity::class.java))
                finish()

            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    val permissionsUtils = PermissionsUtils().getInstance(this)
                    if (permissionsUtils?.isAllPermissionAvailable()!!) {
                        Log.d("Test", "Permission")
                    } else {
                        permissionsUtils.setActivity(this)
                        permissionsUtils.requestPermissionsIfDenied()
                    }
                }
                fetchKeyIO()
                val navView: BottomNavigationView = findViewById(R.id.nav_view)

                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController
                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.
                appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.nav_home,
                        R.id.navigation_dashboard
                    )
                )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
                bp = BillingProcessor(
                    this@MainActivity,
                    SharedPrefUtils.getStringData(this, "lickey"),
                    this@MainActivity
                )
                bp!!.initialize()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.no_ads_menu, menu)
        if (menu != null) {
            noAdsItem = menu.findItem(R.id.no_ads)
            noAdsItem!!.isVisible = !SharedPrefUtils.getBooleanData(this@MainActivity, "hideAds")
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.no_ads) {
            try {
                bp!!.purchase(this@MainActivity, PRODUCT_KEY)
            } catch (e: Exception) {
                e.printStackTrace()
                if (InternetConnection().checkConnection(applicationContext)) {
                    showToast("Restart this app to use this feature")
                } else {
                    showToast(getString(R.string.no_internet))
                }
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}