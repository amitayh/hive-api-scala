package com.wix.hive.commands

import com.wix.hive.client.HiveClient
import com.wix.hive.infrastructure.{AppDef, SimplicatorHive, WiremockEnvironment}
import org.specs2.mutable.{Before, SpecificationWithJUnit}

/**
 * User: maximn
 * Date: 1/13/15
 */

class HiveSimplicatorIT extends SpecificationWithJUnit with SimplicatorHive {
  sequential
  WiremockEnvironment.start
}

object HiveSimplicatorIT {
  val serverPort: Int = 9089

}

