package com.wix.hive.client

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.google.common.base.Charsets
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.client.http.HttpRequestDataImplicits.HttpRequestDataStringify
import org.apache.commons.net.util.Base64


class HiveSigner(key: String) {
  private val encryptionMethod = "HMACSHA256"

  private val includes = Set("application-id", "instance-id", "event-type", "timestamp", "event-id", "bi-token", "staging-environment")
  private val excludes = Set("signature")

  private val headerPrefix = "x-wix-"

  // base64 & mac are NOT THREAD SAFE
  def base64: Base64 = new Base64(true)
  def mac = {
    val secret = new SecretKeySpec(key.getBytes, encryptionMethod)
    val instance = Mac.getInstance(encryptionMethod)
    instance.init(secret)
    instance
  }

  def getSignature(data: HttpRequestData): String = {
    val stringToSign = generateStringToSign(data)

    val result: Array[Byte] = mac.doFinal(stringToSign.getBytes(Charsets.US_ASCII))
    base64.encodeToString(result).trim
  }

  private[client] def generateStringToSign(data: HttpRequestData): String = {
    import data._

    val queryPart = queryString.toList
      .filterNot {case (name, _) => excludes contains name.toLowerCase }

    val headerPart = headers.toList
      .filter {case (name, _) => includes map(headerPrefix + _) contains name.toLowerCase }
      .filterNot {case (name, _) => excludes map(headerPrefix + _) contains name.toLowerCase }

    val paramsAndHeaders = (queryPart ++ headerPart).sortBy { case (name, _) => name }.map { case (_, value) => value }

    (Seq(method.toString, url) ++ paramsAndHeaders :+ data.bodyAsString)
      .filterNot(_.isEmpty)
      .mkString("\n")
  }
}