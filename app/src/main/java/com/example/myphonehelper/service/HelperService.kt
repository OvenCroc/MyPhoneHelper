package com.example.myphonehelper.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import com.example.myphonehelper.R
import com.example.myphonehelper.activity.toast
import com.example.myphonehelper.callback.GestureCallBack
import kotlinx.android.synthetic.main.slide_bottom_view_layout.view.*
import kotlinx.android.synthetic.main.slide_view_layout.view.*


/**
 * description: MyPhoneHelper
 * Created by xm zhoupan on 2019/4/4.
 */
class HelperService : AccessibilityService(), View.OnTouchListener {
    var windowManager: WindowManager? = null
    var gestureCallBack: GestureCallBack = object : GestureCallBack {
        override fun onHorizenFlipCallback() {
            doFinishActivity()
        }

        override fun onVerticalFlipCallback(isTopToBottom: Boolean) {
            if (isTopToBottom) {
                doGoHome()
            } else {
                doShowCurrentApp()
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun doShowCurrentApp() {
        this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
    }

    /**
     * 会首页
     *
     * @author zhoupan
     * Created at 2019/4/19 11:29
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun doGoHome() {
        this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
    }

    /**
     * 返回activity
     *
     * @author zhoupan
     * Created at 2019/4/17 11:19
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun doFinishActivity() {
        this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }

    /**
     * 开始滑动的时候的初始位置X坐标
     */
    private var startX: Float = 0f
    /**
     * 开始滑动的时候的初始位置Y坐标
     */
    private var startY: Float = 0f
    /**
     * 滑动类型 0是横向滑动 1竖向滑动
     */
    private var scrollType: Int = 0

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when (v?.id) {
            R.id.controllerHorizenTv -> {//滑动事件处理左边划过来
                handleBehavior(event)
                return true
            }
            R.id.controllerVerticalTv -> {//底部滑上来
                handleBehavior(event)
                return true

            }
        }
        return false//默认是不处理的
    }

    /**
     * 根据event来判断当前是怎么滑动的
     *
     * @author zhoupan
     * Created at 2019/4/4 16:12
     */
    private fun handleBehavior(event: MotionEvent?) {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {//当前是按下
                //记录初始的xy坐标
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_MOVE -> {//手指开始移动
                val moveX = Math.abs(event.x - startX)
                val moveY = Math.abs(event.y - startY)
                val isHome = event.y > startY //上滑
                if (moveX > moveY) {//当前横向滑动的距离比较大
                    Log.i("zp", "横向移动")
                    scrollType = 0
                } else {//竖向滑动的距离比较大
                    Log.i("zp", "竖向移动")
                    scrollType = 1
                }
            }
            MotionEvent.ACTION_UP -> {//手机抬起来的时候,清空信息
                if (scrollType == 0) {
                    gestureCallBack?.onHorizenFlipCallback()
                } else {
                    gestureCallBack?.onVerticalFlipCallback(event.y > startY)
                }
                startX = 0f
                startY = 0f
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        toast("service start")
        addViewToWindowManager()
    }


    /**
     * 添加view到windowmananger上
     *
     * @author zhoupan
     * Created at 2019/4/4 15:55
     */
    private fun addViewToWindowManager() {
        if (windowManager == null) {
            windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        }
        var leftView = createAddView(0)
        var leftParam = initLayoutParam(Gravity.LEFT)
        var rightView = createAddView(1)
        var rightParam = initLayoutParam(Gravity.RIGHT)
        var bottomView = createAddView(2)
        var bottomParam = initLayoutParam(Gravity.BOTTOM)
        windowManager?.addView(leftView, leftParam)
        windowManager?.addView(rightView, rightParam)
//        windowManager?.addView(bottomView, bottomParam)
    }

    private fun createAddView(type: Int): View? {
        var view: View? = null
        when (type) {
            0, 1 -> { //左边,右边
                view = LayoutInflater.from(this).inflate(R.layout.slide_view_layout, null)
                view?.controllerHorizenTv?.setOnTouchListener(this)
            }
            2 -> {//底部
                view = LayoutInflater.from(this).inflate(R.layout.slide_bottom_view_layout, null)
                view?.controllerVerticalTv?.setOnTouchListener(this)
            }
        }
        return view
    }

    /**
     * 初始化添加view的layoutparam
     *
     * @author zhoupan
     * Created at 2019/4/4 16:03
     */
    private fun initLayoutParam(gravity: Int): WindowManager.LayoutParams {
//        mParams = WindowManager.LayoutParams()
//        // compatible
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//        } else {
//            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
//        }
//        // set bg transparent
//        mParams.format = PixelFormat.TRANSPARENT
//        // can not focusable
//        mParams.flags =
//            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//        mParams.gravity = Gravity.BOTTOM or Gravity.LEFT
//        mParams.x = 0
//        mParams.y = 0
//        // window size
//        mParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
//        mParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

        var params: WindowManager.LayoutParams = WindowManager.LayoutParams()
        // compatible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        // set bg transparent
        params.format = PixelFormat.RGBA_8888
        // can not focusable
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.x = 0
        params.y = 0
        // window size
        params.gravity = gravity
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        return params
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }
}