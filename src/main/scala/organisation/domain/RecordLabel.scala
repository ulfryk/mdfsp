package organisation.domain

import cats.syntax.all.*

opaque type RecordLabelId = Long
object RecordLabelId:
  def apply(id: Long): RecordLabelId = id
  def unapply(id: RecordLabelId): Option[Long] = id.some
  
opaque type RecordLabelName = String
object RecordLabelName:
  def apply(name: String): RecordLabelName = name
  def unapply(name: RecordLabelName): Option[String] = name.some

case class RecordLabel(id: RecordLabelId, name: RecordLabelName)
