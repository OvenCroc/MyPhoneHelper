package com.example.myphonehelper.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.view.accessibility.AccessibilityManager

/**
 * description: 权限工具类
 * Created by xm zhoupan on 2019/4/4.
 */
object PermissionUtils {
    /**
     * 是否开启了悬浮窗权限
     *
     * @author zhoupan
     * Created at 2019/4/4 11:04
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isCanDrawOnOtherApp(ctx: Context): Boolean {
        return Settings.canDrawOverlays(ctx)
    }

    /**
     * 是否开启了辅助功能权限
     *
     * @author zhoupan
     * Created at 2019/4/4 11:05
     */
    fun isAccessibilityServiceEnable(ctx: Context): Boolean {
        val accessibilityManager: AccessibilityManager =
            (ctx.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager) ?: return false
        //获取所有获取了辅助功能app的列表
        var appList =
            accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        appList?.forEach {
            if (it.id.contains(ctx.packageName)) {
                return true
            }
        }
        return false
    }
}