package com.example.android.mines.score

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

import com.example.android.mines.R

class ScoreFragment : Fragment() {

    companion object {
        fun newInstance() = ScoreFragment()
    }

    private lateinit var viewModel: ScoreViewModel
    private lateinit var uiView: View
    private lateinit var buttonAgain: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uiView = inflater.inflate(R.layout.score_fragment, container, false)
        buttonAgain = uiView.findViewById(R.id.button_again)
        buttonAgain.setOnClickListener {
            findNavController().navigate(R.id.action_scoreFragment_to_chooseFragment)
        }
        return uiView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScoreViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
