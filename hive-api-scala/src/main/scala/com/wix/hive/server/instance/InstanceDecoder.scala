//package com.wix.hive.server.instance
//
//import com.wix.hive.Encryptor
//import com.wix.hive.json.JacksonObjectMapper
//import com.wix.hive.server.webhooks.exceptions.InvalidSignatureException
//import org.apache.commons.net.util.Base64
//import org.joda.time.DateTime
//
//import scala.util.{Failure, Success, Try}
//
//
///**
// * User: maximn
// * Date: 12/7/14
// */
//class InstanceDecoder(val key: String) extends Encryptor{
//  def decode(instance: String): Try[WixInstance] = {
//    val idx = instance.indexOf(".")
//    val signature = instance.substring(0, idx)
//    val encodedJson = instance.substring(idx + 1)
//
//      val base64 = new Base64(true)
//
//    val remoteSignature = base64.decode(signature.getBytes)
//    val localSignature = mac.doFinal(encodedJson.getBytes)
//
//    if (remoteSignature == localSignature) {
//      val instanceJson = base64.decode(encodedJson)
//      Success(JacksonObjectMapper.mapper.readValue(instanceJson, classOf[WixInstance]))
//    }
//    else {
//      Failure(new InvalidSignatureException(new String(remoteSignature), new String(localSignature)))
//    }
//
//  }
//}
//
//case class WixInstance(instanceId: String, signDate: DateTime, uid: String, permissions: String, ip: String, port: Int)

//
//import javax.crypto.Mac
//import javax.crypto.spec.SecretKeySpec
//
///**
// * User: maximn
// * Date: 12/7/14
// */
//trait Encryptor{
//  val key: String
//
//  private val encryptionMethod = "HMACSHA256"
//
//  protected lazy val mac = {
//    val secret = new SecretKeySpec(key.getBytes, encryptionMethod)
//    val instance = Mac.getInstance(encryptionMethod)
//    instance.init(secret)
//    instance
//  }
//}
