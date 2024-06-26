package ru.netology.diploma.adapter

import android.net.Uri
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentOnePostBinding
import ru.netology.diploma.dto.AttachmentType
import ru.netology.diploma.dto.Post
import ru.netology.diploma.util.formatDateTime
import ru.netology.diploma.util.load
import ru.netology.diploma.util.loadCircle
import ru.netology.diploma.util.numberRepresentation

interface OnInteractionListener {
    fun like(post: Post)
    fun remove(post: Post)
    fun edit(post: Post)
    fun showPost(post: Post)
    fun playMusic (post: Post)
}

class PostsAdapter(private val onInteractionListener: OnInteractionListener) :
    PagingDataAdapter <Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            FragmentOnePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        post?.let { holder.bind(it) }
    }
}

class PostViewHolder(
    private val binding: FragmentOnePostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = formatDateTime(post.published)
            content.text = post.content
            content.movementMethod = ScrollingMovementMethod()
            likesIcon.isChecked = post.likedByMe
            likesIcon.text = numberRepresentation(post.likeOwnerIds.size)
            attachmentImage.visibility = View.GONE
            music.visibility = View.GONE

            if (post.link != null) {
                videoLink.visibility = View.VISIBLE
            } else videoLink.visibility = View.GONE


            val urlAvatar = "${post.authorAvatar}"
            avatar.loadCircle(urlAvatar)

            if (post.attachment?.url != null) {
                when (post.attachment.type) {
                    AttachmentType.AUDIO -> {
                        music.visibility = View.VISIBLE
                        if (post.attachment.isPlaying) {
                            playButton.setImageResource(R.drawable.ic_pause_circle_outline_24)
                        } else {
                            playButton.setImageResource(R.drawable.ic_play_circle_outline_24)
                        }

                        playButton.setOnClickListener {
                            onInteractionListener.playMusic(post)
                        }
                        oneTrackName.setOnClickListener { onInteractionListener.showPost(post) }
                    }

                    AttachmentType.IMAGE -> {
                        post.attachment.url.let {
                            val url = post.attachment.url
                            attachmentImage.load(url)
                        }
                        attachmentImage.visibility = View.VISIBLE
                        attachmentImage.setOnClickListener {onInteractionListener.showPost(post)}
                    }


                    AttachmentType.VIDEO -> {
                        videoLink.visibility = View.VISIBLE
                        videoLink.setOnClickListener { onInteractionListener.showPost(post) }
                        val uri = Uri.parse(post.attachment.url)
                        videoLink.setVideoURI(uri)
                        videoLink.setOnPreparedListener { mediaPlayer ->
                            mediaPlayer?.setVolume(0F, 0F)
                            mediaPlayer?.isLooping = true
                            videoLink.start()
                        }
                    }

                    else -> Unit
                }
            }


            likesIcon.setOnClickListener {
                onInteractionListener.like(post)
            }


            menuOnePost.isVisible = post.ownedByMe

            menuOnePost.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_optons)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                onInteractionListener.remove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.edit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            content.setOnClickListener {
                onInteractionListener.showPost(post)
            }

        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}