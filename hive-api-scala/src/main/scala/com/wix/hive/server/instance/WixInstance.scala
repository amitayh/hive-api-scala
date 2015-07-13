package com.wix.hive.server.instance

import java.net.InetSocketAddress
import java.util.UUID

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}
import org.joda.time.DateTime

import scala.util.Try

/**
 * User: maximn
 * Date: 7/13/15
 */

/**
 * The instance parameter enables you to identify the site and the Wix user, and to verify that the calling party is indeed Wix
 *
 * http://dev.wix.com/docs/infrastructure/app-instance
 * @param instanceId The instance ID of the app within Wix.
 * @param signedAt The date of the payload signature
 * @param userId The ID of the site member who is currently logged in (optional)
 * @param permissions The permission set of the site visitor. The permissions have the value OWNER if the uid is of the site owner. Otherwise, the permissions value will be empty.
 * @param userIp The IP of the user
 * @param premiumPackageId The Premium Package ID, as was entered in the Dev Center during the app registration process. (If Premium)
 * @param demoMode
 * @param ownerId
 */
case class WixInstance(instanceId: UUID,
                       @JsonProperty("signDate") signedAt: DateTime,
                       @JsonProperty("uid") userId: Option[String],
                       @JsonDeserialize(using = classOf[CustomPermissionsDeserializer]) permissions: Set[String],
                       @JsonProperty("ipAndPort") @JsonDeserialize(using = classOf[CustomIpDeserializer]) userIp: InetSocketAddress,
                       @JsonProperty("vendorProductId") premiumPackageId: Option[UUID],
                       demoMode: Boolean,
                       @JsonProperty("siteOwnerId") ownerId: UUID)

// For backward compatibility this field is a `string` and not an `array `
class CustomPermissionsDeserializer extends JsonDeserializer[Set[String]] {
  override def deserialize(jp: JsonParser, ctxt: DeserializationContext): Set[String] = {
    val permission = jp.readValueAs(classOf[String])
    permission match {
      case "null" => Set.empty
      case permission: String => Set(permission)
    }
  }
}

class CustomIpDeserializer extends JsonDeserializer[InetSocketAddress] {
  override def deserialize(jp: JsonParser, ctxt: DeserializationContext): InetSocketAddress = {
    val ipAndPort = jp.readValueAs(classOf[String])

    ipAndPort match {
      case IpAndPort(ip, port) => InetSocketAddress.createUnresolved(ip, port.toInt)
    }
  }

  object IpAndPort {
    def unapply(ipAndPort: String): Option[(String, Int)] = {
      Try {
        val arr = ipAndPort.split('/')
        val ip = arr(0)
        val port = arr(1)
        val numericPort = port.toInt
        (ip, numericPort)
      }.toOption
    }
  }

}