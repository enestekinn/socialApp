package com.example.socialapp.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.socialapp.R
import com.example.socialapp.adapters.PostAdapter
import com.example.socialapp.adapters.UserAdapter
import com.example.socialapp.other.EventObserver
import com.example.socialapp.ui.main.dialogs.DeletePostDialog
import com.example.socialapp.ui.main.dialogs.LikedByDialog
import com.example.socialapp.ui.main.viewmodels.BasePostViewModel
import com.example.socialapp.ui.snackbar
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

abstract  class BasePostFragment(
    layoutId: Int
): Fragment(layoutId) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var postAdapter: PostAdapter



    protected abstract val basePostViewModel: BasePostViewModel

    private var curLikedIndex: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        postAdapter.setOnLikeClickListener { post, i ->
            curLikedIndex = i
            post.isLiked = !post.isLiked
            basePostViewModel.toggleLikeForPost(post)
        }

        postAdapter.setOnDeleteClickListener { post ->
            DeletePostDialog().apply {
                setPositiveListener {
                    basePostViewModel.deletePost(post)
                }
            }.show(childFragmentManager,null)
        }

        postAdapter.setOnLikedByClickListener { post ->
            basePostViewModel.getUsers(post.likedBy)
        }
        postAdapter.setOnCommentsClickListener { post->
            findNavController().navigate(
                R.id.globalActionToCommentDialog,
                Bundle().apply {
                    putString("postId",post.id)
                }
            )
        }
    }

    private fun subscribeToObservers(){
        basePostViewModel.likePostStatus.observe(viewLifecycleOwner,EventObserver(
            onError = {
                curLikedIndex?.let { index ->
                    postAdapter.peek(index)?.isLiking = false
                    postAdapter.notifyItemChanged(index)
                }
                snackbar(it)
            },
            onLoading = {
                curLikedIndex?.let { index ->
                    postAdapter.peek(index)?.isLiking = true
                    postAdapter.notifyItemChanged(index)
                }
            },
            onSuccess = {isLiked ->
                curLikedIndex?.let { index ->
                    val uid = FirebaseAuth.getInstance().uid!!
                    postAdapter.peek(index)?.apply {
                        this.isLiked = isLiked
                        isLiking = false

                        if (isLiked) {
                            likedBy += uid
                        }
                        else {

                        likedBy -= uid
                        }


                    }
                    postAdapter.notifyItemChanged(index)
                }

            }

        )
        )

        basePostViewModel.likedByUsers.observe(viewLifecycleOwner,EventObserver(
            onError = {
                snackbar(it)
            },
            onLoading = {

            },
            onSuccess = {users->
                val userAdapter = UserAdapter(glide)
                userAdapter.users  = users
                LikedByDialog(userAdapter).show(childFragmentManager,null)

            }
        ))
    }
}