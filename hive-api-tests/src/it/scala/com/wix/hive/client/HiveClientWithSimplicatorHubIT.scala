package com.wix.hive.client

import com.wix.hive.client.infrastructure.SimplicatorHub


class HiveClientWithSimplicatorHubIT extends BaseHiveClientIT with HiveTestkit {
  override val serverPort: Int = 8089
  override val baseUrl = s"http://localhost:$serverPort"

  override def initEnv(): Unit = this.start()

  override def shutdownEnv() = this.stop()

  override def beforeTest(): Unit = this.resetMocks()
}