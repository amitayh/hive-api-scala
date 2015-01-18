package com.wix.hive.client

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.client.http.HttpRequestDataImplicits.HttpRequestDataStringify
import org.apache.commons.net.util.Base64


class HiveSigner(key: String) {

  private val encryptionMethod = "HMACSHA256"
  private lazy val base64: Base64 = new Base64(true)

  private val includes = Set("application-id", "instance-id", "event-type", "timestamp", "event-id")
  private val excludes = Set("signature")

  private val headerPrefix = "x-wix-"
  private def withHeaderPrefix(names: Set[String]) = names map (headerPrefix + _)

  private lazy val mac = {
    val secret = new SecretKeySpec(key.getBytes, encryptionMethod)
    val instance = Mac.getInstance(encryptionMethod)
    instance.init(secret)
    instance
  }

  def getSignature(data: HttpRequestData): String = {
    val stringToSign = generateStringToSign(data)

    val result: Array[Byte] = mac.doFinal(stringToSign.getBytes)
    base64.encodeToString(result).trim
  }

  private[client] def generateStringToSign(data: HttpRequestData): String = {
    import data._

    val queryPart = queryString.toList
      .filterNot {case (name, _) => excludes contains name.toLowerCase }
      .sortBy { case (name, _) => name }
      .map { case (_, value) => value }

    val headerPart = headers.toList
      .filter {case (name, _) => includes map(headerPrefix + _) contains name.toLowerCase }
      .filterNot {case (name, _) => excludes map(headerPrefix + _) contains name.toLowerCase }
      .sortBy { case (name, _) => name }
      .map { case (_, value) => value }

    (Seq(method.toString, url) ++ queryPart ++ headerPart :+ data.bodyAsString)
      .filterNot(_.isEmpty)
      .mkString("\n")
  }
}