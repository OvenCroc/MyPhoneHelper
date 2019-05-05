package com.example.myphonehelper.callback

/**
 * description: MyPhoneHelper
 * Created by xm zhoupan on 2019/4/17.
 */
interface GestureCallBack {
    fun onHorizenFlipCallback()
    /**
     * @param isTopToBottom 垂直滚动的方案,从上到下还是从下到上
     *
     * @author zhoupan
     * Created at 2019/4/29 16:29
     */
    fun onVerticalFlipCallback(isTopToBottom: Boolean)
}