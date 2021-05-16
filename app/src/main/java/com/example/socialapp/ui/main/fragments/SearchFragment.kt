package com.example.socialapp.ui.main.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.R
import com.example.socialapp.adapters.UserAdapter
import com.example.socialapp.other.EventObserver
import com.example.socialapp.other.SEARCH_TIME_DELAY
import com.example.socialapp.other.TAG
import com.example.socialapp.ui.main.dialogs.CommentDialogDirections
import com.example.socialapp.ui.main.viewmodels.SearchViewModel
import com.example.socialapp.ui.snackbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    @Inject
    lateinit var userAdapter: UserAdapter

    private val viewModel: SearchViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeToObservers()

        var job: Job? = null

        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    viewModel.searchUser(it.toString())
                }
            }
        }

        userAdapter.setOnUserClickListener { user ->

            if (FirebaseAuth.getInstance().uid!! == user.uid){

                requireActivity().bottomNavigationView.selectedItemId = R.id.profileFragment
            }else {
                findNavController().navigate(SearchFragmentDirections.globalActionToOthersProfileFragment(
                    user.uid))
            }



        }
    }

    private fun subscribeToObservers(){
        viewModel.searchResults.observe(viewLifecycleOwner,EventObserver(
            onError = {
                searchProgressBar.isVisible = false
                snackbar(it)
                Log.d(TAG, "subscribeToObservers: $it")
            },
            onLoading = {
                searchProgressBar.isVisible = true
            },
            onSuccess = {users->
                searchProgressBar.isVisible = false
                Log.d(TAG, "subscribeToObservers: onSuccess + ")
                userAdapter.users = users

            }
        ))
    }
    private fun setupRecyclerView() = rvSearchResults.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = userAdapter
        itemAnimator = null
    }

}