package tv.teads.teadssdkdemo.format.mediation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import tv.teads.adapter.admob.TeadsAdapter
import tv.teads.helper.TeadsBannerAdapterListener
import tv.teads.helper.TeadsHelper
import tv.teads.sdk.android.AdSettings
import tv.teads.teadssdkdemo.R
import tv.teads.teadssdkdemo.data.RecyclerItemType
import kotlin.math.roundToInt

/**
 * Simple RecyclerView adapter
 */
class AdMobRecyclerViewAdapter(admobBannerId: String, admobAppId: String, context: Context?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val adView: AdView = AdView(context)
    private val mListener: TeadsBannerAdapterListener

    init {
        // 1. Initialize AdMob & Teads Helper
        MobileAds.initialize(context, admobAppId)
        TeadsHelper.initialize()

        // 2. Setup the AdMob view
        adView.adUnitId = admobBannerId
        adView.adSize = AdSize.MEDIUM_RECTANGLE

        // 3. Subsribe to the listener if needed
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Toast.makeText(context, "Ad loading failed: onAdFailedToLoad($errorCode)", Toast.LENGTH_SHORT).show()
            }

            override fun onAdOpened() {
                // Nothing to do for Teads
            }

            override fun onAdLeftApplication() {
                // Nothing to do for Teads
            }

            override fun onAdClosed() {
                // Nothing to do for Teads
            }
        }

        /* 4. Create a TeadsBannerAdapterListener
        You need to create an instance for each instance of AdMob view
        it needs to be a strong reference to it, so our helper can cleanup when you don't need it anymore
         */
        mListener = object : TeadsBannerAdapterListener {
            override fun onRatioUpdated(adRatio: Float) {
                adView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        adView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val params: ViewGroup.LayoutParams = adView.layoutParams

                        // Here the width is MATCH_PARENT
                        params.height = (adView.measuredWidth / adRatio).roundToInt()

                        adView.layoutParams = params
                    }
                })
            }
        }

        // 5. Attach the listener to the helper and save the key
        val key = TeadsHelper.attachListener(mListener)

        // 6. Create the AdSettings to customize our Teads AdView
        val extras = AdSettings.Builder()
                // Needed by european regulation
                // See https://mobile.teads.tv/sdk/documentation/android/gdpr-consent
                .userConsent("1", "0001")
                // The article url if you are a news publisher to increase your earnings
                .pageUrl("https://page.com/article1/")
                // /!\ You need to add the key to the settings
                .addAdapterListener(key)
                .build()

        // 7. Create the AdRequest with the previous settings
        val adRequest = AdRequest.Builder()
                .addCustomEventExtrasBundle(TeadsAdapter::class.java, extras.toBundle())
                .build()

        // 8. Load the ad with the AdRequest
        adView.loadAd(adRequest)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> RecyclerItemType.TYPE_SCROLL_DOWN.value
            1 -> RecyclerItemType.TYPE_ARTICLE_TITLE.value
            2 -> RecyclerItemType.TYPE_ARTICLE_REAL_LINES.value
            4 -> RecyclerItemType.TYPE_TEADS.value
            else -> RecyclerItemType.TYPE_ARTICLE_FAKE_LINES.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RecyclerItemType.TYPE_SCROLL_DOWN.value -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.article_header_row, parent, false)
                ViewHolderDemo(v)
            }
            RecyclerItemType.TYPE_ARTICLE_TITLE.value -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.article_title_row, parent, false)
                ViewHolderDemo(v)
            }
            RecyclerItemType.TYPE_ARTICLE_REAL_LINES.value -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.article_real_lines, parent, false)
                ViewHolderDemo(v)
            }
            RecyclerItemType.TYPE_TEADS.value -> ViewHolderTeadsAd(adView)
            else -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.article_fake_lines, parent, false)
                ViewHolderDemo(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun getItemCount(): Int = 6

    private inner class ViewHolderTeadsAd internal constructor(view: View) : RecyclerView.ViewHolder(view)

    private inner class ViewHolderDemo internal constructor(view: View) : RecyclerView.ViewHolder(view)
}
