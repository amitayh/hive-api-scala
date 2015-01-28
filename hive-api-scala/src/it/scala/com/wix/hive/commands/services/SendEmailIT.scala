package com.wix.hive.commands.services

import com.wix.hive.commands.{BaseHiveCtx, BaseHubIt}
import com.wix.hive.drivers.ServicesTestSupport


class SendEmailIT extends BaseHubIt {

  class ctx extends BaseHiveCtx with ServicesTestSupport {
  }

  "Executing SendEmail " should {
    "send email without an exception" in new ctx {
      expect(app, emailCommand)()

      client.execute(instance, emailCommand) must not(throwA).await

      verifySendEmail(app, emailCommand)
    }
  }
}

