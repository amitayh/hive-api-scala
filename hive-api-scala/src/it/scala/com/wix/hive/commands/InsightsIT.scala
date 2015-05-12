package com.wix.hive.commands

import com.wix.hive.commands.insights.InsightActivitySummary
import com.wix.hive.drivers.ContactsTestSupport._
import com.wix.hive.drivers.SitesTestSupport
import com.wix.hive.infrastructure.HiveSimplicatorIT
import com.wix.hive.model.activities.ActivityType
import com.wix.hive.model.activities.ActivityType._
import com.wix.hive.model.insights.{ActivitySummary, ActivityTypesSummary}
import org.joda.time.DateTime
import org.specs2.matcher.{MustThrownExpectations, Matcher, Matchers}

/**
 * User: maximn
 * Date: 1/27/15
 */
class InsightsIT extends HiveSimplicatorIT {

  class clientContext extends HiveClientContext with SitesTestSupport with Matchers {
    def haveActivityOfType(typ: ActivityType): Matcher[Seq[ActivityTypesSummary]] = (_: Seq[ActivityTypesSummary]).exists(_.activityType == Some(typ))

    def haveActivityOfType(typ: ActivityType, total: Int): Matcher[ActivitySummary] = {
      be_===(total) ^^ { (_: ActivitySummary).total aka "total" } and
        haveActivityOfType(typ) ^^ { (_: ActivitySummary).activityTypes aka "types" }
    }

    def anInsightActivitySummaryCommand(contactId: String) = InsightActivitySummary(Some(contactId))
    val summaryAactivityType = ActivityType.`auth/login`
    val summaryFrom = new DateTime(2010, 1, 1, 10, 10)
    val summary = ActivitySummary(Seq(ActivityTypesSummary(Some(summaryAactivityType), 1, summaryFrom)), 1, summaryFrom)

  }

  "Insights APIs" should {
    "get insights (activity summary) for a contact" in new clientContext {
      val cmd = anInsightActivitySummaryCommand(contactId)

      expect(app, cmd)(summary)

      client.execute(instance, cmd) must haveActivityOfType(typ = summaryAactivityType, total = summary.total).await
    }
  }
}
