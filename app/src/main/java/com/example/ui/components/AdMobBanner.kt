package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.util.NetworkConnectivityObserver
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import java.util.concurrent.atomic.AtomicBoolean

private val isMobileAdsInitialized = AtomicBoolean(false)

/**
 * Standard Google AdMob test banner ad unit ID.
 * Replace with your real AdMob Ad Unit ID before release to production on the Google Play Console.
 */
const val ADMOB_BANNER_TEST_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

/**
 * Displays a Google AdMob banner ad strictly when an active internet connection is present.
 * If offline, no network calls are initiated and no empty ad container is displayed,
 * adhering to Google Play policies and ensuring an uninterrupted meditation experience.
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = ADMOB_BANNER_TEST_UNIT_ID
) {
    val context = LocalContext.current
    val connectivityObserver = remember(context) { NetworkConnectivityObserver(context) }
    var isConnected by remember { mutableStateOf(connectivityObserver.isCurrentlyConnected()) }
    var isAdLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(connectivityObserver) {
        connectivityObserver.observe().collect { connected ->
            isConnected = connected
            if (!connected) {
                isAdLoaded = false
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isMobileAdsInitialized.getAndSet(true)) {
            try {
                MobileAds.initialize(context) {}
            } catch (_: Exception) {
                // Ignore initialization failures gracefully in offline / test environments
            }
        }
    }

    AnimatedVisibility(
        visible = isConnected && isAdLoaded,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp)
                .testTag("admob_banner_container"),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admob_adview"),
                factory = { ctx ->
                    AdView(ctx).apply {
                        setAdSize(AdSize.BANNER)
                        this.adUnitId = adUnitId
                        adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                isAdLoaded = true
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                isAdLoaded = false
                            }
                        }
                        try {
                            loadAd(AdRequest.Builder().build())
                        } catch (_: Exception) {
                            isAdLoaded = false
                        }
                    }
                },
                update = { adView ->
                    if (isConnected && !isAdLoaded) {
                        try {
                            adView.loadAd(AdRequest.Builder().build())
                        } catch (_: Exception) {
                            isAdLoaded = false
                        }
                    }
                }
            )
        }
    }
}
