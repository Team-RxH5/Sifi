package com.anagramsoftware.sifi.ui.provide

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.transition.Fade
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.extension.fadeIn
import com.anagramsoftware.sifi.extension.fadeOut
import com.anagramsoftware.sifi.extension.getService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_provide.*
import kotlinx.android.synthetic.main.fragment_provide.view.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit


class ProvideFragment : androidx.fragment.app.Fragment() {

    init {
        enterTransition = Fade()
        exitTransition = Fade()
    }

    private val viewModel: ProvideViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_provide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get write permissions in M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this.context?.applicationContext)) {

            } else {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + this.context?.packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        with(view) {
            toolbar.title = context.getString(R.string.info_connected_devices)
            toolbar.inflateMenu(R.menu.provide_menu)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_stop -> {
                        getService()?.stopProvidingMock()
                        hideProviding()
                        hideTraffic()
                        true
                    }
                    else -> false
                }
            }

            action_start.setOnClickListener{
                getService()?.let {
                    showProviding()
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getService()?.traffic?.observe(this, Observer {
            if (it != null){
                val sent = when {
                    it.sent > 1024 -> "${it.sent / 1024} KB"
                    it.sent > 1024 * 1024 -> "${it.sent / (1024 * 1024)} MB"
                    else -> "${it.sent} B"
                }
                traffic_sent_tv.text = sent
                val received = when {
                    it.received > 1024 -> "${it.received / 1024} KB"
                    it.received > 1024 * 1024 -> "${it.received / (1024 * 1024)} MB"
                    else -> "${it.received} B"
                }
                traffic_recieved_tv.text = received
            }
        })
    }

    private fun showTraffic() {
        traffic.fadeIn()
    }

    private fun hideTraffic() {
        traffic.fadeOut()
    }

    private fun showProviding() {
        appbar.fadeIn()
        providing.fadeIn()
        action_start.fadeOut()
        Observable.interval(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .firstOrError()
                .subscribe({
                    getService()?.startProvidingMock()
                    showTraffic()
                }, {})
    }

    private fun hideProviding() {
        appbar.fadeOut()
        providing.fadeOut()
        action_start.fadeIn()
    }

}
