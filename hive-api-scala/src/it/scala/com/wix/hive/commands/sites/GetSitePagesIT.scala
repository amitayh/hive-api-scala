package com.wix.hive.commands.sites

import com.wix.hive.commands.{BaseHiveCtx, BaseHubIt}
import com.wix.hive.drivers.SitesTestSupport

/**
 * User: maximn
 * Date: 1/19/15
 */
class GetSitePagesIT extends BaseHubIt {

  class ctx extends BaseHiveCtx with SitesTestSupport {
  }

  "Executing GetSitePages " should {
    "return the site pages" in new ctx {
      expectSiteWithPages(app)(sitePages)

      client.execute(instance, GetSitePages) must be_===(sitePages).await
    }
  }
}
