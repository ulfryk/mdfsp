package organisation.domain

opaque type ArtistId = Long
opaque type ArtistName = String

case class Artist(id: ArtistId, name: ArtistName)
