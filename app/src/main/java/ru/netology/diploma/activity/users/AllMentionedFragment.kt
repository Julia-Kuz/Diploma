package ru.netology.diploma.activity.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.adapter.OnInteractionListenerUser
import ru.netology.diploma.adapter.UserAdapter
import ru.netology.diploma.databinding.FragmentAllLikersBinding
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.util.PostDealtWith
import ru.netology.diploma.viewmodel.PostViewModel
import ru.netology.diploma.viewmodel.UserViewModel
@Suppress("unused", "unused")
@AndroidEntryPoint
class AllMentionedFragment: Fragment() {

    private val viewModelUser: UserViewModel by activityViewModels()
    private val viewModelPost: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAllLikersBinding.inflate(
            inflater,
            container,
            false
        )

        val post = PostDealtWith.get()
        binding.likers.text = context?.getString(R.string.mentioned_users)

        UserAdapter.choosing = false
        binding.checkbox.isVisible = false

        val adapter = UserAdapter(object : OnInteractionListenerUser {
            override fun show(user: UserResponse) {}

            override fun choose(user: UserResponse) {}
        })

        binding.recyclerList.adapter = adapter

        val list: MutableList<UserResponse> = mutableListOf()

        for (i in post.mentionIds.indices) {
            viewModelPost.getUserById(post.mentionIds[i])
            val user = viewModelPost.userList.value
            if (user != null) {
                list.add(user)
            }
        }

        adapter.submitList(list)

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }


        viewModelUser.dataState.observe(viewLifecycleOwner) { feedModelState ->
            binding.progress.isVisible = feedModelState.loading
            binding.errorGroup.isVisible = feedModelState.error
        }

        binding.retryButton.setOnClickListener {
            for (i in post.mentionIds.indices) {
                viewModelPost.getUserById(post.mentionIds[i])
                val user = viewModelPost.userList.value
                if (user != null) {
                    list.add(user)
                }
            }

            adapter.submitList(list)
        }

        binding.swiperefresh.setOnRefreshListener {
            for (i in post.mentionIds.indices) {
                viewModelPost.getUserById(post.mentionIds[i])
                val user = viewModelPost.userList.value
                if (user != null) {
                    list.add(user)
                }
            }

            adapter.submitList(list)
            binding.swiperefresh.isRefreshing = false
        }


        return binding.root
    }
}