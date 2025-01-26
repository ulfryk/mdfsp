package distribution.adapters.rest.dto


import cats.data.Validated.{invalidNel, validNel}
import cats.data.ValidatedNel
import org.http4s.dsl.io.OptionalValidatingQueryParamDecoderMatcher
import org.http4s.{ParseFailure, QueryParamDecoder}

opaque type SearchQueryParam = String

object SearchQueryParam:
  def toString(query: SearchQueryParam): String = query
  
  object Matcher extends OptionalValidatingQueryParamDecoderMatcher[SearchQueryParam]("search")

  private def validateFooItemTextFromString(s: String): ValidatedNel[ParseFailure, SearchQueryParam] =
    if (s.isEmpty || s.isBlank) invalidNel {
      ParseFailure(sanitized = s"empty search text '$s'", details = "")
    }
    else validNel(s)

  private given textQueryParamDecoder: QueryParamDecoder[SearchQueryParam] =
    QueryParamDecoder[String].emapValidatedNel(validateFooItemTextFromString)