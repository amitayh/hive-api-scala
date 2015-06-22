package com.wix.hive.commands

import com.wix.hive.commands.sites.{GetSitePages, GetSiteSettings, Site}
import com.wix.hive.drivers.SitesTestSupport
import com.wix.hive.infrastructure.HiveSimplicatorIT

/**
 * User: maximn
 * Date: 1/19/15
 */
class SitesIT extends HiveSimplicatorIT {

  class clientContext extends HiveClientContext with SitesTestSupport {

  }

  "Sites APIs" should {
    "return the site pages" in new clientContext {
      expect(app, GetSitePages)(sitePages)

      client.execute(instance, GetSitePages) must be_===(sitePages).await
    }

    "get site's URL" in new clientContext {
      expect(app, Site)(siteData)

      client.execute(instance, Site) must haveSiteUrl(url).await
    }

    "return site settings" in new clientContext {
      expect(app, GetSiteSettings)(siteSettings)

      client.execute(instance, GetSiteSettings) must be_===(siteSettings).await
    }
  }
}
