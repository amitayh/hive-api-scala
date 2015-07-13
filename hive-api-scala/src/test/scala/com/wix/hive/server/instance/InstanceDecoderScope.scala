package com.wix.hive.server.instance

import java.net.InetSocketAddress
import java.util.UUID

import org.apache.commons.net.util.Base64
import org.specs2.specification.Scope

/**
 * User: maximn
 * Date: 7/13/15
 */
class InstanceDecoderScope extends Scope {
  val key = "SecretKey"
  val instanceId = UUID.fromString("13f039a6-00d8-7265-5169-ac9ed5123713")
  val signDate = "2015-07-13T13:18:51.965Z"
  val uid = "68f8ef81-c445-4ab8-9e8b-65119e84731b"
  val permission = "OWNER"
  val premiumPackage = UUID.fromString("a05727a8-73d0-4ea3-b4cf-fedddbd49979")
  val ip = "5.102.254.181"
  val port = 62834
  val ipAndPort = InetSocketAddress.createUnresolved(ip, port)
  val demoMode = "false"
  val ownerId = UUID.fromString("c71abb54-25a9-4e98-a9ea-66e7681983fb")
  val base64 = new Base64(true)

  val sampleInstanceEncoded = base64.encodeToString(generateInstance().getBytes)
  val precalculatedSignature = "wcR4TddW55czYzPn3OeOwh8QC7GdC7i_QcfAPWiQsRs"
  val sampleSignedInstance = precalculatedSignature + "." + sampleInstanceEncoded
  val decoder = new InstanceDecoder(key)

  private def optAsJson[T](v: Option[T]): String = v.fold("null")(s => s"""\"$s\"""")

  def generateInstance(permissions: String = permission,
                       userId: Option[String] = Some(uid),
                       premiumPackageId: Option[UUID] = Some(premiumPackage)) = {
    // @formatter:off
    s"""{
        |  "instanceId": "${instanceId.toString}",
        |  "signDate": "${signDate.toString}",
        |  "uid": ${optAsJson(userId)},
        |  "permissions": "$permissions",
        |  "ipAndPort": "$ip/$port",
        |  "vendorProductId": ${optAsJson(premiumPackageId)},
        |  "demoMode": $demoMode,
        |  "siteOwnerId": "${ownerId.toString}"
        |}""".stripMargin
        }
  // @formatter:on
}
