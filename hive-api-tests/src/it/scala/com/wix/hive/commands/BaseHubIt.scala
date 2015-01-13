package com.wix.hive.commands

import com.wix.hive.client.infrastructure.AppDef
import com.wix.hive.client.{HiveClient, HiveTestkit}
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * User: maximn
 * Date: 1/13/15
 */

class BaseHubIt extends SpecificationWithJUnit with HiveTestkit {
  override val serverPort = BaseHubIt.serverPort
}

object BaseHubIt {
  val serverPort: Int = 8089
}

trait BaseHiveCtx extends Scope {
  val baseUrl = s"http://localhost:${BaseHubIt.serverPort}"

  val app = AppDef.random
  val instance = app.instanceId

  val client = new HiveClient(app.appId, app.secret, baseUrl = baseUrl)
}

