package com.wix.hive.commands.activities

import com.wix.hive.commands.HiveBaseCommand

trait ActivityCommand[TResponse] extends HiveBaseCommand[TResponse] {
  override val url: String = "/activities"
}
