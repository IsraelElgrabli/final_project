package com.example.final_project.component

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.final_project.R
import com.example.final_project.model.Post
import java.util.UUID

class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    private val vm: PostViewModel by activityViewModels()
    private val authVm: AuthSimpleViewModel by activityViewModels()
    private val userName: String
        get() = authVm.currentUsername() ?: "admin"
    companion object {
        private const val TAG = "CreatePost"
        private const val DEFAULT_IMAGE =
            "https://images.unsplash.com/photo-1501785888041-af3ef285b470?q=80&w=1200&auto=format&fit=crop"
        private const val DEFAULT_USER = "admin"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleEt = view.findViewById<EditText>(R.id.editTitle)
        val descEt  = view.findViewById<EditText>(R.id.editDescription)
        val postBtn = view.findViewById<Button>(R.id.btnPost)
        val uploadBtn = view.findViewById<Button>(R.id.btnUploadPhoto)

        uploadBtn.setOnClickListener {
            Log.d(TAG, "Upload clicked: using default image for now -> $DEFAULT_IMAGE")
        }

        postBtn.setOnClickListener {
            val title = titleEt.text?.toString()?.trim().orEmpty()
            val desc  = descEt.text?.toString()?.trim().orEmpty()

            if (title.isBlank() || desc.isBlank()) {
                Log.d(TAG, "Validation failed: title/description empty")
                return@setOnClickListener
            }

            val post = Post(
                id = UUID.randomUUID().toString(),
                title = title,
                description = desc,
                imageUrl = DEFAULT_IMAGE,
                userName = userName,
                comments = emptyList()
            )
            vm.addPost(post)             // ✅ persist to Room
            Log.d(TAG, "Inserted post: $post")

            // optional UX: clear and navigate back to feed
            titleEt.text?.clear()
            descEt.text?.clear()
            findNavController().popBackStack()
        }
    }
}
