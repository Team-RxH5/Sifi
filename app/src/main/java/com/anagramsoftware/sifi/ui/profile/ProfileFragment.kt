package com.anagramsoftware.sifi.ui.profile

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.transition.Fade

import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.ui.MainActivity
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.android.ext.android.inject

class ProfileFragment : Fragment(), AppBarLayout.OnOffsetChangedListener {

    init {
        enterTransition = Fade()
        exitTransition = Fade()
    }

    private val viewModel: ProfileViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.profile_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    (activity as MainActivity?)?.logout()
                    true
                }
                else -> false
            }
        }

        appbar.addOnOffsetChangedListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.start()
        viewModel.apply {
            user.observe(this@ProfileFragment, Observer {
                name_tv.text = it?.fname + " " + it?.lname
                tag_tv.text = if (it?.tag != null) "#${it.tag}" else "#TAG"
            })
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout?.let {
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = Math.abs(verticalOffset) / maxScroll.toFloat()

            handleAlphaOnTitle(percentage)
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        name_tv.alpha = (PERCENTAGE_TO_HIDE_TITLE_DETAILS - Math.min(PERCENTAGE_TO_HIDE_TITLE_DETAILS, percentage))/ PERCENTAGE_TO_HIDE_TITLE_DETAILS
    }

    companion object {
        private const val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.5f
    }

}
