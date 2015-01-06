package com.wix.hive.commands.services

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.commands.HiveBaseCommand
import com.wix.hive.model.services.ServiceData

case class ServiceDone(serviceData: ServiceData) extends HiveBaseCommand[Unit]{
  override def url: String = "/services/done"

  override def method: HttpMethod = HttpMethod.POST

  override def body: Option[AnyRef] = Some(serviceData)
}



