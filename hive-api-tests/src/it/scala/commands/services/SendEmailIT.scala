package com.wix.hive.commands.services

import com.wix.hive.commands.{BaseHubIt, BaseHiveCtx}
import com.wix.hive.drivers.ServicesTestSupport


class SendEmailIT extends BaseHubIt {

  class ctx extends BaseHiveCtx with ServicesTestSupport {
  }

  step {
    start()
  }

  "send email" in new ctx {
    givenSendEmail(app, emailCommand)

    client.execute(instance, emailCommand) must not(throwA).await()
  }

  step {
    stop()
  }
}