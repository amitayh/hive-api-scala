package com.wix.hive.commands

import com.wix.hive.commands.redirects.{Redirect, Redirects, GetRedirects}
import com.wix.hive.infrastructure.HiveSimplicatorIT

/**
 * Created by karolisb on 3/9/15.
 */
class RedirectsIT extends HiveSimplicatorIT {
  class clientContext extends HiveClientContext {
    val redirects = Redirects(Seq(Redirect(Some("dashboard"), None, None, Some("http://www.wix.com"))))
  }

  "Redirects operations" should {
    "get list of app redirects" in new clientContext {
      expect(app, GetRedirects)(redirects)

      client.execute(instance, GetRedirects) must be_===(redirects).await
    }
  }
}
