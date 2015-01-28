package com.wix.hive.commands

import com.wix.hive.client.HiveClient
import com.wix.hive.infrastructure.{AppDef, SimplicatorHub, WiremockSimplicator}
import org.specs2.mutable.{Before, SpecificationWithJUnit}

/**
 * User: maximn
 * Date: 1/13/15
 */

class BaseHubIt extends SpecificationWithJUnit with SimplicatorHub {
  sequential
  override val serverPort = BaseHubIt.serverPort
  WiremockSimplicator.start
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

}