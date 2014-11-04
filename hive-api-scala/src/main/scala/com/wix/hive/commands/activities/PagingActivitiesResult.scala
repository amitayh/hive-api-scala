package com.wix.hive.commands.activities

import com.wix.hive.commands.common.PagingResult
import com.wix.hive.model.activities.Activity

case class PagingActivitiesResult(pageSize: Int, previousCursor: Option[String], nextCursor: Option[String], results: Seq[Activity]) extends PagingResult
