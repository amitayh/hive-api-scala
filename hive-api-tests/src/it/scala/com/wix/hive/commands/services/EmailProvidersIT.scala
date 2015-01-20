package com.wix.hive.commands.services

import com.wix.hive.commands.{BaseHiveCtx, BaseHubIt}
import com.wix.hive.drivers.ServicesTestSupport

/**
 * User: maximn
 * Date: 1/18/15
 */
class EmailProvidersIT extends BaseHubIt {

  class ctx extends BaseHiveCtx with ServicesTestSupport {
  }

  "Executing get email providers" should {
    "get providers" in new ctx {
      expectEmailProviders(app)(providersResponse)

      client.execute[Providers](instance, providersCommand) must haveOnlyProvider(be_===(provider)).await
    }
  }
}
