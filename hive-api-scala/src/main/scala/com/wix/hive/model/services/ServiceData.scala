package com.wix.hive.model.services

case class ServiceData(originId: String, correlationId: String, data: ServiceRunData)

case class ServiceRunData(status: String, errorType: Option[String], errorMessage: Option[String])
