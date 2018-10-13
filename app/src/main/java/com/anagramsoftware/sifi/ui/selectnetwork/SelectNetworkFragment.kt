package com.anagramsoftware.sifi.ui.selectnetwork

import androidx.lifecycle.Observer
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.`interface`.ItemClickListener
import com.anagramsoftware.sifi.extension.fadeIn
import com.anagramsoftware.sifi.extension.fadeOut
import com.anagramsoftware.sifi.extension.getService
import com.anagramsoftware.sifi.recyclerview.MarginItemDecorator
import com.cantrowitz.rxbroadcast.RxBroadcast
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_select_network.*
import kotlinx.android.synthetic.main.fragment_select_network.view.*
import org.koin.android.ext.android.inject

/**
 * A [Fragment] to view the scanned Sifi networks.
 *
 */
class SelectNetworkFragment : androidx.fragment.app.Fragment() {

    private val viewModel: SelectNetworkViewModel by inject()

    private lateinit var adapter: ResultAdapter
    private var currentLayout: Int = Layout.FAILED

    private var wifiScanDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_network, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            toolbar.title = context.getString(R.string.info_select_network)
            toolbar.inflateMenu(R.menu.select_network_menu)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_rescan -> {
                        startScan()
                        true
                    }
                    else -> false
                }
            }

            adapter = ResultAdapter()
            adapter.listener = object : ItemClickListener {
                override fun onItemClick(position: Int) {
                    // TODO -REmove mock
                    if (getService()?.connectMock(adapter.getItem(position)) == true) {
                        view.findNavController().navigateUp()
                    }
                }

                override fun onItemLongClick(position: Int) {
                }
            }
            result_rv.adapter = adapter
            result_rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            result_rv.addItemDecoration(MarginItemDecorator(resources.getDimensionPixelSize(R.dimen.card_margin)))
            result_rv.setHasFixedSize(true)
            ViewCompat.setElevation(toolbar, 0.0f)
            result_rv.addOnScrollListener( object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (result_rv.canScrollVertically(-1)) {
                        ViewCompat.setElevation(appbar, 50.0f)
                    } else {
                        ViewCompat.setElevation(appbar, 0.0f)
                    }
                }
            })

            rescan_ib.setOnClickListener{
                Log.d(TAG, "Rescan started")
                startScan()
            }

            action_cancel.setOnClickListener{
                stopScan()
                view.findNavController().navigateUp()
            }

            // Hide unused layouts
            appbar.visibility = View.INVISIBLE
            result_rv.visibility = View.INVISIBLE
            rescan_ib.visibility = View.INVISIBLE
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.apply {
            hotspots.observe(this@SelectNetworkFragment, Observer {
                it?.let { it1 ->
                    if (it.isNotEmpty()) {
                        adapter.accept(it1)
                        showSuccess()
                    } else {
                        showFailed()
                    }

                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()
        startScan()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stop()
        stopScan()
    }

    private fun startScan() {
        stopScan()
        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        wifiScanDisposable = RxBroadcast.fromBroadcast(context, intentFilter)
                .firstOrError()
                .subscribe({
                    Log.d(TAG, "OnScanResult")
                    // TODO - Remove mock
                    getService()?.getWifiSCanResultMock()?.let {
                        viewModel.getResults(it)
                    }
                },{})
        getService()?.startWifiScan()
        showScanning()
    }

    private fun stopScan() {
        wifiScanDisposable?.let {
            if (!it.isDisposed)
                it.dispose()
        }
        wifiScanDisposable = null
    }

    private fun showScanning() {
        Log.d(TAG, "showScanning $currentLayout")
        if (currentLayout != Layout.SCANNING) {
            scanning_ll.fadeIn()
            appbar.fadeOut()
            result_rv.fadeOut()
            rescan_ib.fadeOut()
            currentLayout = Layout.SCANNING
        }
    }

    private fun showFailed() {
        Log.d(TAG, "showFailed $currentLayout")
        if (currentLayout != Layout.FAILED) {
            scanning_ll.fadeOut()
            appbar.fadeOut()
            result_rv.fadeOut()
            rescan_ib.fadeIn()
            currentLayout = Layout.FAILED
        }
    }

    private fun showSuccess() {
        Log.d(TAG, "showSuccess $currentLayout")
        if (currentLayout != Layout.SUCCESS) {
            scanning_ll.fadeOut()
            appbar.fadeIn()
            result_rv.fadeIn()
            rescan_ib.fadeOut()
            currentLayout = Layout.SUCCESS
        }
    }

    private object Layout {
        const val SCANNING = 0
        const val FAILED = 1
        const val SUCCESS = 2
    }

    companion object {
        private const val TAG = "SelectNetworkFragment"
    }
}
