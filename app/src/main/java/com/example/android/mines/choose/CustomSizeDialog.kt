package com.example.android.mines.choose

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.mines.R
import com.example.android.mines.SharedViewModel
import com.example.android.mines.databinding.CustomSizeDialogBinding

/**
 * This [DialogFragment] allows the user to configure the game board however they want it.
 */
class CustomSizeDialog : DialogFragment()  {
    /**
     * The [SharedViewModel] instance we share with all our other fragments.
     */
    private val viewModel: SharedViewModel by activityViewModels()

    /**
     * The [CustomSizeDialogBinding] binding that is created from the inflation of our layout file
     * layout/custom_size_dialog.xml
     */
    private lateinit var binding: CustomSizeDialogBinding

    /**
     * The [SharedPreferences] interface we use to access and modify our preference data
     */
    private lateinit var preferences: SharedPreferences

    /**
     * The number of columns on the game board
     */
    private var customColumns : Int = 6

    /**
     * The number of rows on the game board
     */
    private var customRows : Int = 6

    /**
     * The number of sectors on the game board which have Mines in them.
     */
    private var customMines : Int = 4


    /**
     * Called to have the fragment instantiate its user interface view. First we initialize our
     * [SharedPreferences] field [preferences] to a a [SharedPreferences] object for accessing
     * preferences that are private to this activity. We then initialize our field [binding] to the
     * [CustomSizeDialogBinding] that its `inflate` method returns when  it uses our [LayoutInflater]
     * parameter [inflater] to inflate our layout file [R.layout.custom_size_dialog] using our
     * [ViewGroup] parameter [container] for its LayoutParams without attaching to it. Finally we
     * return the outermost [View] of the layout file associated with [binding].
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        binding = CustomSizeDialogBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. This gives subclasses a chance to initialize themselves once they
     * know their view hierarchy has been completely created. The fragment's view hierarchy is not
     * however attached to its parent at this point. First we call our method [restoreChoices] to
     * read the choices made for the previous custom game board from our [SharedPreferences] into
     * our fields [customColumns], [customRows] and [customMines] and write them as the initial
     * contents of EditTexts used by the user to select them. Then we set the `OnClickListener` of
     * the `abortButton` `Button` in our layout to a lambda which calls [dismiss] in order to return
     * to the `ChooseFragment` without doing anything more. We set  the `OnClickListener` of
     * the `playGameButton` `Button` in our layout to a lambda which fetches the text in the
     * `columnsNumber` `EditText` in our layout file in order to set [customColumns] to the [Int]
     * returned by our [sane] method after it verifies it, fetches the text in the `rowsNumber`
     * `EditText` in our layout file in order to set [customRows] to the [Int] returned by our
     * [sane] method after it verifies it, and fetches the text in the `minesNumber` `EditText` in
     * our layout file in order to set [customMines] to the [Int] returned by our [sane] method
     * after it verifies it. It then calls our [saveChoices] method to write the choices out to our
     * SharedPreferences file, calls the `randomGame` method of our [SharedViewModel] field
     * [viewModel] to have it initialize the game state to [customColumns], [customRows], and
     * [customMines], and then calls the `navigate` method of our `NavController` to navigate to
     * `GameFragment`.
     *
     * @param view The View returned by [onCreateView]
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        restoreChoices()
        binding.abortButton.setOnClickListener {
            dismiss()
        }
        binding.playGameButton.setOnClickListener {
            customColumns = sane(
                binding.columnsNumber.text.toString(),
                4,
                32,
                8
            )
            customRows = sane(
                binding.rowsNumber.text.toString(),
                4,
                32,
                8
            )
            customMines = sane(
                binding.minesNumber.text.toString(),
                1,
                customRows*customColumns/2,
                customRows*customColumns/10
            )
            saveChoices()
            viewModel.randomGame(customColumns, customRows, customMines)
            findNavController().navigate(R.id.action_customSizeDialog_to_gameFragment)
        }
    }

    /**
     * Verifies that the [String] parameter [inputString] is a number string between [low] and [hi]
     * before converting it to an [Int] to return, but if it is not it returns [default] instread.
     *
     * @param inputString [String] entered into an `EditText` by the use.
     * @param low the lowest value the caller want to accept
     * @param hi the highest value the caller want to accept
     * @param default the value to return if the [inputString] is unacceptable
     *
     * @return the [Int] value of [inputString] if [inputString] is a valid number string between
     * [low] and [hi], otherwise [default]
     */
    private fun sane(inputString: String, low: Int, hi: Int, default: Int): Int {
        val new: Int = if (inputString.isDigitsOnly() && inputString != "") {
            inputString.toInt()
        } else {
            low
        }
        return if (new in low..hi) {
            new
        } else {
            default
        }
    }

    /**
     * Reads the choices made for the last custom game played from our [SharedPreferences] file,
     * saves them in our fields and sets them as the initial text in the EditTexts used by the
     * user to choose the game board size.
     */
    private fun restoreChoices() {
        customColumns = preferences.getInt(CUSTOM_COLUMNS, 6)
        binding.columnsNumber.setText(customColumns.toString())
        customRows = preferences.getInt(CUSTOM_ROWS, 6)
        binding.rowsNumber.setText(customRows.toString())
        customMines = preferences.getInt(CUSTOM_MINES, 6)
        binding.minesNumber.setText(customMines.toString())
    }

    /**
     * Saves the choices made for game board size contained in our fields [customColumns],
     * [customRows] and [customMines] in our [SharedPreferences] file.
     */
    private fun saveChoices() {
        val editor = preferences.edit()
        editor.putInt(CUSTOM_COLUMNS, customColumns)
        editor.putInt(CUSTOM_ROWS, customRows)
        editor.putInt(CUSTOM_MINES, customMines)
        editor.apply()
    }

    companion object {
        /**
         * The key in our [SharedPreferences] file for our [customColumns] field.
         */
        const val CUSTOM_COLUMNS = "custom_columns"

        /**
         * The key in our [SharedPreferences] file for our [customRows] field.
         */
        const val CUSTOM_ROWS = "custom_rows"

        /**
         * The key in our [SharedPreferences] file for our [customMines] field.
         */
        const val CUSTOM_MINES = "custom_mines"
    }
}