package ru.netology.diploma.activity

import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentDetailedOnePostBinding
import ru.netology.diploma.dto.AttachmentType
import ru.netology.diploma.dto.Post
import ru.netology.diploma.util.PostBundle
import ru.netology.diploma.util.PostDealtWith
import ru.netology.diploma.util.formatDateTime
import ru.netology.diploma.util.load
import ru.netology.diploma.util.loadCircle
import ru.netology.diploma.util.numberRepresentation
import ru.netology.diploma.util.published
import ru.netology.diploma.viewmodel.PostViewModel

@AndroidEntryPoint
class DetailedOnePostFragment : Fragment() {

    companion object {
        var Bundle.postBundle: Post? by PostBundle
    }


    private val viewModel: PostViewModel by activityViewModels()
    private val mediaObserver = MediaLifecycleObserver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetailedOnePostBinding.inflate(
            inflater,
            container,
            false
        )

        val post = PostDealtWith.get()

//        val post = arguments?.postBundle ?: Post(
//            id = 0,
//            author = "",
//            authorId = 0,
//            authorJob = null,
//            authorAvatar = null,
//            content = "error",
//            published = published(),
//            coords = null,
//            link = null,
//            mentionIds = emptyList(),
//            mentionedMe = false,
//            likeOwnerIds = emptyList(),
//            likedByMe = false,
//            attachment = null,
//            users = emptyMap(),
//        )

        binding.content.movementMethod = ScrollingMovementMethod()

        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                published.text = formatDateTime(post.published)
                placeOfWork.text = post.authorJob ?: context?.getString(R.string.looking_for_a_job)
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
                                if (mediaObserver.player?.isPlaying == true) {
                                    mediaObserver.apply {
                                        viewModel.updateIsPlaying(post.id, false)
                                        playButton.setImageResource(R.drawable.ic_play_circle_outline_24)
                                        stop()
                                    }
                                } else {
                                    mediaObserver.apply {
                                        viewModel.updateIsPlaying(post.id, true)
                                        playButton.setImageResource(R.drawable.ic_pause_circle_outline_24)
                                        post.attachment.url.let { play(it) }
                                    }
                                }
                            }
                        }

                        AttachmentType.IMAGE -> {
                            post.attachment.url.let {
                                val url = post.attachment.url
                                attachmentImage.load(url)
                            }
                            attachmentImage.visibility = View.VISIBLE

                        }


                        AttachmentType.VIDEO -> {

                            videoLink.visibility = View.VISIBLE

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

                mediaObserver.player?.setOnCompletionListener {
                    mediaObserver.player?.stop()
                    viewModel.updatePlayer()
                }

                menuOnePost.isVisible = post.ownedByMe

                menuOnePost.setOnClickListener {

                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.menu_optons)
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.remove -> {
                                    viewModel.removeById(post.id)
                                    findNavController().navigateUp()
                                    true
                                }

                                R.id.edit -> {
                                    PostDealtWith.savePostDealtWith(post)
                                    findNavController().navigate(R.id.action_detailedOnePostFragment_to_editPostFragment)
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()

                }

                //иконки Likers

                avatarLiker1.isVisible = post.likeOwnerIds.isNotEmpty()
                avatarLiker2.isVisible = post.likeOwnerIds.size >= 2
                avatarLiker3.isVisible = post.likeOwnerIds.size >= 3
                avatarLiker4.isVisible = post.likeOwnerIds.size >= 4
                avatarLiker5.isVisible = post.likeOwnerIds.size >= 5
                avatarLikerMore.isVisible = post.likeOwnerIds.size >= 6

                post.likeOwnerIds.forEachIndexed { index, userId ->
                    val avaUrl = post.users[userId]?.avatar ?: return@forEachIndexed
                    when (index) {
                        0 -> avatarLiker1.loadCircle(avaUrl)
                        1 -> avatarLiker2.loadCircle(avaUrl)
                        2 -> avatarLiker3.loadCircle(avaUrl)
                        3 -> avatarLiker4.loadCircle(avaUrl)
                        4 -> avatarLiker5.loadCircle(avaUrl)
                        else -> Unit
                    }
                }

                avatarLikerMore.setOnClickListener {
                    PostDealtWith.savePostDealtWith(post)
                    findNavController().navigate(R.id.action_detailedOnePostFragment_to_allLikersFragment)
                }

                //иконки mentioned
                avatarMention1.isVisible = post.mentionIds.isNotEmpty()
                avatarMention2.isVisible = post.mentionIds.size >= 2
                avatarMention3.isVisible = post.mentionIds.size >= 3
                avatarMention4.isVisible = post.mentionIds.size >= 4
                avatarMention5.isVisible = post.mentionIds.size >= 5
                avatarMentionMore.isVisible = post.mentionIds.size >= 6

                post.mentionIds.forEachIndexed { index, userId ->
                    val avaUrl = post.users[userId]?.avatar ?: return@forEachIndexed
                    when (index) {
                        0 -> avatarMention1.loadCircle(avaUrl)
                        1 -> avatarMention2.loadCircle(avaUrl)
                        2 -> avatarMention3.loadCircle(avaUrl)
                        3 -> avatarMention4.loadCircle(avaUrl)
                        4 -> avatarMention5.loadCircle(avaUrl)
                        else -> Unit
                    }
                }


                avatarMentionMore.setOnClickListener {
                    PostDealtWith.savePostDealtWith(post)
                    findNavController().navigate(R.id.action_detailedOnePostFragment_to_allMentionedFragment)
                }

                //карта

                MapKitFactory.initialize(requireContext())
                val mapView = binding.mapview
                val map = mapView.mapWindow.map

                binding.mapGroup.isVisible = (post.coords?.lat != null && post.coords.long != null)
                if (post.coords?.lat != null && post.coords.long != null) {
                    map.move(
                        CameraPosition(
                            Point(
                                post.coords.lat,
                                post.coords.long
                            ),
                            /* zoom = */ 13.0f,
                            /* azimuth = */ 150.0f,
                            /* tilt = */ 30.0f
                        )
                    )

                }


                val imageProvider =
                    ImageProvider.fromResource(requireContext(), R.drawable.ic_map_marker_icon)
                map.mapObjects.addPlacemark().apply {
                    if (post.coords?.lat != null && post.coords.long != null) {
                        geometry = Point(post.coords.lat, post.coords.long)
                        setIcon(imageProvider)
                        setIconStyle(
                            IconStyle().apply {
                                anchor = PointF(0.5f, 1.0f)
                                scale = 0.08f
                                zIndex = 10F
                            }
                        )
                    }

                }


                binding.zoomInButton.setOnClickListener {
                    val cameraPosition = map.cameraPosition
                    val newZoom = cameraPosition.zoom + 1.0f
                    val newCameraPosition = CameraPosition(
                        cameraPosition.target,
                        newZoom,
                        cameraPosition.azimuth,
                        cameraPosition.tilt
                    )
                    map.move(newCameraPosition)
                }

                binding.zoomOutButton.setOnClickListener {
                    val cameraPosition = map.cameraPosition
                    val newZoom = cameraPosition.zoom - 1.0f
                    val newCameraPosition = CameraPosition(
                        cameraPosition.target,
                        newZoom,
                        cameraPosition.azimuth,
                        cameraPosition.tilt
                    )
                    map.move(newCameraPosition)
                }


            }
        }

        bind(post)

        return binding.root
    }
}