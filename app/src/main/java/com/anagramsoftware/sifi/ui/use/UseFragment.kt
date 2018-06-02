package com.anagramsoftware.sifi.ui.use

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.extension.getService
import org.koin.android.ext.android.inject

/**
 * A [Fragment] to implement use functionality.
 *
 */
class UseFragment : Fragment() {

    companion object {
        private const val TAG = "UseFragment"
    }

    private val viewModel: UseViewModel by inject()

    private lateinit var trafficTV: TextView
    private lateinit var actionScan: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_use, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            trafficTV = findViewById(R.id.traffic_tv)

            actionScan = findViewById(R.id.action_scan)
            actionScan.setOnClickListener{
                view.findNavController().navigate(R.id.action_use_fragment_to_select_network_fragment)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getService()?.traffic?.observe(this, Observer {
            it?.let {
                if (trafficTV.visibility == View.INVISIBLE)
                    showTraffic()
                trafficTV.text = "Sent: ${it.sent}, Received: ${it.received}"
            }
        })
    }

    private fun showTraffic() {
        trafficTV.visibility = View.VISIBLE
        actionScan.visibility = View.INVISIBLE
    }
}
