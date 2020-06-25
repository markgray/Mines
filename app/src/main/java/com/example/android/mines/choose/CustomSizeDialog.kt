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
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. It is recommended to **only** inflate the layout in this
     * method and move logic that operates on the returned View to [onViewCreated]. First we
     * initialize our variable `val binding` to the [CustomSizeDialogBinding] that its
     * `inflate` method returns when  it uses our [LayoutInflater] parameter  [inflater] to inflate
     * our layout file [R.layout.custom_size_dialog] using our [ViewGroup] parameter [container] for
     * its LayoutParams without attaching to it. Then we set the `OnClickListener` of the
     * `dismissButton` `Button` in our layout to a lambda which fetches the text in the `columnsNumber`
     * `EditText` in our layout file in order to set `val columns` to the [Int] returned by our
     * [sane] method after it verifies it, fetches the text in the `rowsNumber` `EditText` in our
     * layout file in order to set `val rows` to the [Int] returned by our [sane] method after it
     * verifies it, and fetches the text in the `minesNumber` `EditText` in our layout file in order
     * to set `val mines` to the [Int] returned by our [sane] method after it verifies it. We then
     * call the `randomGame` method of our [SharedViewModel] field [viewModel] to have it initialize
     * the game state to `columns`, `rows`, and `mines`. Finally we dismiss this [DialogFragment].
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        restoreChoices()
        binding.dismissButton.setOnClickListener {
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
            dismiss()
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

    private fun restoreChoices() {
        customColumns = preferences.getInt(CUSTOM_COLUMNS, 6)
        binding.columnsNumber.setText(customColumns.toString())
        customRows = preferences.getInt(CUSTOM_ROWS, 6)
        binding.rowsNumber.setText(customRows.toString())
        customMines = preferences.getInt(CUSTOM_MINES, 6)
        binding.minesNumber.setText(customMines.toString())
    }

    @Suppress("unused")
    private fun saveChoices() {
        val editor = preferences.edit()
        editor.putInt(CUSTOM_COLUMNS, customColumns)
        editor.putInt(CUSTOM_ROWS, customRows)
        editor.putInt(CUSTOM_MINES, customMines)
        editor.apply()
    }

    companion object {
        const val CUSTOM_COLUMNS = "custom_columns"
        const val CUSTOM_ROWS = "custom_rows"
        const val CUSTOM_MINES = "custom_rows"
    }
}