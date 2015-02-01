package com.wix.hive.commands.common

import com.wix.hive.commands.HiveCommand

trait PagingResult[C <: HiveCommand[_]] {
  def previousCursor: Option[String]
  def nextCursor: Option[String]

  def genCommand(cursor: String): C

  def previousPageCommand: Option[C] = previousCursor map genCommand
  def nextPageCommand: Option[C] = nextCursor map genCommand
}
