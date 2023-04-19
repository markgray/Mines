package com.example.android.mines.choose

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.database.MinesDataBase
import com.example.android.mines.database.MinesDatabaseDao
import com.example.android.mines.databinding.ChooseFragmentBinding
import com.example.android.mines.edit.EditFragment
import com.example.android.mines.game.GameFragment

/**
 * This is the "app:startDestination" for our app and allows the user to choose the size of the
 * game board which it then configures the [SharedViewModel] to represent before navigating to the
 * `GameFragment` to begin playing the game.
 */
class ChooseFragment : Fragment() {

    companion object {
        /**
         * Default number of columns if the user does not choose a game board size
         */
        const val COLUMN_COUNT: Int = 8

        /**
         * Default number of row if the user does not choose a game board size
         */
        const val ROW_COUNT: Int = 8

        /**
         * Default number of Mined sectors if the user does not choose a game board size
         */
        const val MINE_COUNT: Int = 10

        /**
         * Unused [ChooseFragment] factory method.
         */
        @Suppress("unused") // Suggested change would make class less reusable
        fun newInstance(): ChooseFragment = ChooseFragment()
    }

    /**
     * The [SharedViewModel] instance we share with all our other fragments.
     */
    private val viewModel: SharedViewModel by activityViewModels()

    /**
     * The [ChooseFragmentBinding] which is inflated from its associated layout file
     * layout/choose_fragment.xml (resource ID [R.layout.choose_fragment]) and bound to.
     */
    private lateinit var binding: ChooseFragmentBinding

    /**
     * Our [Application] which we need to use as the context for our database when we first open or
     * create it.
     */
    private lateinit var application: Application

    /**
     * The [MinesDataBase] we store our game history in.
     */
    private lateinit var minesDataBase: MinesDataBase

    /**
     * The [RadioGroup] which contains `RadioButton`'s to select different board sizes.
     */
    private lateinit var radioGroup: RadioGroup

    /**
     * If false indicates we should use default board size.
     */
    private var boardSizeChosen: Boolean = false

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. A default View can be returned by calling [Fragment]
     * in your constructor. It is recommended to only inflate the layout in this method and move
     * logic that operates on the returned View to [onViewCreated]. First we initialize our field
     * [binding] to the [ChooseFragmentBinding] that the [ChooseFragmentBinding.inflate] method
     * returns when it uses our [LayoutInflater] parameter [inflater] to inflate our layout file
     * [R.layout.choose_fragment] using our [ViewGroup] parameter [container] for its `LayoutParams`
     * without attaching to it. We set the `OnClickListener` of the quietOrTalkative `Button` in our
     * layout to a lambda which branches on whether the [SharedViewModel.talkEnabled] property of
     * [viewModel] to is `true` or `false`:
     *  - `true`: it sets the [SharedViewModel.talkEnabled] property of [viewModel] to `false` and
     *  and sets the text  of the `Button` to the [String] with resource ID [R.string.clickForTalkativeMode]
     *  ("CLICK FOR TALKATIVE MODE").
     *  - `false`: it sets the [SharedViewModel.talkEnabled] property of [viewModel] to `true` and
     *  and sets the text  of the `Button` to the [String] with resource ID [R.string.clickForQuietMode]
     *  ("CLICK FOR SILENT MODE").
     *
     * We set the `OnClickListener` of the [ChooseFragmentBinding.editHistory] `Button` in our layout
     * to a lambda which sets the [SharedViewModel.buttonTop] property of [viewModel] to the top
     * position of the [ChooseFragmentBinding.buttonPlay] button in our UI relative to its parent (a
     * shameless hack so that the [GameFragment] knows how much vertical space it has available for
     * its game board). Then it finds a [NavController] for our fragment and calls its
     * [NavController.navigate] method to navigate to the [EditFragment]
     *
     * We set the `OnClickListener` of the [ChooseFragmentBinding.buttonCustom] `Button` in our
     * layout to a lambda which sets the [SharedViewModel.buttonTop] property of [viewModel] to the top
     * position of the [ChooseFragmentBinding.buttonPlay] button in our UI relative to its parent and
     * then calls our method [onCustomClicked] with the [View] clicked. We set the `OnClickListener`
     * of the `buttonPlay` `Button` in our layout to a lambda which sets the [SharedViewModel.buttonTop]
     * property of [viewModel] to the top position of the [ChooseFragmentBinding.buttonPlay] button
     * in our UI relative to its parent and then calls our method [onPlayClicked] with the [View]
     * clicked. We set our [RadioGroup] field [radioGroup] to the `boardChoice` [RadioGroup] in our
     * layout file then set its `OnCheckedChangeListener` to a lambda which calls our [selectBoardSize]
     * method with the ID of the `RadioButton` which was checked. Finally we return the outermost
     * [View] in the layout file associated with the [ChooseFragmentBinding] field [binding] (its
     * `root` [View]).
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate any views
     * @param container If non-`null`, this is the parent view that the fragment's UI will be
     * attached to. The fragment should not add the view itself, but this can be used to generate
     * the `LayoutParams` of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed from a previous
     * saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or null.
     */
    @Suppress("RedundantNullableReturnType") // The method we override returns nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChooseFragmentBinding.inflate(
            inflater,
            container,
            false
        )

        binding.quietOrTalkative.setOnClickListener {
            if (viewModel.talkEnabled) {
                viewModel.talkEnabled = false
                binding.quietOrTalkative.text = getString(R.string.clickForTalkativeMode)
            } else {
                viewModel.talkEnabled = true
                binding.quietOrTalkative.text = getString(R.string.clickForQuietMode)
            }
        }

        binding.editHistory.setOnClickListener {
            viewModel.buttonTop = binding.buttonPlay.top
            findNavController().navigate(R.id.action_chooseFragment_to_editFragment)
        }

        binding.buttonCustom.setOnClickListener {
            viewModel.buttonTop = binding.buttonPlay.top
            onCustomClicked()
        }
        binding.buttonPlay.setOnClickListener {
            viewModel.buttonTop = binding.buttonPlay.top
            onPlayClicked()
        }
        radioGroup = binding.boardChoice
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectBoardSize(checkedId)
        }

        return binding.root
    }

    /**
     * Called when all saved state has been restored into the view hierarchy of the fragment. This
     * can be used to do initialization based on saved state that you are letting the view hierarchy
     * track itself, such as whether check box widgets are currently checked. This is called after
     * [onViewCreated] and before [onStart].
     *
     * First we call our super's implementation of `onViewStateRestored`, then we set [Application]
     * field [application] to the application that owns the `FragmentActivity` that our `Fragment`
     * is currently attached to. We then branch on whether the [SharedViewModel.talkEnabled] property
     * of [viewModel] to is `true` or `false`:
     *  - `true`: sets the text  of the `quietOrTalkative` `Button` in our layout to the [String]
     *  with resource ID [R.string.clickForQuietMode] ("CLICK FOR SILENT MODE").
     *  - `false`: sets the text  of the `quietOrTalkative` `Button` in our layout to the [String]
     *  with resource ID [R.string.clickForTalkativeMode] ("CLICK FOR TALKATIVE MODE").
     *
     * We then use [application] in a call to the [MinesDataBase.getInstance] method to retrieve the
     * [MinesDataBase] singleton (creating it if need be, or returning the previously opened instance)
     * whose reference we save in our field [minesDataBase]. If the [SharedViewModel.minesDatabaseDao]
     * field of [viewModel] is `null` we set it to the [MinesDatabaseDao] field `minesDatabaseDao` of
     * [minesDataBase], and if the `LiveData` field `gameHistory` of [viewModel] is `null` we set it
     * to the result of calling the `getAllGames` method of the `minesDatabaseDao` field of
     * [viewModel].
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        application = requireNotNull(this.activity).application

        if (viewModel.talkEnabled) {
            binding.quietOrTalkative.text = getString(R.string.clickForQuietMode)
        } else {
            binding.quietOrTalkative.text = getString(R.string.clickForTalkativeMode)
        }

        minesDataBase = MinesDataBase.getInstance(application)
        if (viewModel.minesDatabaseDao == null) {
            viewModel.minesDatabaseDao = minesDataBase.minesDatabaseDao
        }
        if (viewModel.gameHistory == null) {
            viewModel.gameHistory = (viewModel.minesDatabaseDao ?: return).getAllGames()
        }
    }

    /**
     * Called when the `buttonPlay` `Button` (ID [R.id.button_play]) is clicked. If our [boardSizeChosen]
     * flag is false the user has not selected a board size before clicking the Play button so we call
     * the `randomGame` method of [viewModel] with the default values [COLUMN_COUNT], [ROW_COUNT], and
     * [MINE_COUNT] before we call the [NavController.navigate] method of the [NavController] of our
     * fragment to navigate to the [GameFragment] fragment.
     */
    private fun onPlayClicked() {
        if (!boardSizeChosen) viewModel.randomGame(COLUMN_COUNT, ROW_COUNT, MINE_COUNT)
        findNavController().navigate(R.id.action_chooseFragment_to_gameFragment)
    }

    /**
     * Called when the `buttonCustom` `Button` (ID [R.id.button_custom]) is clicked. Then we call
     * the [NavController.navigate] method of the [NavController] of our fragment to navigate to the
     * [CustomSizeDialog] dialog fragment.
     */
    private fun onCustomClicked() {
        findNavController().navigate(R.id.action_chooseFragment_to_customSizeDialog)
    }

    /**
     * Called by the [RadioGroup.OnCheckedChangeListener] lambda of the [RadioGroup] field [radioGroup]
     * when the user selects one of its [RadioButton]'s. We simply branch on the [checkedId] of the
     * [RadioButton] selected calling the `randomGame` method of [SharedViewModel] field [viewModel]
     * using hard coded values for its `columnCount`, `rowCount`, and `mines` parameters which
     * correspond to the values specified by the [RadioButton] label.
     *
     * @param checkedId the resource ID of the `RadioButton` which was selected
     */
    private fun selectBoardSize(checkedId: Int) {
        boardSizeChosen = true
        when (checkedId) {
            R.id.board8by8 -> {
                viewModel.randomGame(8, 8, 10)
            }

            R.id.board12by16 -> {
                viewModel.randomGame(12, 16, 30)
            }

            R.id.board16by16 -> {
                viewModel.randomGame(16, 16, 40)
            }

            R.id.board16by30 -> {
                viewModel.randomGame(16, 30, 99)
            }
        }
    }

}
