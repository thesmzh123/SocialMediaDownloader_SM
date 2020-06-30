package en.all.social.downloader.app.online.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import en.all.social.downloader.app.online.R
import en.all.social.downloader.app.online.utils.Constants.PRODUCT_KEY
import en.all.social.downloader.app.online.utils.Constants.TAGI
import en.all.social.downloader.app.online.utils.SharedPrefUtils
import kotlinx.android.synthetic.main.layout_loading_dialog.view.*

open class BaseActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    private var dialog: AlertDialog? = null
    private lateinit var interstitial: InterstitialAd
    var bp: BillingProcessor? = null
    var noAdsItem: MenuItem? = null

    //TODO: show dialog
    fun showDialog(message: String) {
        dialog = setProgressDialog(message)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    //TODO: hide dialog
    fun hideDialog() {
        if (dialog?.isShowing!!) {
            dialog?.dismiss()
        }
    }

    @SuppressLint("InflateParams")
    private fun setProgressDialog(message: String): AlertDialog {

        val builder = MaterialAlertDialogBuilder(
            this@BaseActivity
        )
        builder.setCancelable(false)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.layout_loading_dialog, null)
        builder.setView(view)

        view.dialogText.text = message
        return builder.create()
    }

    //TODO: load interstial
    fun loadInterstial() {
        try {
            Log.d(TAGI, "load ads")
            if (!SharedPrefUtils.getBooleanData(this@BaseActivity, "hideAds")) {
                interstitial = InterstitialAd(this)
                interstitial.adUnitId = getString(R.string.interstitial)
                try {
                    if (!interstitial.isLoading && !interstitial.isLoaded) {
                        val adRequest = AdRequest.Builder().build()
                        interstitial.loadAd(adRequest)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.d(TAGI, "error: " + ex.message)
                }

                requestNewInterstitial()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO: requestNewInterstitial
    private fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder().build()
        interstitial.loadAd(adRequest)
    }

    //TODO: start activity
    fun startNewActivty(activity: Activity) {
        startActivity(Intent(this@BaseActivity, activity.javaClass))
        finish()
    }

    //TODO: start activity  as ads
    fun startNewActivtyAds(activity: Activity) {
        if (!SharedPrefUtils.getBooleanData(this@BaseActivity, "hideAds")) {

            if (interstitial.isLoaded) {
                if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(
                        Lifecycle.State.STARTED
                    )
                ) {
                    interstitial.show()
                } else {
                    Log.d(TAGI, "App Is In Background Ad Is Not Going To Show")
                }
            } else {
                startActivity(Intent(this@BaseActivity, activity.javaClass))
                finish()

            }
            interstitial.adListener = object : AdListener() {
                override fun onAdClosed() {
                    requestNewInterstitial()
                    startActivity(Intent(this@BaseActivity, activity.javaClass))
                    finish()

                }
            }
        } else {
            startActivity(Intent(this@BaseActivity, activity.javaClass))
            finish()
        }
    }

    fun fetchKeyIO() {
        //connecting declared wiidgets with xml
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("all_social_downloader")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val value =
                    dataSnapshot.child("license_key").getValue(
                        String::class.java
                    )!!
                Log.d(TAGI, value)
                try {
                    SharedPrefUtils.saveData(
                        applicationContext,
                        "lickey",
                        value
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {
                Log.d(
                    TAGI,
                    "loadPost:onCancelled",
                    databaseError.toException()
                )
            }
        })
    }

    override fun onBillingInitialized() {
        Log.d(TAGI, "onBillingInitialized")
    }

    override fun onPurchaseHistoryRestored() {
        try {
            Log.d(TAGI, "onPurchaseHistoryRestored: ")
            if (bp!!.isPurchased(PRODUCT_KEY)) {
                Log.d(TAGI, "onPurchaseHistoryRestored: true")
                hideAds()
            } else {
                Log.d(TAGI, "onPurchaseHistoryRestored: false")
                loadInterstial()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        try {
            Log.d(TAGI, "onProductPurchased: $productId")
            Log.d(TAGI, "onProductPurchased: $details")
            hideAds()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        try {
            Log.d(TAGI, "onBillingError: " + error?.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        if (bp != null) {
            bp!!.release()
        }
        super.onDestroy()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (!bp!!.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data)
                Log.d(TAGI, "onActivityResult: done")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //TODO: hide ads
    private fun hideAds() {
        try {
            SharedPrefUtils.saveData(this, "hideAds", true)
            noAdsItem!!.isVisible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun showToast(message: String) {
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()
    }
}