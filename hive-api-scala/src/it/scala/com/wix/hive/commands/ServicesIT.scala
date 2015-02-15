package com.wix.hive.commands

import com.wix.hive.commands.services.Providers
import com.wix.hive.drivers.ServicesTestSupport
import com.wix.hive.infrastructure.HiveSimplicatorIT

/**
 * User: maximn
 * Date: 1/18/15
 */
class ServicesIT extends HiveSimplicatorIT {

  class clientContext extends HiveClientContext with ServicesTestSupport {
  }

  "Executing get email providers" should {
    "get providers" in new clientContext {
      expect(app, providersCommand)(providersResponse)

      client.execute[Providers](instance, providersCommand) must haveOnlyProvider(be_===(provider)).await
    }

    "send done signal to the hub" in new clientContext {
      expect(app, serviceDoneCommand)()

      client.execute(instance, serviceDoneCommand)

      verify(app, serviceDoneCommand)
    }

    "send email without an exception" in new clientContext {
      expect(app, emailCommand)()

      client.execute(instance, emailCommand)

      verify(app, emailCommand)
    }

    "send single email" in new clientContext {
      expect(app, singleEmailCommand)()

      client.execute(instance, singleEmailCommand)

      verify(app, singleEmailCommand)
    }
  }
}
