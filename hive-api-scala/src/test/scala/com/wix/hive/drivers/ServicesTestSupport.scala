package com.wix.hive.drivers

import java.util.UUID

//import com.wix.hive.commands.services.{EmailContacts, SendEmail}
import com.wix.hive.model.services.{ServiceData, ServiceRunData}

/**
 * User: maximn
 * Date: 1/6/15
 */
trait ServicesTestSupport {
  val callerAppId = UUID.randomUUID().toString
  val providerAppId = UUID.randomUUID().toString


  val servicesCorrelationId = UUID.randomUUID().toString
  val serviceRunData = ServiceRunData("success", None, None)

  def aServiceData(callerAppId: String = callerAppId) = ServiceData(callerAppId, servicesCorrelationId, serviceRunData)
  val serviceData = aServiceData()


//  val providerId = UUID.randomUUID().toString
//  val redemptionToken = UUID.randomUUID().toString
//  def anEmail(providerId: String = providerId, redemptionToken: String = redemptionToken) = SendEmail(providerId, None, redemptionToken, EmailContacts("id", Seq("id1", "id2")))
//  val emailCommand = anEmail()
}
