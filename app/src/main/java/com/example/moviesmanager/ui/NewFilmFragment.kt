package com.example.moviesmanager.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.moviesmanager.R
import com.example.moviesmanager.adapter.SpinnerAdapter
import com.example.moviesmanager.data.Film
import com.example.moviesmanager.databinding.FragmentNewFilmBinding
import com.example.moviesmanager.viewmodel.FilmsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class NewFilmFragment : Fragment() {
    private var _binding: FragmentNewFilmBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: FilmsViewModel
    private var spinnerGender: Spinner? = null
    private var spinnerScore: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FilmsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        spinnerGender = view.findViewById(R.id.spinnerGender)
        spinnerGender?.let { it.adapter = SpinnerAdapter(requireContext(), viewModel.genders) }
        spinnerScore = view.findViewById(R.id.spinnerScore)
        spinnerScore?.let { it.adapter = SpinnerAdapter(requireContext(), viewModel.scores) }

        spinnerGender?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
               Log.d("TEST", "spinnerGender: $position")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("TEST", "spinnerGender - onNothingSelected")
            }
        }

        spinnerScore?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.d("TEST", "spinnerScore: $spinnerScore")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("TEST", "spinnerGender - onNothingSelected")
            }
        }
        binding.commonLayout.radioButtonYes.isChecked = true

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_film_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.actionSaveFilm -> {
                        view.clearFocus()
                        val inputMethodManager = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

                        val film = Film(
                            id = generateId(),
                            name = binding.commonLayout.editTextNome.text.toString(),
                            releaseYear = binding.commonLayout.editTextReleaseYear.text.toString(),
                            studio = binding.commonLayout.editTextStudio.text.toString(),
                            duration = binding.commonLayout.editTextDuration.text.toString().toInt(),
                            isBeenWatched = binding.commonLayout.radioButtonYes.isChecked,
                            score = binding.commonLayout.spinnerScore.selectedItem.toString().toInt(),
                            scoreIndex = binding.commonLayout.spinnerScore.selectedItemPosition,
                            gender = binding.commonLayout.spinnerGender.selectedItem.toString(),
                            genderIndex = binding.commonLayout.spinnerGender.selectedItemPosition,
                        )

                        if(!viewModel.insert(film)){
                            Snackbar.make(binding.commonLayoutScreen, getString(R.string.action_save_film_label), Snackbar.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        } else {
                            Snackbar.make(binding.commonLayoutScreen, "Esse titulo de filme já existe na sua lista", Snackbar.LENGTH_SHORT).show()
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun generateId(): Int {
        return Random.nextInt()
    }
}
