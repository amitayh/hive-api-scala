package com.wix.hive.model.labels

import com.fasterxml.jackson.core.`type`.TypeReference

class LabelTypesRef extends TypeReference[LabelTypes.type]

object LabelTypes extends Enumeration {
  type LabelType = Value
  val `system` = Value("system")
  val `userDefined` = Value("userDefined")
}
