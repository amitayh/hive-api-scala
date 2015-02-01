package com.wix.hive.commands

import com.wix.hive.infrastructure.{SimplicatorHive, WiremockEnvironment}
import org.specs2.mutable.SpecificationWithJUnit

/**
 * User: maximn
 * Date: 1/13/15
 */

class HiveSimplicatorIT extends SpecificationWithJUnit with SimplicatorHive {
  sequential
  WiremockEnvironment.start
}

