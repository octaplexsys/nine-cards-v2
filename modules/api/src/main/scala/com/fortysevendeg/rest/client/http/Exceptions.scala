package com.fortysevendeg.rest.client.http

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException


case class HttpClientException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsHttpClientExceptions {
  implicit def httpClientExceptionConverter = (t: Throwable) => HttpClientException(t.getMessage, Option(t))
}