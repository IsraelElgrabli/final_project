package com.example.final_project.component

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.final_project.MainActivity
import com.example.final_project.R

class SimpleLoginFragment : Fragment(R.layout.fragment_simple_login) {
    private val vm: AuthSimpleViewModel by activityViewModels()

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        val user = v.findViewById<EditText>(R.id.editUsername)
        val pass = v.findViewById<EditText>(R.id.editPassword)
        val chk  = v.findViewById<CheckBox>(R.id.checkRemember)
        val btn  = v.findViewById<Button>(R.id.btnLogin)
        val reg  = v.findViewById<Button>(R.id.btnGoRegister)

        // Load remembered username if any
        user.setText(vm.rememberedUsername())

        // Observe error messages
        vm.error.observe(viewLifecycleOwner) { it?.let { toast(it) } }

        // On successful login → go to feed
        vm.success.observe(viewLifecycleOwner) { ok ->
            if (ok == true) {
                findNavController().navigate(
                    R.id.action_login_to_feed
                )
            }
        }

        // Handle login click
        btn.setOnClickListener {
            vm.login(user.text.toString(), pass.text.toString(), chk.isChecked)
        }

        // Go to register screen
        reg.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    private fun toast(m: String) =
        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show()
}
