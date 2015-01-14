package com.wix.hive.commands.activities

import com.wix.hive.commands.HiveCommand

trait ActivityCommand[TResponse] extends HiveCommand[TResponse] {
  override val url: String = "/activities"
}
