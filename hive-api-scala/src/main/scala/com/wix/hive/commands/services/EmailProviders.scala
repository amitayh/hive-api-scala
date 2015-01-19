package com.wix.hive.commands.services

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
/**
 * User: maximn
 * Date: 1/18/15
 */
case object EmailProviders extends ServicesCommand[Providers] {
  override def method: HttpMethod = HttpMethod.GET

  override def url: String = super.url + "/email/providers"
}


case class Providers(providers: Seq[Provider])
case class Provider(providerId: String, displayName: String, image: String, icon: String)