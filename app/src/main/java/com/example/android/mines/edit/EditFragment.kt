package com.example.android.mines.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.choose.ChooseFragment
import com.example.android.mines.databinding.EditFragmentBinding

/**
 * A simple [Fragment] subclass which allows the user to view and edit the game history database. It
 * is launched by [ChooseFragment] when the user clicks on the "Edit History" button in its UI.
 */
class EditFragment : Fragment() {

    /**
     * The [ViewBinding] generated from our layout file [R.layout.edit_fragment].
     */
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

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onViewCreated]. It is recommended to ONLY inflate the layout in this method
     * and move logic that operates on the returned [View] to [onViewCreated].
     *
     * First we initialize our [EditFragmentBinding] field [binding] by having the
     * [EditFragmentBinding.inflate] method inflate its associated layout file
     * [R.layout.edit_fragment] using our [LayoutInflater] parameter [inflater], with
     * our [ViewGroup] parameter [container] supplying the layout params without the
     * view being attached to it.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI.
     */
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