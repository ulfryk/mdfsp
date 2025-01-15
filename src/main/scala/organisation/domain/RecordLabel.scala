package organisation.domain

opaque type RecordLabelId = Long
object RecordLabelId:
  def apply(id: Long): RecordLabelId = id
  
opaque type RecordLabelName = String
object RecordLabelName:
  def apply(name: String): RecordLabelName = name

case class RecordLabel(id: RecordLabelId, name: RecordLabelName)
