package nl.jcraane.simpleimagecast

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.IntroductoryOverlay.OnOverlayDismissedListener
import com.google.android.gms.common.images.WebImage
import kotlinx.android.synthetic.main.activity_main.*
import nl.jcraane.simpleimagecast.cast.EmptySessionManagerListener

class MainActivity : AppCompatActivity() {
    private var mIntroductoryOverlay: IntroductoryOverlay? = null
    private var mCastStateListener: CastStateListener? = null
    private var mediaRouteMenuItem: MenuItem? = null
    private var mCastContext: CastContext? = null
    private var mCastSession: CastSession? = null
    private var mSessionManagerListener: SessionManagerListener<CastSession>? = null
    private var currentPosition = 0

    private val images = listOf(
        "https://homepages.cae.wisc.edu/~ece533/images/boat.png",
        "https://homepages.cae.wisc.edu/~ece533/images/arctichare.png",
        "https://homepages.cae.wisc.edu/~ece533/images/baboon.png",
        "https://homepages.cae.wisc.edu/~ece533/images/lena.png"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCastContext = CastContext.getSharedInstance(this)
        setupCastListener()
        setContentView(R.layout.activity_main)

        mCastStateListener = CastStateListener { newState ->
            if (newState != CastState.NO_DEVICES_AVAILABLE) {
                showIntroductoryOverlay()
            }
        }

        imagesView.clipToPadding = false
        imagesView.clipChildren = false
        imagesView.offscreenPageLimit = 3
        imagesView.setPageTransformer(
            CarouselPageTransformer(
                pageMargin = convertToPixels(10f),
                pageOffset = convertToPixels(10f),
                viewPager = imagesView
            )
        )

        imagesView.adapter = ImageAdapter(images)
        imagesView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
                castImage(images[position])
            }
        })
    }

    override fun onResume() {
        mCastContext!!.sessionManager.addSessionManagerListener(
            mSessionManagerListener,
            CastSession::class.java
        )
        mCastContext!!.addCastStateListener(mCastStateListener)
        super.onResume()
    }

    override fun onPause() {
        mCastContext!!.removeCastStateListener(mCastStateListener)
        mCastContext!!.sessionManager.removeSessionManagerListener(
            mSessionManagerListener,
            CastSession::class.java
        )
        super.onPause()
    }

    private fun showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay?.remove()
        }
        if (mediaRouteMenuItem != null && mediaRouteMenuItem?.isVisible() == true) {
            Handler().post {
                mIntroductoryOverlay = IntroductoryOverlay.Builder(
                    this@MainActivity, mediaRouteMenuItem
                )
                    .setTitleText("Introducing Cast")
                    .setSingleTime()
                    .setOnOverlayDismissedListener(
                        OnOverlayDismissedListener { mIntroductoryOverlay = null })
                    .build()
                mIntroductoryOverlay?.show()
            }
        }
    }

    private fun castImage(url: String) {
        mCastSession?.remoteMediaClient?.let { remoteMediaClient ->
            remoteMediaClient.load(
                MediaLoadRequestData.Builder()
                    .setMediaInfo(buildMediaInfo(url, isMovie = false)).build()
            )
        }
    }

    private fun buildMediaInfo(url: String, isMovie: Boolean): MediaInfo? {
        val streamType = if (isMovie) MediaInfo.STREAM_TYPE_BUFFERED else MediaInfo.STREAM_TYPE_NONE
        val contentType = if (isMovie) "video/mp4" else "image/png"
        return MediaInfo.Builder(url)
            .setStreamType(streamType)
            .setContentType(contentType)
            .setMetadata(MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO).apply {
                putString(MediaMetadata.KEY_TITLE, url)
                addImage(WebImage(Uri.parse(url)))
            })
            .build()
    }

    private fun setupCastListener() {
        mSessionManagerListener = object : EmptySessionManagerListener() {
            override fun onSessionEnded(session: CastSession, error: Int) {
                onApplicationDisconnected()
            }

            override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
                onApplicationConnected(session)
            }

            override fun onSessionResumeFailed(session: CastSession, error: Int) {
                onApplicationDisconnected()
            }

            override fun onSessionStarted(session: CastSession, sessionId: String) {
                onApplicationConnected(session)
            }

            override fun onSessionStartFailed(session: CastSession, error: Int) {
                onApplicationDisconnected()
            }

            private fun onApplicationConnected(castSession: CastSession) {
                mCastSession = castSession
                castImage(images[currentPosition])
                supportInvalidateOptionsMenu()
            }

            private fun onApplicationDisconnected() {
                supportInvalidateOptionsMenu()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.browse, menu)
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(
            applicationContext,
            menu,
            R.id.media_route_menu_item
        )
        return true
    }
}