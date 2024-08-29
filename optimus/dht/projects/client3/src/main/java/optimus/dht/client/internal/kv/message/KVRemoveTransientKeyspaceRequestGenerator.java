/*
 * Morgan Stanley makes this available to you under the Apache License, Version 2.0 (the "License").
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * See the NOTICE file distributed with this work for additional information regarding copyright ownership.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package optimus.dht.client.internal.kv.message;

import optimus.dht.common.api.Keyspace;
import optimus.dht.common.api.transport.MessageGenerator;
import optimus.dht.common.api.transport.MessageStream;

public class KVRemoveTransientKeyspaceRequestGenerator implements MessageGenerator {

  private final long requestId;
  private final Keyspace keyspace;
  private final String correlationName;

  public KVRemoveTransientKeyspaceRequestGenerator(
      long requestId, Keyspace keyspace, String correlationName) {
    this.requestId = requestId;
    this.keyspace = keyspace;
    this.correlationName = correlationName;
  }

  @Override
  public MessageStream build(int protocolVersion) {
    switch (protocolVersion) {
      case 1:
        return KVRequestsGeneratorV1.removeTransientKeyspace(requestId, keyspace, correlationName);
      default:
        throw new IllegalArgumentException("Unsupported protocol version=" + protocolVersion);
    }
  }

  @Override
  public long estimatedSize() {
    return 0;
  }
}
