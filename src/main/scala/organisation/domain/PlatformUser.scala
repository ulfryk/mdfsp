package organisation.domain

opaque type PlatformUserId = Long
opaque type PlatformUserName = String

case class PlatformUser(id: PlatformUserId, name: PlatformUserName)
