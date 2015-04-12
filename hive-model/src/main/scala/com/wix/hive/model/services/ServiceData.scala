package com.wix.hive.model.services

case class ServiceData(originId: String, correlationId: String, data: ServiceRunData)

//TODO: status -> enum
//TODO: errorType -> enum [UNKNOWN_TOKEN' or 'MISSING_PARAMETERS' or 'INTERNAL_ERROR' or 'LIMIT_REACHED' or 'MISSING_PREMIUM']]
case class ServiceRunData(status: String, errorType: Option[String], errorMessage: Option[String])
