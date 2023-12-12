package com.example.moviesmanager.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesmanager.adapter.FilmAdapter
import com.example.moviesmanager.constants.Constants.ID_FILM
import com.example.moviesmanager.R
import com.example.moviesmanager.databinding.FragmentMoviesManagerBinding
import com.example.moviesmanager.viewmodel.FilmsViewModel

class MoviesManagerFragment : Fragment(){

    private var _binding: FragmentMoviesManagerBinding? = null
    private val binding get() = _binding!!
    lateinit var filmAdapter: FilmAdapter
    private lateinit var viewModel: FilmsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoviesManagerBinding.inflate(inflater, container, false)
        binding.addFloatingActionButton.setOnClickListener { findNavController().navigate(R.id.action_moviesManagerFragment_to_newFilmFragment) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureRecyclerView()

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_films_menu, menu)

                val searchView = menu.findItem(R.id.actionSearch).actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        filmAdapter.filter.filter(p0)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }


        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun configureRecyclerView() {
        viewModel = ViewModelProvider(this)[FilmsViewModel::class.java]

        viewModel.allFilms.observe(viewLifecycleOwner) { list ->
            list?.let {
                filmAdapter.updateList(list)
            }
        }

        val recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        filmAdapter = FilmAdapter()
        recyclerView.adapter = filmAdapter

        val listener = object : FilmAdapter.FilmListener {
            override fun onItemClick(pos: Int) {
                val itemClick = filmAdapter.filmsListFilterable[pos]

                val bundle = Bundle()
                bundle.putInt(ID_FILM, itemClick.id)

                findNavController().navigate(
                    R.id.action_moviesManagerFragment_to_filmDetailsFragment,
                    bundle
                )
            }
        }
        filmAdapter.setClickListener(listener)
    }
}
