package com.wix.hive.model.activities

import com.fasterxml.jackson.core.{JsonParser, ObjectCodec}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}
import com.wix.hive.model.activities.ActivityType.ActivityType
import com.wix.hive.model.activities.ActivityTypes.knownNames

@JsonDeserialize(using = classOf[ActivityTypesDeserializer])
case class ActivityTypes(types: Seq[ActivityType])

object ActivityTypes {
  val knownNames = ActivityType.values.map(_.toString)
}

private class ActivityTypesDeserializer extends JsonDeserializer[ActivityTypes] {
  override def deserialize(json: JsonParser, context: DeserializationContext): ActivityTypes = {
    import scala.collection.JavaConverters._

    val codec: ObjectCodec = json.getCodec
    val node: ObjectNode = codec.readTree(json)

    ActivityTypes(
      node.withArray("types").asScala.map(_.asText).filter(knownNames).map(name => ActivityType.withName(name)).toSeq
    )

  }
}