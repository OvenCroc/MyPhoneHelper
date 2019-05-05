package com.example.myphonehelper.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.myphonehelper.service.HelperService
import com.example.myphonehelper.utils.PermissionUtils
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    /**
     * 跳转系统悬浮窗权限界面请求码
     */
    private val FLAT_REQUEST_CODE: Int = 6666

    /**
     * 跳转系统辅助功能设置界面请求码
     */
    private val ACCESSIBILITY_REQUEST_CODE: Int = 8888
    private var isFirstIn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.myphonehelper.R.layout.activity_main)
        initView()
    }

    private fun initView() {
        getPermissionTv.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (isFirstIn) {//每次都要去点按钮，麻烦，直接进来就请求权限不行么
            getPermission()
            isFirstIn = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            com.example.myphonehelper.R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            getPermissionTv -> {//获取权限按钮
                getPermission()
            }
        }
    }

    /**
     * 获取悬浮窗和辅助功能权限
     *
     * @author zhoupan
     * Created at 2019/4/4 9:43
     */
    private fun getPermission() {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionUtils.isCanDrawOnOtherApp(this)
            } else {//如果版本默认比M小的话,悬浮窗权限好像默认是开启的,所以这里直接返回true
                true
            }
        ) {
            //判断是否已经开启了辅助功能权限
            if (PermissionUtils.isAccessibilityServiceEnable(this)) {
                startHelperService()
                finish()
            } else {
                //跳转开启辅助权限界面
                toast("来！请打开辅助功能权限")
                gotoOpenAccessibility()
            }
        } else {
            //跳转开启悬浮窗权限界面
            toast("来！请打开悬浮窗权限")
            gotoOpenDrawOnOtherAppPermission()
        }
    }

    /**
     * 启动服务
     *
     * @author zhoupan
     * Created at 2019/4/4 15:37
     */
    private fun startHelperService() {
        val intent = Intent(this, HelperService::class.java)
        startService(intent)
    }


    /**
     * 跳转开启辅助功能权限系统页面
     *
     * @author zhoupan
     * Created at 2019/4/4 11:15
     */
    private fun gotoOpenAccessibility() {
        val accessibleIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivityForResult(accessibleIntent, ACCESSIBILITY_REQUEST_CODE)
    }


    /**
     * 跳转到系统设置页面,开启悬浮窗权限
     *
     * @author zhoupan
     * Created at 2019/4/4 9:56
     */
    private fun gotoOpenDrawOnOtherAppPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        startActivityForResult(intent, FLAT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FLAT_REQUEST_CODE, ACCESSIBILITY_REQUEST_CODE -> {//从设置悬浮窗的界面回来了 , 从系统设置辅助功能页面回来了
                if (resultCode == Activity.RESULT_OK) {
                    toast("悬浮窗权限设置成功")
                }
                getPermission()
            }
        }
    }
}

//kotlin 的这种静态方法简直爽的1b。。。
fun Context.toast(str: String) {
    Toast.makeText(this, str, Toast.LENGTH_LONG).show()
}
