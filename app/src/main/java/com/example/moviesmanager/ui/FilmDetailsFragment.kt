package com.example.moviesmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.moviesmanager.constants.Constants.ID_FILM
import com.example.moviesmanager.data.Film
import com.example.moviesmanager.R
import com.example.moviesmanager.constants.Constants.IS_GONE_FILM_DETAILS
import com.example.moviesmanager.databinding.FragmentFilmDetailsBinding
import com.example.moviesmanager.viewmodel.FilmsViewModel
import com.google.android.material.snackbar.Snackbar

class FilmDetailsFragment : Fragment() {
    private var _binding: FragmentFilmDetailsBinding? = null
    private val binding get() = _binding!!
    lateinit var film: Film
    private lateinit var nameEditText: EditText
    private lateinit var releaseYearEditText: EditText
    private lateinit var isBeenWatchedRadioGroup: RadioGroup
    private lateinit var radioButtonYes: RadioButton
    private lateinit var radioButtonNo: RadioButton

    private lateinit var viewModel: FilmsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[FilmsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = binding.commonLayout.editTextNome
        releaseYearEditText = binding.commonLayout.editTextReleaseYear
        isBeenWatchedRadioGroup = binding.commonLayout.isBeenWatchedRadioGroup
        radioButtonYes = binding.commonLayout.radioButtonYes
        radioButtonNo = binding.commonLayout.radioButtonNo

        isBeenWatchedRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.radioButtonYes -> { film.isBeenWatched = true }
                R.id.radioButtonYes -> { film.isBeenWatched = false }
            }
        }

        val idFilm = requireArguments().getInt(ID_FILM)
        val isGoneFilmDetails = requireArguments().getBoolean(IS_GONE_FILM_DETAILS)

        viewModel.getFilmById(idFilm)

        viewModel.film.observe(viewLifecycleOwner) { result ->
            result?.let {
                film = result
                nameEditText.setText(film.name)
                releaseYearEditText.setText(film.releaseYear)
                if (film.isBeenWatched){
                    radioButtonYes.isChecked = true
                    radioButtonNo.isChecked = false
                }else{
                    radioButtonNo.isChecked = true
                    radioButtonYes.isChecked = false
                }
            }
        }

        if (isGoneFilmDetails) unableDetailsElement()

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.film_details_menu, menu).apply {
                    if (isGoneFilmDetails) {
                        menu[0].isVisible = false
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.actionEditFilm -> {
                        actionEditFilm()
                        true
                    }
                    R.id.actionDeleteFilm ->{
                        actionDeleteFilm()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun actionEditFilm() {
        film.name = nameEditText.text.toString()
        film.releaseYear = releaseYearEditText.text.toString()
        film.isBeenWatched = radioButtonYes.isChecked
        viewModel.update(film)

        Snackbar.make(binding.root, getString(R.string.action_edit_film_label), Snackbar.LENGTH_SHORT).show()

        findNavController().popBackStack()
    }

    private fun actionDeleteFilm() {
        viewModel.delete(film)

        Snackbar.make(binding.root, getString(R.string.action_delete_film_label), Snackbar.LENGTH_SHORT).show()

        findNavController().popBackStack()
    }

    private fun unableDetailsElement() {
        nameEditText.isClickable = false
        nameEditText.isEnabled = false
        releaseYearEditText.isClickable = false
        releaseYearEditText.isEnabled = false
        radioButtonNo.isClickable = false
        radioButtonNo.isEnabled = false
        radioButtonYes.isClickable = false
        radioButtonYes.isEnabled = false
    }
}
