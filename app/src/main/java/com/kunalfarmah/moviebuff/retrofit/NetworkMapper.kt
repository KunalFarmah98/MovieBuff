package com.kunalfarmah.moviebuff.retrofit

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
            popularity = entity.popularity
        )
    }

    override fun mapToEntity(domainModel: Movies): MovieEntity {
        var path = domainModel.posterPath
        if(null==path)
            path=""
        return MovieEntity(
            id = domainModel.id!!,
            title = domainModel.title!!,
            image = path,
            popularity = domainModel.popularity!!,
        )
    }

}





















