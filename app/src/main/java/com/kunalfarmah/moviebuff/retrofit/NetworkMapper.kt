package com.kunalfarmah.moviebuff.retrofit

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kunalfarmah.moviebuff.room.MovieEntity
import com.kunalfarmah.moviebuff.util.EntityMapper
import javax.inject.Inject

class NetworkMapper
@Inject
constructor():
    EntityMapper<MovieEntity, Movies> {

    override fun mapFromEntity(entity: MovieEntity): Movies {
        return Movies(
            id = entity.id,
            title = entity.title,
            posterPath = entity.image,
            popularity = entity.popularity,
            releaseDate = entity.releaseDate,
            genreIds = Gson().fromJson(entity.genreIds, object: TypeToken<List<Int?>?>() {}.type)
        )
    }

    override fun mapToEntity(domainModel: Movies): MovieEntity {
        var path = domainModel.posterPath
        if(null==path)
            path=""
        return MovieEntity(
            id = domainModel.id ?: 0,
            title = domainModel.title ?: "",
            image = path,
            popularity = domainModel.popularity ?: 0.0,
            releaseDate = domainModel.releaseDate ?: "",
            genreIds = Gson().toJson(domainModel.genreIds ?: ArrayList<Int>())
        )
    }

}





















