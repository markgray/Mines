package com.example.android.mines.choose

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

import com.example.android.mines.R
import com.example.android.mines.score.ScoreFragment

class ChooseFragment : Fragment() {

    companion object {
        fun newInstance() = ChooseFragment()
    }

    private lateinit var viewModel: ChooseViewModel
    private lateinit var uiView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uiView = inflater.inflate(R.layout.choose_fragment, container, false)
        uiView.findViewById<Button>(R.id.button_play).setOnClickListener {
            findNavController().navigate(R.id.action_chooseFragment_to_gameFragment)
        }
        return uiView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChooseViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
