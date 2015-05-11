package com.wix.hive.commands

import java.io.{ByteArrayInputStream, InputStream}

import com.fasterxml.jackson.core.JsonParseException
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.json.JacksonObjectMapper
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * User: maximn
 * Date: 11/19/14
 */
class HiveCommandTest extends SpecificationWithJUnit {

  trait Context extends Scope {
    val cmd = new HiveCommand[CmdResponse] {
      override def url: String = ???
      override def method: HttpMethod = ???
    }

    def aMap(value: AnyRef) = Map("key" -> value)

    object DummyEnum extends Enumeration {
      type DummyEnum = Value
      val A, B = Value
    }

  }

  "mapValuesToStrings" should {
    "convert value of Some to string of inner" in new Context {
      cmd.mapValuesToStrings(aMap(Some("abc"))) must contain(exactly("key" -> "abc"))
    }

    "convert value of Seq to comma separated string" in new Context {
      cmd.mapValuesToStrings(aMap(Seq("a", "b", "c"))) must contain(exactly("key" -> "a,b,c"))
    }

    "convert value of Enum to its toString" in new Context {
      cmd.mapValuesToStrings(aMap(DummyEnum.B)) must contain(exactly("key" -> "B"))
    }

    "keep value of string as string" in new Context {
      cmd.mapValuesToStrings(aMap("value")) must contain(exactly("key" -> "value"))
    }

    "remove the pair for all other types that not defined" in new Context {
      cmd.mapValuesToStrings(aMap(new AnyRef)) must beEmpty
    }
  }

  "decode" should {
    "deserialize valid json" in new Context {
      val response = CmdResponse("some data")
      val jsonIS: InputStream = new ByteArrayInputStream(JacksonObjectMapper.mapper.writeValueAsBytes(response))
      cmd.decode(jsonIS) mustEqual response
    }

    "fail when content is not a json" in new Context {
      val is: InputStream = new ByteArrayInputStream("<head>wohoo</head>".getBytes)
      cmd.decode(is) must throwA[JsonParseException]

    }
  }
}

case class CmdResponse(data: String)
