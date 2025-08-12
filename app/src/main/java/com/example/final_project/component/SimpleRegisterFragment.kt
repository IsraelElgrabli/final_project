package com.example.final_project.component

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.final_project.MainActivity
import com.example.final_project.R

class SimpleRegisterFragment : Fragment(R.layout.fragment_simple_register) {
    private val vm: AuthSimpleViewModel by activityViewModels()

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        val user = v.findViewById<EditText>(R.id.editUsername)
        val pass = v.findViewById<EditText>(R.id.editPassword)
        val btn  = v.findViewById<Button>(R.id.btnRegister)

        vm.error.observe(viewLifecycleOwner) { it?.let { toast(it) } }
        vm.success.observe(viewLifecycleOwner) { ok ->
            if (ok == true) {
                findNavController().navigate(
                    R.id.action_register_to_feed
                )
            }
        }

        btn.setOnClickListener {
            vm.register(user.text.toString(), pass.text.toString())
        }
    }

    private fun toast(m: String) =
        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show()
}
