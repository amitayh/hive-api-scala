package com.wix.hive.commands.redirects

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.HiveCommand

/**
 * Created by karolisb on 3/9/15.
 */
case object GetRedirects extends HiveCommand[Redirects] {
  override def url: String = "/redirects"

  override def method: HttpMethod = GET
}

case class Redirects(redirects: Seq[Redirect])
case class Redirect(id: Option[String], name: Option[String], description: Option[String], target: Option[String])