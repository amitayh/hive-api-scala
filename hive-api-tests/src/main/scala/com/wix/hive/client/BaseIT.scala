package com.wix.hive.client

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.time.NoTimeConversions

/**
 * User: maximn
 * Date: 12/2/14
 */
abstract class BaseIT extends SpecificationWithJUnit with NoTimeConversions{
  sequential

  def initEnv(): Unit

  def shutdownEnv(): Unit

  def beforeTest(): Unit
}
