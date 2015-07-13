package com.wix.hive.security

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.net.util.Base64

/**
 * User: maximn
 * Date: 7/13/15
 */
class HiveSigner(key: String) {
  private val encryptionMethod = "HMACSHA256"
  private val encodingForSignature = "UTF-8"

  // base64 & mac are NOT THREAD SAFE
  private def base64: Base64 = new Base64(true)
  private def mac = {
    val secret = new SecretKeySpec(key.getBytes, encryptionMethod)
    val instance = Mac.getInstance(encryptionMethod)
    instance.init(secret)
    instance
  }

  def signString(str: String): String = {
    val bytes = str.getBytes(encodingForSignature)
    val signed = mac.doFinal(bytes)
    base64.encodeToString(signed).trim
  }
}
