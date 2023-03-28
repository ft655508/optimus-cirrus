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
package optimus.utils

object PropertyUtils {

  // Look first for -Dfoo.bar.baz and then for OPTIMUS_DIST_FOO_BAR_BAZ.
  // While this could be a generic utility, its real purpose is to lever
  // the auto-distribution of OPTIMUS_DIST-prefixed environment variables.
  private val Prefix = "optimus.dist."
  private def asEnv(s: String) = s.toUpperCase.replaceAllLiterally(".", "_")
  def flag(k: String) = get(k, false)
  def get(k: String, default: => Boolean): Boolean = get(k).map(parseBoolean(_)).getOrElse(default)
  def get(k: String, default: => Int): Int = get(k).map(_.toInt).getOrElse(default)
  def get(k: String, default: => String): String = get(k).getOrElse(default)
  def get(k: String, overrides: Map[String, String] = Map.empty): Option[String] = {
    overrides.get(k) orElse {
      Option(System.getProperty(k))
    } orElse {
      Option(System.getenv(asEnv(k)))
    } orElse {
      val kv = (if (k.startsWith(Prefix)) k else s"$Prefix$k")
      Option(System.getenv(asEnv(kv)))
    }
  }

  private def parseBoolean(value: String): Boolean =
    if (value.length == 0) true
    else if ("1" == value || "true" == value) true
    else if ("0" == value || "false" == value) false
    else throw new IllegalArgumentException(s"Can't parse >>$value<< to boolean.")

  def propertyMap(orig: Map[String, String], overridess: String*): Map[String, String] = {
    orig ++ propertyMap(overridess: _*)
  }

  def propertyMap(settingss: String*): Map[String, String] =
    settingss.foldLeft(Map.empty[String, String]) {
      case (z, null) => z
      case (z, value) =>
        z ++ value
          .split("[;:]")
          .map(_.split("=",2))
          .collect { case Array(k, v) =>
            (k, v)
          }
          .toMap
    }
}
