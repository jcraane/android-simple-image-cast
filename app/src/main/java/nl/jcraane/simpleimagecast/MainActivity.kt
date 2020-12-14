package nl.jcraane.simpleimagecast

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.IntroductoryOverlay.OnOverlayDismissedListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mIntroductoryOverlay: IntroductoryOverlay? = null
    private var mCastStateListener: CastStateListener? = null
    private var mediaRouteMenuItem: MenuItem? = null
    private var mCastContext: CastContext? = null
    private var mCastSession: CastSession? = null
    private var mSessionManagerListener: SessionManagerListener<CastSession>? = null

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
        castTwo.setOnClickListener {
            castImage("https://homepages.cae.wisc.edu/~ece533/images/arctichare.png")
        }
        castThree.setOnClickListener {
            castImage("https://homepages.cae.wisc.edu/~ece533/images/baboon.png")
        }
    }

    override fun onResume() {
        mCastContext!!.sessionManager.addSessionManagerListener(mSessionManagerListener, CastSession::class.java)
        mCastContext!!.addCastStateListener(mCastStateListener)
        super.onResume()
    }

    override fun onPause() {
        mCastContext!!.removeCastStateListener(mCastStateListener)
        mCastContext!!.sessionManager.removeSessionManagerListener(mSessionManagerListener, CastSession::class.java)
        super.onPause()
    }

    private fun showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay?.remove()
        }
        if (mediaRouteMenuItem != null && mediaRouteMenuItem?.isVisible() == true) {
            Handler().post {
                mIntroductoryOverlay = IntroductoryOverlay.Builder(
                        this@MainActivity, mediaRouteMenuItem)
                        .setTitleText("Introducing Cast")
                        .setSingleTime()
                        .setOnOverlayDismissedListener(
                                OnOverlayDismissedListener { mIntroductoryOverlay = null })
                        .build()
                mIntroductoryOverlay?.show()
            }
        }
    }

    private fun castMovie(url: String) {
        mCastSession?.remoteMediaClient?.let { remoteMediaClient ->
            remoteMediaClient.load(MediaLoadRequestData.Builder()
                    .setMediaInfo(buildMediaInfo(url, isMovie = true)).build())
        }
    }

    private fun castImage(url: String) {
        mCastSession?.remoteMediaClient?.let { remoteMediaClient ->
            remoteMediaClient.load(MediaLoadRequestData.Builder()
                    .setMediaInfo(buildMediaInfo(url, isMovie = false)).build())
        }
    }

    private fun buildMediaInfo(url: String, isMovie: Boolean): MediaInfo? {
        val streamType = if (isMovie) MediaInfo.STREAM_TYPE_BUFFERED else MediaInfo.STREAM_TYPE_NONE
        val contentType = if (isMovie) "video/mp4" else "image/png"
        val mediaMetaData = if (isMovie) MediaMetadata.MEDIA_TYPE_MOVIE else MediaMetadata.MEDIA_TYPE_PHOTO
        return MediaInfo.Builder(url)
                .setStreamType(streamType)
                .setContentType(contentType)
//                .setMetadata(MediaMetadata(mediaMetaData))
                .build()
    }

    private fun setupCastListener() {
        mSessionManagerListener = object : SessionManagerListener<CastSession> {
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

            override fun onSessionStarting(session: CastSession) {}
            override fun onSessionEnding(session: CastSession) {}
            override fun onSessionResuming(session: CastSession, sessionId: String) {}
            override fun onSessionSuspended(session: CastSession, reason: Int) {}
            private fun onApplicationConnected(castSession: CastSession) {
                mCastSession = castSession
                /*if (null != mSelectedMedia) {
                    if (mPlaybackState == PlaybackState.PLAYING) {
                        mVideoView.pause()
                        loadRemoteMedia(mSeekbar.getProgress(), true)
                        return
                    } else {
                        mPlaybackState = PlaybackState.IDLE
                        updatePlaybackLocation(com.google.sample.cast.refplayer.mediaplayer.LocalPlayerActivity.PlaybackLocation.REMOTE)
                    }
                }
                updatePlayButton(mPlaybackState)*/
                supportInvalidateOptionsMenu()
            }

            private fun onApplicationDisconnected() {
                /*updatePlaybackLocation(com.google.sample.cast.refplayer.mediaplayer.LocalPlayerActivity.PlaybackLocation.LOCAL)
                mPlaybackState = PlaybackState.IDLE
                mLocation = com.google.sample.cast.refplayer.mediaplayer.LocalPlayerActivity.PlaybackLocation.LOCAL
                updatePlayButton(mPlaybackState)*/
                supportInvalidateOptionsMenu()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.browse, menu)
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(applicationContext, menu, R.id.media_route_menu_item)
        return true
    }
}