package com.logicrealm.scordz.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.logicrealm.scordz.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Remove references to old views (e.g., textView)

        // Add references to new views using binding.yourNewViewId
        // Example: val myButton = binding.myButton

        // Update logic to interact with new views
        // Example: myButton.setOnClickListener { /* your code */ }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}