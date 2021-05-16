package com.example.socialapp.ui.main.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.R
import com.example.socialapp.other.EventObserver
import com.example.socialapp.ui.main.viewmodels.BasePostViewModel
import com.example.socialapp.ui.main.viewmodels.ProfileViewModel
import com.example.socialapp.ui.snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class ProfileFragment : BasePostFragment(R.layout.fragment_profile) {


    override val basePostViewModel: BasePostViewModel
        get()  {
            val vm : ProfileViewModel by viewModels()
            return vm
        }

    protected open val uid : String
        get() = FirebaseAuth.getInstance().uid!!


    val viewModel: ProfileViewModel by lazy {
        basePostViewModel as ProfileViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeToObservers()

        btnToggleFollow.isVisible = false
        viewModel.loadProfile(uid)

        lifecycleScope.launch {
            viewModel.getPagingFlow(uid).collect {
                postAdapter.submitData(it)
            }

        }

        lifecycleScope.launch {
            postAdapter.loadStateFlow.collectLatest {
                profilePostsProgressBar?.isVisible = it.refresh is LoadState.Loading ||
                        it.append is LoadState.Loading
            }
        }

    }

    private fun setupRecyclerView() = rvPosts.apply {
        adapter = postAdapter
        itemAnimator = null
        layoutManager = LinearLayoutManager(requireContext())
    }
    private fun subscribeToObservers(){
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver(
            onError = {
                profileMetaProgressBar.isVisible = false
                snackbar(it)
            },
            onLoading = {
                profileMetaProgressBar.isVisible = true
            }
        ){  user ->
            profileMetaProgressBar.isVisible = false
            tvUsername.text = user.username

            tvProfileDescription.text =
                if(user.description.isEmpty()) {
                    requireContext().getString(R.string.no_description)
                }
                else user.description

            glide.load(user.profilePictureUrl).into(ivProfileImage)
        })

        basePostViewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
            },
            onLoading = {

            }
        ){deletedPost ->
            postAdapter.refresh()
        })
    }

}