package com.wix.hive.commands

import com.wix.hive.commands.activities.GetActivityTypes
import com.wix.hive.drivers.ActivitiesTestSupport._
import com.wix.hive.drivers.ContactsTestSupport._
import com.wix.hive.matchers.HiveMatchers._

/**
 * User: maximn
 * Date: 1/27/15
 */
class ActivitiesIT extends BaseHubIt {

  class ctx extends BaseHiveCtx

  "Activities operations" should {
    "get activities for a given contact" in new ctx {
      val cmd = aGetContactActivitiesCommand(contactId)

      expect(app, cmd)(pagingActivityResult)

      client.execute(instance, cmd) must beActivitiesWithIds(activity).await
    }

    "create activity for contact" in new ctx {
      val cmd = aCreateContactActivity(contactId)
      val res = anActivityCreateResult(contactId)

      expect(app, cmd)(res)

      client.execute(instance, cmd) must haveActivityResult(res.activityId, contactId).await
    }

    "get activity by ID" in new ctx {
      expect(app, getActivityByIdCommand)(activity)

      client.execute(app.instanceId, getActivityByIdCommand) must beAnActivityWith(activityId).await
    }

    "get list of all activity types" in new ctx {
      expect(app, GetActivityTypes())(types)

      client.execute(app.instanceId, GetActivityTypes()) must haveTypes(types.types).await
    }

    "create activity for contact using contact's user session" in new ctx {
      expect(app, createActivityCommand)(anActivityCreateResult(contactId))

      client.execute(instance, createActivityCommand) must haveActivityResult(activityId, contactId).await
    }

    "get all activities" in new ctx {
      expect(app, getActivitiesCommand)(pagingActivityResult)

      client.execute(instance, getActivitiesCommand) must beActivitiesWithIds(activity).await
    }
  }

}
