package com.wix.hive.commands

import com.wix.hive.drivers.HiveCommandsMatchers._
import com.wix.hive.drivers.LabelsTestSupport
import com.wix.hive.infrastructure.HiveSimplicatorIT
import com.wix.hive.model.labels.PagingLabelsResult

/**
 * User: karenc
 * Date: 2/1/15
 */
class LabelsIT extends HiveSimplicatorIT {


  class clientContext extends HiveClientContext with LabelsTestSupport {
  }

  "Labels operations" should {

    "get label by id" in new clientContext {
      expect(app, getLabelByIdCommand)(label)

      client.execute(instance, getLabelByIdCommand) must beLabelWithId(labelId).await
    }

    "get labels with filtering" in new clientContext {
      expect(app, getLabelsCommand)(PagingLabelsResult(total = 2, pageSize = 25, previousCursor = None, nextCursor = None, results = Seq(label, anotherLabel)))

      client.execute(instance, getLabelsCommand) must beLabelsWith(contain(allOf(beLabelWithId(labelId), beLabelWithId(anotherLabelId)))).await
    }

  }
}
