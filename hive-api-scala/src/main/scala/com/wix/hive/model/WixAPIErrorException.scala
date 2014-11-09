package com.wix.hive.model

case class WixAPIErrorException(errorCode: Int, message: Option[String] = None, wixErrorCode: Option[Int] = None)
  extends Exception(s"Wix API Error with StatusCode: '$errorCode', message: '${message.getOrElse("")}', Wix code: '${wixErrorCode.getOrElse("")}'") {
  
  def errorCodeString(): Option[String] = WixAPIErrorException.ErrorCodeStringRepresentation.get(errorCode)
}

object WixAPIErrorException {
  val ErrorCodeStringRepresentation = Map(
    400 -> "Bad Request",
    403 -> "Forbidden",
    404 -> "NotFound",
    408 -> "Request timeout",
    429 -> "Too many requests",
    500 -> "Internal server error",
    502 -> "Bad gateway",
    503 -> "Service unavailable",
    504 -> "Gateway timeout"
  )
}