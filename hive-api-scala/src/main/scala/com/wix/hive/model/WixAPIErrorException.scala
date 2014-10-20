package com.wix.hive.model

case class WixAPIErrorException(errorCode: Int, message: Option[String] = None, wixErrorCode: Option[Int] = None)
  extends Exception(s"Wix API Error with StatusCode: '${errorCode}', message: '${message.getOrElse("")}', Wix code: '${wixErrorCode.getOrElse("")}'")