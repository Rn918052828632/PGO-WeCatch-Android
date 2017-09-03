package com.kennah.wecatch.module.main.ui.activity

import android.Manifest
import android.app.Service
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.widget.FrameLayout
import butterknife.BindString
import butterknife.BindView
import com.kennah.wecatch.R
import com.kennah.wecatch.core.base.BaseActivity
import com.kennah.wecatch.module.main.service.MainService
import com.kennah.wecatch.module.main.ui.fragment.MainFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasServiceInjector
import dagger.android.support.HasSupportFragmentInjector
import permissions.dispatcher.RuntimePermissions
import javax.inject.Inject
import permissions.dispatcher.NeedsPermission
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.OnPermissionDenied

@RuntimePermissions
class MainActivity : BaseActivity(), HasSupportFragmentInjector, HasServiceInjector {

    @BindView(R.id.container)
    lateinit var mLayoutContainer: FrameLayout
    @BindString(R.string.permission_location_title)
    lateinit var mStringLocationTitle: String
    @BindString(R.string.permission_location_message)
    lateinit var mStringLocationMessage: String

    @Inject
    lateinit var mFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var mServiceInjector: DispatchingAndroidInjector<Service>

    override fun afterViews() {
        super.afterViews()
        MainActivityPermissionsDispatcher.showLocationWithCheck(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        MainActivityPermissionsDispatcher.onActivityResult(this, requestCode)
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showLocation() {
        setupFragment()
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showDeniedForLocation() {
        setupFragment()
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showRationaleForLocation(request: PermissionRequest) {

        alert(mStringLocationMessage, mStringLocationTitle) {
            yesButton { request.proceed() }
            noButton { request.cancel() }
        }.show()
    }

    @NeedsPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    fun requestAlertWindows() {
        startService(Intent(this, MainService::class.java))
        finish()
    }

    @OnPermissionDenied(Manifest.permission.SYSTEM_ALERT_WINDOW)
    fun showDeniedForAlertWindows() {
        toast(R.string.permission_not_granted)
    }

    fun checkPermission() {
        MainActivityPermissionsDispatcher.requestAlertWindowsWithCheck(this)
    }

    private fun setupFragment() {

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment(), "MainFragment")
                .addToBackStack(null)
                .commit()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = mFragmentInjector

    override fun serviceInjector(): AndroidInjector<Service> = mServiceInjector

    override fun layout(): Int = R.layout.activity_main

    override fun toolbar(): Toolbar? = null

    override fun title(): String {
        return resources.getString(R.string.app_name)
    }

}
