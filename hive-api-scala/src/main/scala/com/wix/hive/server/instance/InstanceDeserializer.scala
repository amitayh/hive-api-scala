package com.wix.hive.server.instance

import com.wix.hive.json.JacksonObjectMapper


/**
 * User: maximn
 * Date: 7/13/15
 */
class InstanceDeserializer {
  def deserialize(payload: Array[Byte]): WixInstance =
    JacksonObjectMapper.mapper.readValue(payload, classOf[WixInstance])
}
