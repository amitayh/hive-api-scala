package com.wix.hive.model.labels

import com.wix.hive.commands.common.PagingResult
import com.wix.hive.commands.labels.GetLabels

case class PagingLabelsResult(total: Int, pageSize: Int, previousCursor: Option[String], nextCursor: Option[String], results: Seq[Label]) extends PagingResult[GetLabels] {
  override def genCommand(cursor: String): GetLabels = GetLabels(Some(cursor))
}
