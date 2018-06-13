package com.anagramsoftware.sifi.ui.auth.createuser

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.anagramsoftware.sifi.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_create_user.view.*
import org.koin.android.ext.android.inject

class CreateUserFragment : Fragment() {

    private val viewModel: CreateUserViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_create_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        view.apply {
            action_done.setOnClickListener {
                Log.d(TAG, "actionDone")
                viewModel.createUser(fname_et.text.toString(), lname_et.text.toString(), tag_et.text.toString())
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.apply {
            createUserCompleteEvent.observe(this@CreateUserFragment, Observer {
                it?.let {
                    if (it) {
                        Log.d(TAG, "createUserCompleteEvent Success")
                        view?.findNavController()?.navigate(R.id.main_activity)
                        activity?.finish()
                    } else {
                        Log.d(TAG, "createUserCompleteEvent Failed")
                        FirebaseAuth.getInstance().signOut()
                        view?.findNavController()?.navigateUp()
                    }
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stop()
    }

    companion object {
        private const val TAG = "CreateUserFragment"
    }

}
