package organisation.domain

opaque type RecordLabelId = Long
opaque type RecordLabelName = String

case class RecordLabel(id: RecordLabelId, name: RecordLabelName)
