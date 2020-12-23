package com.serenegiant.usb.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.serenegiant.glutils.EGLBase;
import com.serenegiant.glutils.GLDrawer2D;
import com.serenegiant.glutils.es1.GLHelper;
import com.serenegiant.usb.encoder.IVideoEncoder;
import com.serenegiant.usb.encoder.MediaEncoder;
import com.serenegiant.usb.encoder.MediaVideoEncoder;
import com.serenegiant.utils.FpsCounter;

/**
 * @author: lico
 * @Desc:
 */
public class UVCTextureView extends AspectRatioTextureView implements
        TextureView.SurfaceTextureListener, CameraViewInterface {

    private int mWidth, mHeight;
    private boolean mHasSurface;
    private CameraViewInterface.Callback mCallback;
    private UVCTextureView.RenderHandler mRenderHandler;
    private Surface mPreviewSurface;
    private final FpsCounter mFpsCounter = new FpsCounter();

    public UVCTextureView(Context context) {
        this(context, null, 0);
    }

    public UVCTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UVCTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {
        Log.e("zzkong", "onResume: " + mHasSurface);
        if (mHasSurface) {
            mRenderHandler = UVCTextureView.RenderHandler.createHandler(mFpsCounter, super.getSurfaceTexture(), getWidth(), getHeight());
        }
    }

    @Override
    public void setCallback(final CameraViewInterface.Callback callback) {
        mCallback = callback;
    }

    @Override
    public Surface getSurface() {
        return null;
    }

    @Override
    public boolean hasSurface() {
        return false;
    }

    @Override
    public void setVideoEncoder(IVideoEncoder encoder) {

    }

    @Override
    public Bitmap captureStillImage(int width, int height) {
        return null;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i("zzkong", "onSurfaceTextureAvailable : " + mRenderHandler);
        this.mWidth = width;
        this.mHeight = height;

        if (mRenderHandler == null) {
            mRenderHandler = RenderHandler.createHandler(mFpsCounter, surface, width, height);
        } else {
            mRenderHandler.resize(width, height);
        }
        mHasSurface = true;
        if (mCallback != null) {
            mCallback.onSurfaceCreated(this, getSurface());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {


        if (mRenderHandler != null) {
            mRenderHandler.release();
            mRenderHandler = null;
        }

        mHasSurface = false;
        Log.i("zzkong", "onSurfaceTextureDestroyed: " + mHasSurface);
        if (mCallback != null) {
            mCallback.onSurfaceDestroy(this, getSurface());
        }

        if (mPreviewSurface != null) {
            mPreviewSurface.release();
            mPreviewSurface = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mRenderHandler != null ? mRenderHandler.getPreviewTexture() : null;
    }

    private static final class RenderHandler extends Handler
            implements SurfaceTexture.OnFrameAvailableListener  {

        private static final int MSG_REQUEST_RENDER = 1;
        private static final int MSG_SET_ENCODER = 2;
        private static final int MSG_CREATE_SURFACE = 3;
        private static final int MSG_RESIZE = 4;
        private static final int MSG_TERMINATE = 9;

        private UVCTextureView.RenderHandler.RenderThread mThread;
        private boolean mIsActive = true;
        private final FpsCounter mFpsCounter;

        public static final UVCTextureView.RenderHandler createHandler(final FpsCounter counter,
                                                                             final SurfaceTexture surface, final int width, final int height) {

            final UVCTextureView.RenderHandler.RenderThread thread = new UVCTextureView.RenderHandler.RenderThread(counter, surface, width, height);
            thread.start();
            return thread.getHandler();
        }

        private RenderHandler(final FpsCounter counter, final UVCTextureView.RenderHandler.RenderThread thread) {
            mThread = thread;
            mFpsCounter = counter;
        }

        public final void setVideoEncoder(final IVideoEncoder encoder) {
            if (mIsActive){
                sendMessage(obtainMessage(MSG_SET_ENCODER, encoder));
            }
        }

        public final SurfaceTexture getPreviewTexture() {
            if (mIsActive) {
                synchronized (mThread.mSync) {
                    sendEmptyMessage(MSG_CREATE_SURFACE);
                    try {
                        mThread.mSync.wait();
                    } catch (final InterruptedException e) {
                    }
                    return mThread.mPreviewSurface;
                }
            } else {
                return null;
            }
        }

        public void resize(final int width, final int height) {
            if (mIsActive) {
                synchronized (mThread.mSync) {
                    sendMessage(obtainMessage(MSG_RESIZE, width, height));
                    try {
                        mThread.mSync.wait();
                    } catch (final InterruptedException e) {
                    }
                }
            }
        }

        public final void release() {
            if (mIsActive) {
                mIsActive = false;
                removeMessages(MSG_REQUEST_RENDER);
                removeMessages(MSG_SET_ENCODER);
                sendEmptyMessage(MSG_TERMINATE);
            }
        }

        @Override
        public final void onFrameAvailable(final SurfaceTexture surfaceTexture) {
            if (mIsActive) {
                mFpsCounter.count();
                sendEmptyMessage(MSG_REQUEST_RENDER);
            }
        }

        @Override
        public final void handleMessage(final Message msg) {
            if (mThread == null){
                return;
            }
            switch (msg.what) {
                case MSG_REQUEST_RENDER:
                    mThread.onDrawFrame();
                    break;
                case MSG_SET_ENCODER:
                    mThread.setEncoder((MediaEncoder)msg.obj);
                    break;
                case MSG_CREATE_SURFACE:
                    mThread.updatePreviewSurface();
                    break;
                case MSG_RESIZE:
                    mThread.resize(msg.arg1, msg.arg2);
                    break;
                case MSG_TERMINATE:
                    Looper.myLooper().quit();
                    mThread = null;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

        private static final class RenderThread extends Thread {
            private final Object mSync = new Object();
            private final SurfaceTexture mSurface;
            private UVCTextureView.RenderHandler mHandler;
            private EGLBase mEgl;
            /** IEglSurface instance related to this TextureView */
            private EGLBase.IEglSurface mEglSurface;
            private GLDrawer2D mDrawer;
            private int mTexId = -1;
            /** SurfaceTexture instance to receive video images */
            private SurfaceTexture mPreviewSurface;
            private final float[] mStMatrix = new float[16];
            private MediaEncoder mEncoder;
            private int mViewWidth, mViewHeight;
            private final FpsCounter mFpsCounter;

            /**
             * constructor
             * @param surface: drawing surface came from TexureView
             */
            public RenderThread(final FpsCounter fpsCounter, final SurfaceTexture surface, final int width, final int height) {
                mFpsCounter = fpsCounter;
                mSurface = surface;
                mViewWidth = width;
                mViewHeight = height;
                setName("RenderThread");
            }

            public final UVCTextureView.RenderHandler getHandler() {
                synchronized (mSync) {
                    // create rendering thread
                    if (mHandler == null){
                        try {
                            mSync.wait();
                        } catch (final InterruptedException e) {
                        }
                    }
                }
                return mHandler;
            }

            public void resize(final int width, final int height) {
                if (((width > 0) && (width != mViewWidth)) || ((height > 0) && (height != mViewHeight))) {
                    mViewWidth = width;
                    mViewHeight = height;
                    updatePreviewSurface();
                } else {
                    synchronized (mSync) {
                        mSync.notifyAll();
                    }
                }
            }

            public final void updatePreviewSurface() {
                synchronized (mSync) {
                    if (mPreviewSurface != null) {
                        mPreviewSurface.setOnFrameAvailableListener(null);
                        mPreviewSurface.release();
                        mPreviewSurface = null;
                    }
                    mEglSurface.makeCurrent();
                    if (mTexId >= 0) {
                        mDrawer.deleteTex(mTexId);
                    }
                    // create texture and SurfaceTexture for input from camera
                    mTexId = mDrawer.initTex();
                    mPreviewSurface = new SurfaceTexture(mTexId);
                    mPreviewSurface.setDefaultBufferSize(mViewWidth, mViewHeight);
                    mPreviewSurface.setOnFrameAvailableListener(mHandler);
                    // notify to caller thread that previewSurface is ready
                    mSync.notifyAll();
                }
            }

            public final void setEncoder(final MediaEncoder encoder) {
                if (encoder != null && (encoder instanceof MediaVideoEncoder)) {
                    ((MediaVideoEncoder)encoder).setEglContext(mEglSurface.getContext(), mTexId);
                }
                mEncoder = encoder;
            }

            /*
             * Now you can get frame data as ByteBuffer(as YUV/RGB565/RGBX/NV21 pixel format) using IFrameCallback interface
             * with UVCCamera#setFrameCallback instead of using following code samples.
             */
/*			// for part1
 			private static final int BUF_NUM = 1;
			private static final int BUF_STRIDE = 640 * 480;
			private static final int BUF_SIZE = BUF_STRIDE * BUF_NUM;
			int cnt = 0;
			int offset = 0;
			final int pixels[] = new int[BUF_SIZE];
			final IntBuffer buffer = IntBuffer.wrap(pixels); */
/*			// for part2
			private ByteBuffer buf = ByteBuffer.allocateDirect(640 * 480 * 4);
 */
            /**
             * draw a frame (and request to draw for video capturing if it is necessary)
             */
            public final void onDrawFrame() {
                mEglSurface.makeCurrent();
                // update texture(came from camera)
                mPreviewSurface.updateTexImage();
                // get texture matrix
                mPreviewSurface.getTransformMatrix(mStMatrix);
                // notify video encoder if it exist
                if (mEncoder != null) {
                    // notify to capturing thread that the camera frame is available.
                    if (mEncoder instanceof MediaVideoEncoder){
                        ((MediaVideoEncoder)mEncoder).frameAvailableSoon(mStMatrix);
                    }else {
                        mEncoder.frameAvailableSoon();
                    }
                }
                // draw to preview screen
                mDrawer.draw(mTexId, mStMatrix, 0);
                mEglSurface.swap();

            }

            @Override
            public final void run() {
                Log.e("zzkong", "run: ----------------------");
                init();
                Looper.prepare();
                synchronized (mSync) {
                    mHandler = new UVCTextureView.RenderHandler(mFpsCounter, this);
                    mSync.notify();
                }

                Looper.loop();

                release();
                synchronized (mSync) {
                    mHandler = null;
                    mSync.notify();
                }
            }

            private final void init() {
                // create EGLContext for this thread
                mEgl = EGLBase.createFrom(null, false, false);
                mEglSurface = mEgl.createFromSurface(mSurface);
                mEglSurface.makeCurrent();
                // create drawing object
                mDrawer = new GLDrawer2D(true);
            }

            private final void release() {
                if (mDrawer != null) {
                    mDrawer.release();
                    mDrawer = null;
                }
                if (mPreviewSurface != null) {
                    mPreviewSurface.release();
                    mPreviewSurface = null;
                }
                if (mTexId >= 0) {
                    GLHelper.deleteTex(mTexId);
                    mTexId = -1;
                }
                if (mEglSurface != null) {
                    mEglSurface.release();
                    mEglSurface = null;
                }
                if (mEgl != null) {
                    mEgl.release();
                    mEgl = null;
                }
            }
        }
    }
}
