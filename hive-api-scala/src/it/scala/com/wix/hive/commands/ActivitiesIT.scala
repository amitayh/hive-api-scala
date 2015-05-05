package com.wix.hive.commands

import com.wix.hive.commands.activities.GetActivityTypes
import com.wix.hive.drivers.ActivitiesTestSupport._
import com.wix.hive.drivers.ContactsTestSupport._
import com.wix.hive.infrastructure.HiveSimplicatorIT
import com.wix.hive.matchers.HiveMatchers._
import com.wix.hive.model.activities.ActivityType._
import com.wix.hive.model.activities.{ActivityType, ActivityTypes}

/**
 * User: maximn
 * Date: 1/27/15
 */
class ActivitiesIT extends HiveSimplicatorIT {

  class clientContext extends HiveClientContext

  "Activities operations" should {
    "get activities for a given contact" in new clientContext {
      val cmd = aGetContactActivitiesCommand(contactId)

      expect(app, cmd)(pagingActivityResult)

      client.execute(instance, cmd) must beActivitiesWithIds(activity).await
    }

    "create activity for contact" in new clientContext {
      val cmd = aCreateContactActivity(contactId)
      val res = anActivityCreateResult(contactId)

      expect(app, cmd)(res)

      client.execute(instance, cmd) must haveActivityResult(res.activityId, contactId).await

      verify(app, cmd)
    }

    "get activity by ID" in new clientContext {
      expect(app, getActivityByIdCommand)(activity)

      client.execute(app.instanceId, getActivityByIdCommand) must beAnActivityWith(activityId).await
    }

    "get list of all known activity types" in new clientContext {
      expectCustom(app, GetActivityTypes())(aJsonResponseOfActivityTypesNamedAs(ActivityTypes.knownNames.toSeq: _*))

      client.execute(app.instanceId, GetActivityTypes()) must haveTypes(ActivityType.values.toSeq).await
    }

    "ignore unknown activity types" in new clientContext {
      expectCustom(app, GetActivityTypes())(aJsonResponseOfActivityTypesNamedAs(
        `auth/login`, `auth/register`, "unknown/activity-type-to-hive-client"
      ))

      client.execute(app.instanceId, GetActivityTypes()) must haveTypes(Seq(`auth/login`, `auth/register`)).await
    }

    "create activity for contact using contact's user session" in new clientContext {
      expect(app, createActivityCommand)(anActivityCreateResult(contactId))

      client.execute(instance, createActivityCommand) must haveActivityResult(activityId, contactId).await
    }

    "get all activities" in new clientContext {
      expect(app, getActivitiesCommand)(pagingActivityResult)

      client.execute(instance, getActivitiesCommand) must beActivitiesWithIds(activity).await
    }
  }

  private def aJsonResponseOfActivityTypesNamedAs(names: Any*): String = {
    val jsonArrayOfNames = names.map(name => s""""$name"""").mkString("[", ", ", "]")
    s"""{"types":$jsonArrayOfNames}"""
  }

}
