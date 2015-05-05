package com.wix.hive.model.activities

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.wix.hive.model.activities.ActivityType.ActivityType
import com.wix.hive.model.activities.ActivityTypes.knownNames

case class ActivityTypes(types: Seq[ActivityType]) {

  @JsonCreator
  def this(@JsonProperty("types") types: Seq[String], @JsonProperty("hack_to_overcome_type_erasure") ignored: Option[_] = None) = this(
    types.filter(knownNames).map(name => ActivityType.withName(name))
  )

}

object ActivityTypes {
  val knownNames = ActivityType.values.map(_.toString)
}