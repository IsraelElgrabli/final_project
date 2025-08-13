package com.example.final_project.component

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.final_project.R
import com.example.final_project.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    private val vm: PostViewModel by activityViewModels()
    private val authVm: AuthSimpleViewModel by activityViewModels()

    private val userName: String
        get() = authVm.currentUsername() ?: "admin"

    private var uploadedImageUrl: String? = null
    private var selectedUri: Uri? = null

    companion object {
        private const val TAG = "CreatePost"
        private const val DEFAULT_IMAGE =
            "https://images.unsplash.com/photo-1501785888041-af3ef285b470?q=80&w=1200&auto=format&fit=crop"
    }

    // --- Permissions (API 29–35) ---
    private val requestImagePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) pickImage.launch("image/*")
            else Toast.makeText(requireContext(), "Permission required to select image", Toast.LENGTH_SHORT).show()
        }

    private fun ensureImagePermission(then: () -> Unit) {
        val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(requireContext(), perm) == PackageManager.PERMISSION_GRANTED) {
            then()
        } else {
            requestImagePermission.launch(perm)
        }
    }

    // Pick an image
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) {
            Log.d(TAG, "Image picking cancelled")
            return@registerForActivityResult
        }
        selectedUri = uri
        view?.findViewById<ImageView>(R.id.imagePreview)?.setImageURI(uri)
        startUpload(uri) // kick off upload after sign-in
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleEt = view.findViewById<EditText>(R.id.editTitle)
        val descEt  = view.findViewById<EditText>(R.id.editDescription)
        val postBtn = view.findViewById<Button>(R.id.btnPost)
        val uploadBtn = view.findViewById<Button>(R.id.btnUploadPhoto)

        uploadBtn.setOnClickListener {
            ensureImagePermission { pickImage.launch("image/*") }
        }

        postBtn.setOnClickListener {
            val title = titleEt.text?.toString()?.trim().orEmpty()
            val desc  = descEt.text?.toString()?.trim().orEmpty()

            if (title.isBlank() || desc.isBlank()) {
                Toast.makeText(requireContext(), "Title/Description required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // If an image was picked, force waiting for upload to complete
            if (selectedUri != null && uploadedImageUrl == null) {
                Toast.makeText(requireContext(), "Please wait for image upload to finish…", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageUrl = uploadedImageUrl ?: DEFAULT_IMAGE

            val post = Post(
                id = UUID.randomUUID().toString(),
                title = title,
                description = desc,
                imageUrl = imageUrl,
                userName = userName,
                comments = emptyList()
            )

            // ✅ Preload the image into Picasso cache before adding
            com.squareup.picasso.Picasso.get().load(imageUrl).fetch(object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    vm.addPost(post)
                    Toast.makeText(requireContext(), "Posted!", Toast.LENGTH_SHORT).show()
                    titleEt.text?.clear()
                    descEt.text?.clear()
                    findNavController().navigate(R.id.feedFragment)
                }

                override fun onError(e: java.lang.Exception?) {
                    // Even if cache fails, still add the post
                    vm.addPost(post)
                    Toast.makeText(requireContext(), "Posted!", Toast.LENGTH_SHORT).show()
                    titleEt.text?.clear()
                    descEt.text?.clear()
                    findNavController().navigate(R.id.feedFragment)
                }
            })
        }
    }

    /** Ensure we are signed in (anonymous) BEFORE uploading. */
    private suspend fun ensureSignedIn() {
        if (Firebase.auth.currentUser != null) return
        try {
            Firebase.auth.signInAnonymously().await()
            Log.d(TAG, "Anonymous sign-in OK: uid=${Firebase.auth.currentUser?.uid}")
        } catch (e: Exception) {
            Log.e(TAG, "Anonymous sign-in FAILED", e)
            throw e
        }
    }

    /** Your teammate’s flow + we await auth first. */
    private fun startUpload(uri: Uri) {
        val postBtn = view?.findViewById<Button>(R.id.btnPost)
        val uploadBtn = view?.findViewById<Button>(R.id.btnUploadPhoto)
        val spinner = view?.findViewById<ProgressBar>(R.id.uploadSpinner)

        // Disable buttons & show spinner
        postBtn?.isEnabled = false
        uploadBtn?.isEnabled = false
        spinner?.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                ensureSignedIn()

                val storage = Firebase.storage
                val bucket = storage.reference.bucket
                val storageRef = storage.reference.child("post_images/${System.currentTimeMillis()}.jpg")
                Log.d(TAG, "Uploading to bucket=$bucket path=${storageRef.path} uri=$uri")

                if (bucket.isNullOrBlank()) {
                    throw IllegalStateException("Firebase Storage bucket is empty. Check google-services.json and enable Storage in Firebase Console.")
                }

                val snap = storageRef.putFile(uri).await()
                Log.d(TAG, "Uploaded bytes=${snap.totalByteCount}")

                val downloadUri = storageRef.downloadUrl.await()
                uploadedImageUrl = downloadUri.toString()
                Log.d(TAG, "Download URL = $uploadedImageUrl")
                Toast.makeText(requireContext(), "Image uploaded", Toast.LENGTH_SHORT).show()

            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                uploadedImageUrl = null
                Log.e(TAG, "Upload failed", e)
                Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                // Enable buttons & hide spinner
                postBtn?.isEnabled = true
                uploadBtn?.isEnabled = true
                spinner?.visibility = View.GONE
            }
        }
    }
}
