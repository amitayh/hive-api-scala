package com.wix.hive.security

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.client.http.HttpRequestDataImplicits.HttpRequestDataStringify


class HiveRequestSigner(key: String) {
  private val signer = new HiveSigner(key)

  private val includes = Set("application-id", "instance-id", "event-type", "timestamp", "event-id", "bi-token", "staging-environment")
  private val excludes = Set("signature")

  private val headerPrefix = "x-wix-"


  def getSignature(data: HttpRequestData): String = {
    val stringToSign = generateStringToSign(data)

    signer.signString(stringToSign)
  }

  implicit object StringCaseInsensitiveOrder extends Ordering[String] {
    def compare(x: String, y: String) = x.compareToIgnoreCase(y)
  }

  private[security] def generateStringToSign(data: HttpRequestData): String = {
    import data._

    val queryPart = queryString.toList
      .filterNot {case (name, _) => excludes contains name.toLowerCase }

    val headerPart = headers.toList
      .filter {case (name, _) => includes map(headerPrefix + _) contains name.toLowerCase }

    val paramsAndHeaders = (queryPart ++ headerPart).sortBy { case (name, _) => name }.map { case (_, value) => value }

    (Seq(method.toString, url) ++ paramsAndHeaders :+ data.bodyAsString)
      .filterNot(_.isEmpty)
      .mkString("\n")
  }
}