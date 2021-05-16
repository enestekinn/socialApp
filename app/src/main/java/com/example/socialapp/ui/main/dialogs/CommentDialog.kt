package com.example.socialapp.ui.main.dialogs

import android.annotation.SuppressLint
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.socialapp.R
import com.example.socialapp.adapters.CommentAdapter
import com.example.socialapp.other.EventObserver
import com.example.socialapp.ui.main.viewmodels.CommentViewModel
import com.example.socialapp.ui.snackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class CommentDialog : DialogFragment(R.layout.fragment_comment){

    private val TAG = "CommentDialog"

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var commentAdapter: CommentAdapter

    private val args: CommentDialogArgs by navArgs()

    private val viewModel: CommentViewModel by viewModels()

    private lateinit var dialogView: View
    private lateinit var rvComments: RecyclerView
    private lateinit var etComment: EditText
    private lateinit var btnComment: Button
    private lateinit var commentProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return dialogView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.fragment_comment,
            null
        )
        rvComments = dialogView.findViewById(R.id.rvComments)
        etComment = dialogView.findViewById(R.id.etComment)
        btnComment = dialogView.findViewById(R.id.btnComment)
        commentProgressBar = dialogView.findViewById(R.id.commentProgressBar)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.comments)
            .setView(dialogView)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        viewModel.getCommentsForPost(args.postId)

        btnComment.setOnClickListener {
            val commentText = etComment.text.toString()
            viewModel.createComment(commentText,args.postId)
            etComment.text?.clear()
        }
        commentAdapter.setOnDeleteClickListener {comment ->
        viewModel.deleteComment(comment)

        }
        commentAdapter.setOnUserClickListener { comment->
            if (FirebaseAuth.getInstance().uid!! == comment.uid){

                requireActivity().bottomNavigationView.selectedItemId = R.id.profileFragment
            }
            findNavController().navigate(
                CommentDialogDirections.globalActionToOthersProfileFragment(comment.uid)
            )
        }
    }
    private fun subscribeToObservers() {
        viewModel.commentsForPost.observe(viewLifecycleOwner,EventObserver(
            onError = {
                commentProgressBar.isVisible = false
                snackbar(it)
                Log.e(TAG, "ERROR $it : On commentsForPost observer ")
            },
            onLoading = {
                commentProgressBar.isVisible = true
            },
            onSuccess = {comments->
                commentProgressBar.isVisible = false
                commentAdapter.comments = comments
            }
        ))

        viewModel.createCommentStatus.observe(viewLifecycleOwner,EventObserver(
            onError = {
                commentProgressBar.isVisible = false
                snackbar(it)
                btnComment.isEnabled = true
                Log.e(TAG, "ERROR $it : On createCommentStatus observer ")
            },
            onSuccess = {comment ->
                commentProgressBar.isVisible = false
                btnComment.isEnabled = true
                commentAdapter.comments += comment
            })
        )
        viewModel.deleteCommentStatus.observe(viewLifecycleOwner,EventObserver(
            onError = {
                commentProgressBar.isVisible = false
                snackbar(it)
            },
            onLoading = {
                commentProgressBar.isVisible = true
            },
            onSuccess = {comment->
                commentProgressBar.isVisible = false
                commentAdapter.comments -= comment
            }
        ))

    }
    private fun setupRecyclerView() = rvComments.apply {
        adapter = commentAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}