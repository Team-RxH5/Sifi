package com.anagramsoftware.sifi.ui.use

import android.arch.lifecycle.Observer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.`interface`.ItemClickListener
import com.anagramsoftware.sifi.extension.getService
import org.koin.android.ext.android.inject

class UseFragment : Fragment() {

    companion object {
        private const val TAG = "UseFragment"
    }

    private val viewModel: UseViewModel by inject()

    private lateinit var adapter: ResultAdapter
    private lateinit var resultRV: RecyclerView
    private lateinit var trafficTV: TextView
    private lateinit var actionScan: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_use, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            resultRV = findViewById(R.id.result_rv)
            adapter = ResultAdapter()
            adapter.listener = object : ItemClickListener {
                override fun onItemClick(position: Int) {
                    getService()?.connect(adapter.getItem(position))
                }

                override fun onItemLongClick(position: Int) {
                }
            }
            resultRV.adapter = adapter
            resultRV.layoutManager = LinearLayoutManager(context)
            resultRV.addItemDecoration(DividerItemDecoration(context,  DividerItemDecoration.VERTICAL))
            resultRV.setHasFixedSize(true)

            trafficTV = findViewById(R.id.traffic_tv)

            actionScan = findViewById(R.id.action_scan)
            actionScan.setOnClickListener{
                getService()?.startWifiScan()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.apply {
            hotspots.observe(this@UseFragment, Observer {
                it?.let { it1 ->
                    adapter.accept(it1)
                    showHotspots()
                }
            })
        }
        getService()?.traffic?.observe(this, Observer {
            it?.let {
                if (trafficTV.visibility == View.INVISIBLE)
                    showTraffic()
                trafficTV.text = "Sent: ${it.sent}, Received: ${it.received}"
            }
        })
    }

    override fun onStart() {
        super.onStart()
        activity?.apply {
            val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            registerReceiver(wifiScanResult, intentFilter)
        }
        viewModel.start()
    }

    override fun onStop() {
        super.onStop()
        viewModel.dispose()
        activity?.apply {
            unregisterReceiver(wifiScanResult)
        }
        viewModel.stop()
    }

    private val wifiScanResult = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getService()?.getWifiSCanResult()?.let {
                viewModel.onScanResult(it)
            }
        }
    }

    private fun showHotspots() {
        resultRV.visibility = View.VISIBLE
        trafficTV.visibility = View.INVISIBLE
        actionScan.visibility = View.INVISIBLE
    }

    private fun showScan() {
        trafficTV.visibility = View.INVISIBLE
        resultRV.visibility = View.INVISIBLE
        actionScan.visibility = View.VISIBLE
    }

    private fun showTraffic() {
        resultRV.visibility = View.INVISIBLE
        trafficTV.visibility = View.VISIBLE
        actionScan.visibility = View.INVISIBLE
    }
}
