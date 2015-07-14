package com.wix.hive.infrastructure

import org.joda.time._
import org.specs2.mutable.Specification

/**
 * User: maximn
 * Date: 7/14/15
 */
class SystemTimeProviderIT extends Specification {
  "now" should {
    "return current system time (UTC)" in {
      val systemNowUtc: DateTime = new DateTime(DateTimeZone.UTC)
      val systemProviderNow: DateTime = new SystemTimeProvider().now

      Seconds.secondsBetween(systemNowUtc, systemProviderNow).getSeconds must be ~(0 +/- 1)
      systemProviderNow.getZone must be_===(DateTimeZone.UTC)
    }
  }
}
