package com.example.android.mines.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.databinding.EditFragmentBinding

/**
 * A simple [Fragment] subclass.
 */
class EditFragment : Fragment() {

    private lateinit var binding: EditFragmentBinding
    /**
     * The [SharedViewModel] shared view model used by all of our fragments.
     */
    private val viewModel: SharedViewModel by activityViewModels()

    /**
     * The [RecyclerView] in our layout which displays the list of old games which is read from our
     * ROOM database.
     */
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = EditFragmentBinding.inflate(inflater, container, false)
        binding.launchDialog.setOnClickListener {
            findNavController().navigate(R.id.action_editFragment_to_editHistoryDialog)
        }
        binding.abortEditHistory.setOnClickListener {
            findNavController().navigate(R.id.action_editFragment_to_chooseFragment)
        }
        recyclerView = binding.historyRecyclerView
        val adapter = EditHistoryAdapter {
            viewModel.deleteMinesDatum(it)
        }
        recyclerView.adapter = adapter
        viewModel.gameHistory!!.observe(viewLifecycleOwner, {
            it?.let {
                adapter.data = it
                if (it.isEmpty()) {
                    binding.textViewFiller.visibility = View.VISIBLE
                    binding.historyRecyclerView.visibility = View.GONE
                } else {
                    binding.textViewFiller.visibility = View.GONE
                    binding.historyRecyclerView.visibility = View.VISIBLE
                }
            }
        })
        return binding.root
    }

}