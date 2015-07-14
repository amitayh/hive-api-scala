package com.wix.hive.infrastructure

import org.joda.time.{DateTime, DateTimeZone}

/**
 * User: maximn
 * Date: 7/14/15
 */
trait TimeProvider {
  def now: DateTime
}

class SystemTimeProvider extends TimeProvider{
  def now: DateTime = DateTime.now(DateTimeZone.UTC)
}