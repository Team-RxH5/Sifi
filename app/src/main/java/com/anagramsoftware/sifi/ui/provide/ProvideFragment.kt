package com.anagramsoftware.sifi.ui.provide

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.extension.getService
import org.koin.android.ext.android.inject

class ProvideFragment : Fragment() {

    private val viewModel: ProvideViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_provide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view) {
            findViewById<FloatingActionButton>(R.id.action_start).setOnClickListener{
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
