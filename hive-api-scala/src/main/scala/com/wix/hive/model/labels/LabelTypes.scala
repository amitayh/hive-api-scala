package com.wix.hive.model.labels

object LabelTypes extends Enumeration {
  type LabelType = Value
  val `system` = Value("system")
  val `userDefined` = Value("userDefined")
}
