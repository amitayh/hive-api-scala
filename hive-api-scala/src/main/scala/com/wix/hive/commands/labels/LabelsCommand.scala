package com.wix.hive.commands.labels

import com.wix.hive.commands.HiveCommand

trait LabelsCommand[TResponse] extends HiveCommand[TResponse] {
  override val url: String = "/labels"
}
