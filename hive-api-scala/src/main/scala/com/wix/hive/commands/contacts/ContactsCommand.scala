package com.wix.hive.commands.contacts

import com.wix.hive.commands.HiveCommand

trait ContactsCommand[TResponse] extends HiveCommand[TResponse] {
  override val url: String = "/contacts"
}
