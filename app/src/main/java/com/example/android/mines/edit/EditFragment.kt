package com.example.android.mines.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.mines.R
import com.example.android.mines.databinding.EditFragmentBinding

/**
 * A simple [Fragment] subclass.
 */
class EditFragment : Fragment() {

    private lateinit var binding: EditFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = EditFragmentBinding.inflate(inflater, container, false)
        binding.textViewFiller.setOnClickListener {
            findNavController().navigate(R.id.action_editFragment_to_chooseFragment)
        }
        return binding.root
    }

}