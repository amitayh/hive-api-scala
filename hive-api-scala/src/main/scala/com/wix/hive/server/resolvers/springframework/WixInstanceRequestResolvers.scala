package com.wix.hive.server.resolvers.springframework

import org.springframework.web.context.request.WebRequest

class HeaderRequestResolver(headerName: String) extends WixInstanceRequestResolver {
  override def isDefinedAt(request: WebRequest): Boolean = request.getHeader(headerName) != null
  override def apply(request: WebRequest): String = request.getHeader(headerName)
}

class QueryParamRequestResolver(paramName: String) extends WixInstanceRequestResolver {
  override def isDefinedAt(request: WebRequest): Boolean = request.getParameter(paramName) != null
  override def apply(request: WebRequest): String = request.getParameter(paramName)
}
