@file:Suppress("unused", "RedundantOverride")

package com.example.android.mines.score

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel

class ScoreFragment : Fragment() {

    companion object {
        fun newInstance() = ScoreFragment()
    }

    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var uiView: View
    private lateinit var buttonAgain: Button
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uiView = inflater.inflate(R.layout.score_fragment, container, false)
        buttonAgain = uiView.findViewById(R.id.button_again)
        buttonAgain.setOnClickListener {
            findNavController().navigate(R.id.action_scoreFragment_to_chooseFragment)
        }
        textView = uiView.findViewById(R.id.textViewScore)
        textView.append("\n${SystemClock.elapsedRealtime() - viewModel.startTime} milliseconds")
        return uiView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}
