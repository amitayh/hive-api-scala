package com.wix.hive.drivers

import java.util.UUID

import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.security.HiveSigner
import com.wix.hive.server.instance.WixInstance
import org.apache.commons.net.util.Base64
import org.joda.time.DateTime

trait InstanceEncoderSupport {

  private lazy val base64 = new Base64

  def signAndEncodeInstance(wixInstance: WixInstance, key: String): String = {
    val instanceForSerialization = createInstanceForSerialization(wixInstance)
    val instanceJson = JacksonObjectMapper.mapper.writeValueAsString(instanceForSerialization)
    val signer = new HiveSigner(key)
    val encodedInstance = new String(base64.encode(instanceJson.getBytes))
    val signature = signer.signString(encodedInstance)
    s"$signature.$encodedInstance"
  }

  private def createInstanceForSerialization(wixInstance: WixInstance) =
    WixInstanceForSerialization(
      instanceId = wixInstance.instanceId,
      signDate = wixInstance.signedAt,
      uid = wixInstance.userId,
      permissions = wixInstance.permissions.headOption.getOrElse(""),
      ipAndPort = "%s/%d".format(wixInstance.userIp.getHostString, wixInstance.userIp.getPort),
      vendorProductId = wixInstance.premiumPackageId,
      demoMode = wixInstance.demoMode,
      siteOwnerId = wixInstance.ownerId)

}

case class WixInstanceForSerialization(instanceId: UUID,
                                       signDate: DateTime,
                                       uid: Option[String],
                                       permissions: String,
                                       ipAndPort: String,
                                       vendorProductId: Option[UUID],
                                       demoMode: Boolean,
                                       siteOwnerId: UUID)