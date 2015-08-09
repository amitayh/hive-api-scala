package com.wix.hive.drivers

import java.util.UUID

import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.security.HiveSigner
import com.wix.hive.server.instance.WixInstance
import org.apache.commons.net.util.Base64
import org.joda.time.DateTime

trait InstanceEncoderSupport {

  def signAndEncodeInstance(wixInstance: WixInstance, key: String): String = {
    val base64 = new Base64(256, null, true)
    val signer = new HiveSigner(key)
    val instanceForSerialization = WixInstanceForSerialization.from(wixInstance)
    val instanceJson = JacksonObjectMapper.mapper.writeValueAsString(instanceForSerialization)
    val encodedInstance = new String(base64.encode(instanceJson.getBytes))
    val signature = signer.signString(encodedInstance)
    s"$signature.$encodedInstance"
  }

}

case class WixInstanceForSerialization(instanceId: UUID,
                                       signDate: DateTime,
                                       uid: Option[String],
                                       permissions: String,
                                       ipAndPort: String,
                                       vendorProductId: Option[String],
                                       demoMode: Boolean,
                                       siteOwnerId: UUID)

object WixInstanceForSerialization {
  def from(wixInstance: WixInstance): WixInstanceForSerialization =
    WixInstanceForSerialization(
      instanceId = wixInstance.instanceId,
      signDate = wixInstance.signedAt,
      uid = wixInstance.userId,
      permissions = wixInstance.permissions.headOption.getOrElse("null"),
      ipAndPort = s"${wixInstance.userIp.getHostString}/${wixInstance.userIp.getPort}",
      vendorProductId = wixInstance.premiumPackageId,
      demoMode = wixInstance.demoMode,
      siteOwnerId = wixInstance.ownerId)
}
