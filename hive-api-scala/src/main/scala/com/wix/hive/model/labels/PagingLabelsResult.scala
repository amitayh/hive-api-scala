package com.wix.hive.model.labels

import com.wix.hive.commands.labels.GetLabels

case class PagingLabelsResult(total: Int, pageSize: Int, previous: Option[GetLabels], next: Option[GetLabels], results: Seq[Label])
