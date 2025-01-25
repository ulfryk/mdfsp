package common.http4s

import cats.data.*
import cats.data.Validated.*
import cats.implicits.*

sealed trait InvalidPathId:
  val message: String

final case class MissingId() extends InvalidPathId:
  val message = s"Id is not provided"

final case class MalformedId(found: String) extends InvalidPathId:
  val message = s"$found is not a valid Id"

object ApiId:
  def unapply(str: String): Option[ValidatedNec[InvalidPathId, Long]] =
    if (str.isEmpty) invalidNec(MissingId()).some
    else str.toLongOption.toValidNec(MalformedId(str)).some
