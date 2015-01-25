package com.wix.hive.commands.services

import com.wix.hive.commands.{BaseHiveCtx, BaseHubIt}
import com.wix.hive.drivers.ServicesTestSupport

/**
 * User: maximn
 * Date: 1/25/15
 */
class ServiceDoneIT extends BaseHubIt {

  class ctx extends BaseHiveCtx with ServicesTestSupport {
  }

  "Executing ServiceDone " should {
    "send done signal to the hub" in new ctx {
      expectServiceDone(app)

      client.execute(instance, serviceDoneCommand) must not(throwA).await

      verifyServiceDone(app, serviceDoneCommand)
    }
  }
}
