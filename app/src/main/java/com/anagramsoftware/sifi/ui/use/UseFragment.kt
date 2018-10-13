package com.anagramsoftware.sifi.ui.use

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.transition.Fade
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.extension.getService
import kotlinx.android.synthetic.main.fragment_use.*
import org.koin.android.ext.android.inject

/**
 * A [Fragment] to implement use functionality.
 *
 */
class UseFragment : androidx.fragment.app.Fragment() {

    companion object {
        private const val TAG = "UseFragment"
    }

    init {
        enterTransition = Fade()
        exitTransition = Fade()
    }

    private val viewModel: UseViewModel by inject()

    private lateinit var actionScan: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_use, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            actionScan = findViewById(R.id.action_scan)
            actionScan.setOnClickListener{
                getService()?.let {
                    if (it.isConnected()) {
                        it.disconnectMock()
                        checkScanAvailable()
                    } else {
                        view.findNavController().navigate(R.id.action_use_fragment_to_select_network_fragment)
                    }
                }
            }
            hideTraffic()
            checkScanAvailable()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getService()?.traffic?.observe(this, Observer {
            if (it != null){
                if (traffic.visibility == View.INVISIBLE)
                    showTraffic()
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
            } else {
                if (traffic.visibility == View.VISIBLE)
                    hideTraffic()
            }
        })
    }

    private fun showTraffic() {
        traffic.visibility = View.VISIBLE
//        actionScan.visibility = View.INVISIBLE
    }

    private fun hideTraffic() {
        traffic.visibility = View.INVISIBLE
//        actionScan.visibility = View.VISIBLE
    }

    private fun checkScanAvailable() {
        getService()?.let {
            if (it.isConnected())
                actionScan.setImageResource(R.drawable.ic_close_white_24dp)
            else
                actionScan.setImageResource(R.drawable.ic_search_white_24dp)
        }
    }
}
