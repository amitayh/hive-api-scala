package com.wix.hive.commands.services

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.commands.HiveCommand

/**
 * User: maximn
 * Date: 1/7/15
 */
trait ServicesCommand extends HiveCommand[Unit] {
  override def url: String = "/services"
  override def method: HttpMethod = HttpMethod.POST
}
