package com.wix.hive.commands.labels

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.commands.common.PageSizes._
import com.wix.hive.model.labels.PagingLabelsResult


case class GetLabels(cursor: Option[String] = None,
                     pageSize: Option[PageSizes] = None) extends LabelsCommand[PagingLabelsResult] {

  override val method = HttpMethod.GET

  override val query = {
    super.mapValuesToStrings({
      import com.wix.hive.commands.labels.GetLabels.QueryKeys
      Map(
        QueryKeys.cursor -> cursor,
        QueryKeys.pageSize -> pageSize
      )
    })
  }
}

object GetLabels {

  object QueryKeys {
    val cursor = "cursor"
    val pageSize = "pageSize"
  }

}
