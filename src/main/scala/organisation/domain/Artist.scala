package organisation.domain

opaque type ArtistId = Long
object ArtistId:
  def apply(id: Long): ArtistId = id
  
opaque type ArtistName = String
object ArtistName:
  def apply(name: String): ArtistName = name

case class Artist(id: ArtistId, name: ArtistName)
