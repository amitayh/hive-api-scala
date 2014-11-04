package com.wix.hive.commands.common

import com.wix.hive.commands.GetActivities

trait Cursor {
  def  previousCursor: Option[String]
  def nextCursor: Option[String]

  def previousPageCommand: Option[GetActivities] = previousCursor.map(_ => GetActivities(cursor = this.previousCursor))
  def nextPageCommand: Option[GetActivities] = nextCursor.map(_ => GetActivities(cursor = this.nextCursor))
}
