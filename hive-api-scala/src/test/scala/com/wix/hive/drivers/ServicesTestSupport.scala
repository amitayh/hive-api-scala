package com.wix.hive.drivers

import java.util.UUID

import com.wix.hive.commands.services._
import com.wix.hive.commands.sites.{Page, SitePages}
import com.wix.hive.model.services.{ServiceData, ServiceRunData}
import com.wix.hive.model.sites.{SiteStatus, SiteData}
import org.specs2.matcher.{Matcher, MustMatchers}
import MustMatchers._
/**
 * User: maximn
 * Date: 1/6/15
 */
trait ServicesTestSupport {
  def randomStringId = UUID.randomUUID().toString

  val callerAppId = randomStringId
  val providerAppId = randomStringId


  val servicesCorrelationId = randomStringId
  val serviceRunData = ServiceRunData("SUCCESS", None, None)

  def aServiceData(callerAppId: String = callerAppId) = ServiceData(callerAppId, servicesCorrelationId, serviceRunData)
  val serviceData = aServiceData()


  val providerId = randomStringId
  val redemptionToken = UUID.randomUUID().toString
  def anEmail(providerId: String = providerId, redemptionToken: String = redemptionToken) = SendEmail(providerId, None, redemptionToken, EmailContacts("id", Seq("id1", "id2")))
  val emailCommand = anEmail()
  val providersCommand = EmailProviders

  val displayName = "display name"
  val image = "img.jpg"
  val icon = "icon.ico"
  val provider = Provider(providerId, displayName, image, icon)
  val providersResponse = Providers(Seq(provider))

  def haveOnlyProvider(provider: Matcher[Provider]): Matcher[Providers] = (_: Providers).providers must contain(provider).exactly(1)
}

