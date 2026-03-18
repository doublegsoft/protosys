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
package org.doublegsoft.protosys.reader;

import com.doublegsoft.jcommons.metabean.ModelDefinition;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public interface MetaReader {

  String OBJECT_TEXT = "obj.text";

  String OBJECT_NAME = "obj.name";

  String OBJECT_PERSISTENCE_NAME = "obj.persistenceName";

  String OBJECT_ROLE = "obj.role";

  String ATTRIBUTE_TEXT = "attr.text";

  String ATTRIBUTE_PERSISTENCE_NAME = "attr.persistenceName";

  String ATTRIBUTE_NAME = "attr.name";

  String ATTRIBUTE_DATA_TYPE = "attr.dataType";

  String ATTRIBUTE_UNIT = "attr.unit";

  String ATTRIBUTE_NULLABLE = "attr.nullable";

  String ATTRIBUTE_DEFAULT_VALUE = "attr.defaultValue";

  String ATTRIBUTE_SYSTEM = "attr.system";

  String ATTRIBUTE_IDENTIFIABLE = "attr.identifiable";

  String ATTRIBUTE_RELATIONSHIP = "attr.relationship";

  String ATTRIBUTE_EXTENSION = "attr.extension";

  String ATTRIBUTE_DOMAIN_TYPE = "attr.domain";

  String ATTRIBUTE_TEST_DATA = "attr.testdata";

  ModelDefinition readFrom(InputStream stream) throws IOException;

}
