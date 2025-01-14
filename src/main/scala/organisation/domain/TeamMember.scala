package organisation.domain

opaque type TeamMemberId = Long

// I already see that most probably it should be separate entities/aggregates
case class TeamMember(
  id: TeamMemberId,
  userId: PlatformUserId,
  organisationId: ArtistId | RecordLabelId
)
