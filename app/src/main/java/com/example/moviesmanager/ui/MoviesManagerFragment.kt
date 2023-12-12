package com.example.moviesmanager.ui


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesmanager.R
import com.example.moviesmanager.adapter.FilmAdapter
import com.example.moviesmanager.constants.Constants.ID_FILM
import com.example.moviesmanager.databinding.FragmentMoviesManagerBinding
import com.example.moviesmanager.viewmodel.FilmsViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.Collections
import kotlin.math.abs
import kotlin.math.roundToInt

class MoviesManagerFragment : Fragment() {

    private var _binding: FragmentMoviesManagerBinding? = null
    private val binding get() = _binding!!
    lateinit var filmAdapter: FilmAdapter
    private lateinit var viewModel: FilmsViewModel
    private lateinit var dragHelper: ItemTouchHelper
    private lateinit var swipeHelper: ItemTouchHelper

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
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp()

        val deleteIcon = resources.getDrawable(R.drawable.ic_delete_foreground, null)
        val editIcon = resources.getDrawable(R.drawable.ic_edit_foreground, null)

        val deleteColor = resources.getColor(android.R.color.holo_red_light)
        val editColor = resources.getColor(android.R.color.holo_green_light)

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


        swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Snackbar.make(
                    binding.moviesManagerFragmentScreen,
                    if (direction == ItemTouchHelper.RIGHT) "Deletar" else "Editar",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                //Background color based upon direction swiped
                when {
                    abs(dX) < width / 3 -> canvas.drawColor(Color.GRAY)
                    dX > width / 3 -> canvas.drawColor(deleteColor)
                    else -> canvas.drawColor(editColor)
                }

                //Printing the icons
                val textMargin = resources.getDimension(R.dimen.text_margin)
                    .roundToInt()
                deleteIcon.bounds = Rect(
                    textMargin,
                    viewHolder.itemView.top + textMargin,
                    textMargin + deleteIcon.intrinsicWidth,
                    viewHolder.itemView.top + deleteIcon.intrinsicHeight
                )

                editIcon.bounds = Rect(
                    width - textMargin - editIcon.intrinsicWidth,
                    viewHolder.itemView.top ,
                    width - textMargin,
                    viewHolder.itemView.top + editIcon.intrinsicHeight
                )

                //Drawing icon based upon direction swiped
                if (dX > 0) deleteIcon.draw(canvas) else editIcon.draw(canvas)

                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

        dragHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                viewHolder.itemView.elevation = 8F

                val from = viewHolder.adapterPosition
                val to = target.adapterPosition

                viewModel.allFilms.value?.let { Collections.swap(it, from, to) }
                filmAdapter.notifyItemMoved(from, to)
                return true
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                viewHolder?.itemView?.elevation = 0F
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

        })

        swipeHelper.attachToRecyclerView(recyclerView)
        dragHelper.attachToRecyclerView(recyclerView)

    }

    private fun Int.dp() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(), resources.displayMetrics
    ).roundToInt()
}
