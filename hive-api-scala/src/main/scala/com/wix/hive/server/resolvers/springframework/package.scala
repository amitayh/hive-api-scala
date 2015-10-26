package com.wix.hive.server.resolvers

import org.springframework.web.context.request.WebRequest

package object springframework {
  type WixInstanceRequestResolver = PartialFunction[WebRequest, String]
}
