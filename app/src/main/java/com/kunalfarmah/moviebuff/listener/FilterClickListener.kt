package com.kunalfarmah.moviebuff.listener

import com.kunalfarmah.moviebuff.model.FilterItem

interface FilterClickListener {
    fun onFilterClick(genre: FilterItem, pos: Int)
}