package com.example.ui.components

import android.graphics.Color as AndroidColor
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

/** Development-only placement. Replace this test ID before publishing. */
const val ADMOB_NATIVE_TEST_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

private data class NativeAdAssets(
    val attribution: TextView,
    val headline: TextView,
    val body: TextView,
    val callToAction: Button
)

@Composable
fun NativeAdCard(
    modifier: Modifier = Modifier,
    adUnitId: String = ADMOB_NATIVE_TEST_UNIT_ID
) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    DisposableEffect(context, adUnitId) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { loadedAd ->
                nativeAd?.destroy()
                nativeAd = loadedAd
            }
            .build()
        adLoader.loadAd(AdRequest.Builder().build())

        onDispose {
            nativeAd?.destroy()
            nativeAd = null
        }
    }

    val ad = nativeAd ?: return
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { viewContext ->
            NativeAdView(viewContext).apply {
                val content = LinearLayout(viewContext).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(32, 24, 32, 24)
                    setBackgroundColor(AndroidColor.rgb(250, 248, 245))
                }
                val attribution = TextView(viewContext).apply {
                    text = "ADVERTISEMENT"
                    textSize = 10f
                    setTextColor(AndroidColor.DKGRAY)
                }
                val headline = TextView(viewContext).apply {
                    textSize = 17f
                    setTextColor(AndroidColor.BLACK)
                }
                val body = TextView(viewContext).apply {
                    textSize = 14f
                    setTextColor(AndroidColor.DKGRAY)
                }
                val callToAction = Button(viewContext).apply { gravity = Gravity.CENTER }
                content.addView(attribution)
                content.addView(headline)
                content.addView(body)
                content.addView(callToAction)
                addView(content)
                headlineView = headline
                bodyView = body
                callToActionView = callToAction
                tag = NativeAdAssets(attribution, headline, body, callToAction)
            }
        },
        update = { adView ->
            val assets = adView.tag as NativeAdAssets
            assets.headline.text = ad.headline
            assets.body.text = ad.body.orEmpty()
            assets.body.visibility = if (ad.body.isNullOrBlank()) View.GONE else View.VISIBLE
            assets.callToAction.text = ad.callToAction.orEmpty()
            assets.callToAction.visibility = if (ad.callToAction.isNullOrBlank()) View.GONE else View.VISIBLE
            adView.setNativeAd(ad)
        }
    )
}
