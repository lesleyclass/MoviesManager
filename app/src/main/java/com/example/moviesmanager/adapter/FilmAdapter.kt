package com.example.moviesmanager.adapter

import android.R
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesmanager.data.Film
import com.example.moviesmanager.databinding.FilmItemBinding

class FilmAdapter: RecyclerView.Adapter<FilmAdapter.FilmViewHolder>(),
    Filterable {

    var listener: FilmListener?=null
    var filmsList = ArrayList<Film>()
    var filmsListFilterable = ArrayList<Film>()
    private lateinit var binding: FilmItemBinding

    fun updateList(newList: List<Film> ){
        filmsList = newList as ArrayList<Film>
        filmsListFilterable = filmsList
        notifyDataSetChanged()
    }

    fun setClickListener(listener: FilmListener)
    {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilmViewHolder {
        binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  FilmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val bitmap = BitmapFactory.decodeResource(holder.itemView.context.resources,
            if (filmsListFilterable[position].isBeenWatched) {
                R.drawable.presence_video_online
            } else {
                R.drawable.presence_video_busy
            }
        )

        holder.nameVH.text = filmsListFilterable[position].name
        holder.releaseYearVH.text = filmsListFilterable[position].releaseYear
        holder.iconCheck.setImageBitmap(bitmap)

    }

    override fun getItemCount(): Int {
        return filmsListFilterable.size
    }

    inner class FilmViewHolder(view: FilmItemBinding): RecyclerView.ViewHolder(view.root)
    {
        val nameVH = view.name
        val releaseYearVH = view.releaseYear
        val iconCheck = view.iconCheck

        init {
            view.root.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }
    }

    interface FilmListener
    {
        fun onItemClick(pos: Int)
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                filmsListFilterable = if (p0.toString().isEmpty())
                    filmsList
                else {
                    val resultList = ArrayList<Film>()
                    for (row in filmsList)
                        if (row.name.lowercase().contains(p0.toString().lowercase()))
                            resultList.add(row)
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filmsListFilterable
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filmsListFilterable = p1?.values as ArrayList<Film>
                notifyDataSetChanged()
            }

        }
    }
}
