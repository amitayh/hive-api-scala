package com.wix.hive.drivers

import com.wix.hive.commands.labels._
import com.wix.hive.model.labels.{LabelTypes, PagingLabelsResult, Label}
import org.specs2.matcher.Matcher
import org.specs2.matcher.Matchers._

/**
 * User: karenc
 * Date: 2/1/15
 */
trait LabelsTestSupport {
  def beLabelWithId(matcher: Matcher[String]): Matcher[Label] = matcher ^^ { (_: Label).id aka "labelId" }
  def beLabelsWith(matcher: Matcher[Seq[Label]]): Matcher[PagingLabelsResult] = matcher ^^ { (_: PagingLabelsResult).results aka "results" }

  val labelId = "contacts_server/new"
  val anotherLabelId = "contacts_server/contacted_me"
  val label = Label(labelId, None, "New", None, 0, LabelTypes.`userDefined`)
  val anotherLabel = Label(anotherLabelId, None, "Contacted Me", None, 1, LabelTypes.`userDefined`)

  val getLabelByIdCommand = GetLabelById(labelId)
  val getLabelsCommand = GetLabels()
}

object LabelsTestSupport extends LabelsTestSupport