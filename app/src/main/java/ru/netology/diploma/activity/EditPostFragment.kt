package ru.netology.diploma.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentEditPostBinding
import ru.netology.diploma.dto.AttachmentType
import ru.netology.diploma.util.AndroidUtils
import ru.netology.diploma.util.Constants
import ru.netology.diploma.util.PostDealtWith
import ru.netology.diploma.util.checkMediaType
import ru.netology.diploma.util.createFileFromInputStream
import ru.netology.diploma.util.getInputStreamFromUri
import ru.netology.diploma.util.load
import ru.netology.diploma.viewmodel.PostViewModel
import ru.netology.diploma.viewmodel.UserViewModel

@Suppress("unused", "unused")
@AndroidEntryPoint
class EditPostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    private val viewModelUser: UserViewModel by activityViewModels()

    private val photoResultContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data
                    ?: return@registerForActivityResult
                val file = uri.toFile()
                viewModel.setAttachment(uri, file, AttachmentType.IMAGE)
            }
        }

    private val pickFileResultContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data ?: return@registerForActivityResult
                val file = if (uri.scheme == "file") {
                    uri.toFile()
                } else {
                    getInputStreamFromUri(context, uri)?.let { inputStream ->
                        createFileFromInputStream(inputStream)
                    }
                }

                val path = uri.path
                val type = path?.let { it1 -> checkMediaType(it1) }!!
                if (file != null) {
                    viewModel.setAttachment(uri, file, type)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditPostBinding.inflate(
            inflater,
            container,
            false
        )

        val post = PostDealtWith.get()
        viewModel.setPostToEdit(post)

        binding.edit.requestFocus()
        binding.edit.setText(post.content)
        binding.edit.movementMethod = ScrollingMovementMethod()


        val toolbar: Toolbar = binding.toolbar
        toolbar.overflowIcon?.colorFilter = BlendModeColorFilter(
            Color.WHITE,
            BlendMode.SRC_ATOP
        )
        toolbar.inflateMenu(R.menu.menu_edit)

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save -> {

                    val mentioned = viewModel.userChosen.value ?: viewModel.postToEdit.value?.mentionIds
                    val finalMentioned = mentioned ?: emptyList()

                    val postEdited =
                        viewModel.postToEdit.value?.copy(content = binding.edit.text.toString(), mentionIds = finalMentioned)
                    if (postEdited != null) {
                        viewModel.edit(postEdited)
                    }
                    AndroidUtils.hideKeyboard(requireView())
                    true
                }

                R.id.cancel -> findNavController().navigateUp()
                else -> false
            }
        }


        binding.linkIcon.setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            val pickIntent = Intent.createChooser(intent, context?.getString (R.string.select_a_file))
            pickFileResultContract.launch(pickIntent)

        }


        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                .maxResultSize(2048, 2048)
                .createIntent { photoResultContract.launch(it) }
        }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .galleryOnly()
                .compress(2048)
                .createIntent { photoResultContract.launch(it) }
        }

        binding.photoPreview.visibility = View.GONE
        binding.videoPreview.visibility = View.GONE
        binding.audioPreview.visibility = View.GONE

        viewModel.postToEdit.observe(viewLifecycleOwner) { post ->
            if (viewModel.attachment.value == null) {

                when (post.attachment?.type) {
                    AttachmentType.IMAGE -> {
                        binding.photoPreview.isVisible = true
                        post.attachment.url?.let { it1 -> binding.photoPreview.load(it1) }
                    }

                    AttachmentType.VIDEO -> {
                        binding.videoPreview.isVisible = true
                        binding.videoPreview.setVideoPath(post.attachment.url)
                        binding.videoPreview.setOnPreparedListener { mediaPlayer ->
                            mediaPlayer?.setVolume(0F, 0F)
                            mediaPlayer?.isLooping = true
                            binding.videoPreview.start()
                        }

                    }

                    AttachmentType.AUDIO -> {
                        binding.audioPreview.isVisible = true
                    }

                    else -> Unit
                }
            }
        }

        viewModel.attachment.observe(viewLifecycleOwner) {attachmentModel ->
            if (attachmentModel != null) {
                when (attachmentModel.type) {
                    AttachmentType.IMAGE -> {
                        binding.videoPreview.visibility = View.GONE
                        binding.audioPreview.visibility = View.GONE

                        binding.photoPreview.isVisible = true
                        binding.photoPreview.setImageURI(attachmentModel.uri)
                    }

                    AttachmentType.VIDEO -> {
                        binding.photoPreview.visibility = View.GONE
                        binding.audioPreview.visibility = View.GONE

                        binding.videoPreview.isVisible = true
                        binding.videoPreview.setVideoURI(attachmentModel.uri)
                        binding.videoPreview.setOnPreparedListener { mediaPlayer ->
                            mediaPlayer?.setVolume(0F, 0F)
                            mediaPlayer?.isLooping = true
                            binding.videoPreview.start()
                        }

                    }
                    AttachmentType.AUDIO -> {
                        binding.videoPreview.visibility = View.GONE
                        binding.photoPreview.visibility = View.GONE

                        binding.audioPreview.isVisible = true
                    }
                    else-> Unit
                }
            }
        }

        binding.removeAttachment.setOnClickListener{
            viewModel.clearAttachment()
            viewModel.clearAttachmentEdit()
            binding.photoPreview.visibility = View.GONE
            binding.videoPreview.visibility = View.GONE
            binding.audioPreview.visibility = View.GONE
            val toast = Toast.makeText(context, R.string.removed, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }

        binding.removeLocation.setOnClickListener{
            viewModel.clearLocationEdit()
            val toast = Toast.makeText(context, R.string.removed, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }

        binding.removeMentioned.setOnClickListener{
            viewModelUser.clearChoosing()
            viewModel.clearUserChosen()
            viewModel.clearMentionedEdit()
            val toast = Toast.makeText(context, R.string.removed, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }

        binding.chooseUsers.setOnClickListener{
            findNavController().navigate(R.id.action_editPostFragment_to_choosingFragment)
        }

        binding.choosePlace.setOnClickListener{
            findNavController().navigate(R.id.action_editPostFragment_to_mapsFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this)
        {
            (activity as MainActivity).getSharedPreferences(
                Constants.DRAFT_PREF_NAME,
                Context.MODE_PRIVATE
            ).edit().apply {
                putString(Constants.DRAFT_KEY, binding.edit.text.toString())
                apply()
            }
            findNavController().navigateUp()
        }

        viewModel.postCreated.observe(viewLifecycleOwner)
        {
            findNavController().navigate(R.id.action_editPostFragment_to_allPostsFragment)
        }

        viewModel.postCreatedError.observe(viewLifecycleOwner)
        {

            Snackbar.make(binding.scrollView, "", Snackbar.LENGTH_LONG)
                .setAnchorView(binding.edit)
                .setTextMaxLines(3)
                .setText(R.string.something_went_wrong)
                .setBackgroundTint(Color.rgb(0, 102, 255))
                .show()

        }

        return binding.root
    }
}