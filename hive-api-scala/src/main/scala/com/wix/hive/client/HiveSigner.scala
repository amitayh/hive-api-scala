package com.wix.hive.client

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.wix.hive.client.http.HttpRequestDataImplicits.HttpRequestDataStringify
import com.wix.hive.client.http.{HttpRequestData, NamedParameters}
import org.apache.commons.net.util.Base64


class HiveSigner(key: String) {

  private val encryptionMethod = "HMACSHA256"
  private lazy val base64: Base64 = new Base64(true)

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

  private def generateStringToSign(data: HttpRequestData): String = {
    import data._

    val sortedQuery = getValuesSortedByKey(queryString)
    val sortedWixHeaders = getValuesSortedByKey(headers.filterKeys { str => val lower = str.toLowerCase; lower != "x-wix-signature" && lower.startsWith("x-wix-")})
    val sortedParams = (sortedQuery ++ sortedWixHeaders).mkString("\n")
    val post = data.bodyAsString
    val postSeparator = if (post.nonEmpty) "\n" else ""

    s"""$method\n$url\n$sortedParams$postSeparator$post"""
  }

  private def getValuesSortedByKey(params: NamedParameters) = params.toSeq.sortBy(_._1).map(_._2)
}