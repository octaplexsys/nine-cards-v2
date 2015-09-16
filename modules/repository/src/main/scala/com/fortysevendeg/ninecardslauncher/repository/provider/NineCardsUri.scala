package com.fortysevendeg.ninecardslauncher.repository.provider

object NineCardsUri {

  val authorityPart = "com.fortysevendeg.ninecardslauncher2"

  val contentPrefix = "content://"

  val cacheCategoryUriString = s"$contentPrefix$authorityPart/${CacheCategoryEntity.table}"

  val cardUriString = s"$contentPrefix$authorityPart/${CardEntity.table}"

  val collectionUriString = s"$contentPrefix$authorityPart/${CollectionEntity.table}"

  val geoInfoUriString = s"$contentPrefix$authorityPart/${GeoInfoEntity.table}"
}