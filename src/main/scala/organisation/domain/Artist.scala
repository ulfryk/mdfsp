package organisation.domain

import cats.syntax.all.*

opaque type ArtistId = Long
object ArtistId:
  def apply(id: Long): ArtistId = id
  def unapply(id: ArtistId): Option[Long] = id.some
  
opaque type ArtistName = String
object ArtistName:
  def apply(name: String): ArtistName = name
  def unapply(name: ArtistName): Option[String] = name.some

case class Artist(id: ArtistId, name: ArtistName)
