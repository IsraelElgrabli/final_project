package com.example.final_project.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.final_project.R
import com.example.final_project.viewmodal.AuthViewModel
import androidx.navigation.fragment.navArgs

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val vm: AuthViewModel by activityViewModels()
    private val args: LoginFragmentArgs by navArgs()

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        vm.clearAuthFlags()

        val user = v.findViewById<EditText>(R.id.editUsername)
        val pass = v.findViewById<EditText>(R.id.editPassword)
        val chk  = v.findViewById<CheckBox>(R.id.checkRemember)
        val btn  = v.findViewById<Button>(R.id.btnLogin)
        val reg  = v.findViewById<Button>(R.id.btnGoRegister)

        // Prefill username box with remembered user (if any)
        user.setText(vm.rememberedUsername() ?: "")

        // If there is a remembered user, auto-login immediately
        // (Navigate to feed and skip typing)
        if (!args.fromLogout) {
            vm.tryAutoLogin()
        }
        // Observe error messages
        vm.error.observe(viewLifecycleOwner) { it?.let { toast(it) } }

        // On successful login → go to feed
        vm.success.observe(viewLifecycleOwner) { ok ->
            if (ok == true) {
                findNavController().navigate(R.id.action_login_to_feed)
            }
        }

        // Handle login click
        btn.setOnClickListener {
            vm.login(
                username = user.text.toString(),
                password = pass.text.toString(),
                remember = chk.isChecked
            )
        }

        // Go to register screen
        reg.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    private fun toast(m: String) =
        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show()
}
