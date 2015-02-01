package com.wix.hive.infrastructure

import org.specs2.mutable.SpecificationWithJUnit

/**
 * User: maximn
 * Date: 1/13/15
 */

trait HiveSimplicatorIT extends SpecificationWithJUnit with SimplicatorHive {
  sequential
  WiremockEnvironment.start
}

