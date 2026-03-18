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
package org.doublegsoft.protosys;

/**
 * {@link Action} enumerates the actions for information system development.
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 * @since 1.0
 */
public enum Action {

  /**
   * creates any data.
   */
  CREATE("create"),

  /**
   * updates any data for entities.
   */
  UPDATE("update"),

  /**
   * deletes entities from persistent system.
   */
  REMOVE("remove"),

  /**
   * changes the status to entities.
   */
  CHANGE("change"),

  /**
   * finds any data.
   */
  FIND("find"),

  /**
   * statistics entities.
   */
  STAT("stat");

  Action(String text) {
    this.text = text;
  }

  private final String text;

  public String getText() {
    return text;
  }
}
