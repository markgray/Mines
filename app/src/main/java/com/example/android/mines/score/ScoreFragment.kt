@file:Suppress("unused", "RedundantOverride")

package com.example.android.mines.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.database.MinesDatum
import com.example.android.mines.databinding.ScoreFragmentBinding
import com.example.android.mines.formatMinesDatum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoreFragment : Fragment() {

    companion object {
        fun newInstance() = ScoreFragment()
    }

    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var buttonAgain: Button
    private lateinit var textView: TextView
    private lateinit var latestDatum: MinesDatum
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ScoreFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.score_fragment, container, false)
        buttonAgain = binding.buttonAgain
        buttonAgain.setOnClickListener {
            findNavController().navigate(R.id.action_scoreFragment_to_chooseFragment)
        }
        textView = binding.textViewScore
        recyclerView = binding.gameHistoryList
        val adapter = MineDatumAdapter()
        recyclerView.adapter = adapter
        viewModel.gameHistory!!.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        CoroutineScope(Dispatchers.Main).launch {
            latestDatum = viewModel.retrieveLatestDatum()
            textView.append(formatMinesDatum(latestDatum))
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}
