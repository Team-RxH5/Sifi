package com.anagramsoftware.sifi.ui.provide

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.extension.getService
import kotlinx.android.synthetic.main.fragment_provide.view.*
import org.koin.android.ext.android.inject


class ProvideFragment : Fragment() {

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
            action_start.setOnClickListener{
                getService()?.let {
                    if (it.isHotspotActive()) {
                        it.stopProviding()
                    } else {
                        it.startProviding()
                    }
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ProvideViewModel
    }

}
