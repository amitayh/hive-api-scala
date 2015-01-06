package com.wix.hive.drivers

import java.util.UUID

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
}
