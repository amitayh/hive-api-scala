package com.wix.hive.commands

import com.wix.hive.client.{HiveClient}
import com.wix.hive.infrastructure.{SimplicatorHub, WiremockSimplicator, AppDef}
import org.specs2.mutable.{Before, SpecificationWithJUnit}
import org.specs2.specification.Scope

/**
 * User: maximn
 * Date: 1/13/15
 */

class BaseHubIt extends SpecificationWithJUnit with SimplicatorHub {
  override val serverPort = BaseHubIt.serverPort
}

object BaseHubIt {
  val serverPort: Int = 9089

}

trait BaseHiveCtx extends Before {

  override def before: Any = WiremockSimplicator.resetMocks()

  val baseUrl = s"http://localhost:${BaseHubIt.serverPort}"

  val app = AppDef.random
  val instance = app.instanceId

  val client = new HiveClient(app.appId, app.secret, baseUrl = baseUrl)

  WiremockSimplicator.start
}