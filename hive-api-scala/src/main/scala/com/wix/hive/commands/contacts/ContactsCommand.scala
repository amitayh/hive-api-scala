package com.wix.hive.commands.contacts

import com.wix.hive.commands.HiveBaseCommand

trait ContactsCommand[TResponse] extends HiveBaseCommand[TResponse] {
  override val url: String = "/contacts"
}
