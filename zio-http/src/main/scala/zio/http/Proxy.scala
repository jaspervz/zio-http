/*
 * Copyright 2021 - 2023 Sporta Technologies PVT LTD & the ZIO HTTP contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.http
import java.net.InetSocketAddress

import zio.stacktracer.TracingImplicits.disableAutoTrace

import zio.http.middleware.Auth.Credentials
import zio.http.model.Headers

/**
 * Represents the connection to the forward proxy before running the request
 *
 * @param url:
 *   url address of the proxy server
 * @param credentials:
 *   credentials for the proxy server. Encodes credentials with basic auth and
 *   put under the 'proxy-authorization' header
 * @param headers:
 *   headers for the request to the proxy server
 */
final case class Proxy(
  url: URL,
  credentials: Option[Credentials] = None,
  headers: Headers = Headers.empty,
) { self =>

  def withUrl(url: URL): Proxy                         = self.copy(url = url)
  def withCredentials(credentials: Credentials): Proxy = self.copy(credentials = Some(credentials))
  def withHeaders(headers: Headers): Proxy             = self.copy(headers = headers)
}

object Proxy {
  val empty: Proxy = Proxy(URL.empty)
}
