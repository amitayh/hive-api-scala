package com.wix.hive.commands.contacts

import org.joda.time.DateTime
import org.specs2.specification.Scope

trait ContextForModification extends Scope{
  val contactId = "3d49b088-1d05-4576-9a31-103c3510be58"
  val modifiedAt = new DateTime(2010, 6, 2, 1, 2)
}
