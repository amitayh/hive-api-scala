package com.wix.hive.model

import com.wix.hive.model.HiveClientErrorCodes._

case class HiveClientException(errorCode: Int = GENERIC, message: String)
  extends RuntimeException(s"Hive Client error with code: '$errorCode' and message: '$message'")

object HiveClientErrorCodes {
  val GENERIC = -1
}
