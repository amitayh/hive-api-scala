package com.wix.hive.model

import com.wix.hive.model.HiveClientErrorCodes._

case class HiveClientException(errorCode: Int = Generic, message: String, cause: Throwable)
  extends RuntimeException(s"Hive Client error with code: '$errorCode' and message: '$message'", cause)

object HiveClientErrorCodes {
  val Generic = -1
}
