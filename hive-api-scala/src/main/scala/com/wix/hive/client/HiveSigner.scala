package com.wix.hive.client

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.wix.hive.client.http.HttpRequestDataImplicits.HttpRequestDataStringify
import com.wix.hive.client.http.{HttpRequestData, NamedParameters}
import org.apache.commons.net.util.Base64


object HiveSigner {

  def getSignature(key: String, data: HttpRequestData): String = {
    val stringToSign = generateStringToSign(data)

    val secret = new SecretKeySpec(key.getBytes, "HMACSHA256")
    val mac = Mac.getInstance("HMACSHA256")
    mac.init(secret)

    val result: Array[Byte] = mac.doFinal(stringToSign.getBytes)
    new Base64(true).encodeToString(result).trim
  }

  def generateStringToSign(data: HttpRequestData): String = {
    import data._

    val sortedQuery = toSortedSeq(queryString)
    val sortedWixHeaders = toSortedSeq(headers.filterKeys(_.toLowerCase.startsWith("x-wix-")))
    val sortedParams =  (sortedQuery ++ sortedWixHeaders).mkString("\n")
    val post = data.bodyAsString
    val postSeparator = if (post.nonEmpty) "\n" else ""


    s"""$method\n$url\n$sortedParams$postSeparator$post"""
  }

  def toSortedSeq(params: NamedParameters) = params.toSeq.sortBy(_._1).map(_._2)
}