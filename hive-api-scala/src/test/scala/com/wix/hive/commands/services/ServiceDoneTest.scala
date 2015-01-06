package com.wix.hive.commands.services

import java.util.UUID

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.drivers.ServicesTestSupport
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.services.{ServiceData, ServiceRunData}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class ServiceDoneTest extends Specification with HiveMatchers {

  class Context extends Scope with ServicesTestSupport {
    val command = ServiceDone(serviceData)
  }

  "services done" should {
    "create HttpRequestData with all parameters" in new Context {
      command.createHttpRequestData must httpRequestDataWith(
        url = be_===("/services/done"),
        method = be_===(HttpMethod.POST),
        body = beSome(serviceData))
    }
  }
}
