/*
 * Copyright 2016 doublegsoft.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.doublegsoft.protosys.util;

/**
 * TODO: ADD DESCRIPTION
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 * @since 1.0
 */
public class StringHolder {

  private StringBuilder builder = new StringBuilder();

  public StringHolder indent(int indent) {
    Strings.indent(builder, indent);
    return this;
  }

  public StringHolder linefeed() {
    Strings.linefeed(builder);
    return this;
  }

  public StringHolder append(Object obj) {
    builder.append(obj);
    return this;
  }

  public StringHolder append(String str) {
    builder.append(str);
    return this;
  }

  public StringHolder append(StringBuffer sb) {
    builder.append(sb);
    return this;
  }

  public StringHolder append(CharSequence s) {
    builder.append(s);
    return this;
  }

  public StringHolder append(boolean b) {
    builder.append(b);
    return this;
  }

  public StringHolder append(char c) {
    builder.append(c);
    return this;
  }

  public StringHolder append(int i) {
    builder.append(i);
    return this;
  }

  public StringHolder append(long lng) {
    builder.append(lng);
    return this;
  }

  public StringHolder append(float f) {
    builder.append(f);
    return this;
  }

  public StringHolder append(double d) {
    builder.append(d);
    return this;
  }

  public String toString() {
    return builder.toString();
  }
}
