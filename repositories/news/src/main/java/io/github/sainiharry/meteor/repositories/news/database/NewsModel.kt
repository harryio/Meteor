package io.github.sainiharry.meteor.repositories.news.database

import androidx.room.Embedded
import androidx.room.Entity
import io.github.sainiharry.meteor.repositories.news.News

@Entity
internal data class NewsModel(@Embedded val news: News)