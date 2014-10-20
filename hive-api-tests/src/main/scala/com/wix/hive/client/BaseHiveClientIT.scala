package com.wix.hive.client

import com.wix.hive.commands.contacts.GetContacts
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

abstract class BaseHiveClientIT extends SpecificationWithJUnit with HiveApiDrivers {
  "Hive client" should {

    "get all contacts" in new Context {

      val command = GetContacts()

      client.execute(command)

      failure("not implemented")
    }
  }

  class Context extends Scope {
    val client = new HiveClient("id", "key", "inst")
  }
}

trait HiveApiDrivers {

  def givenContactExists: Unit
}




