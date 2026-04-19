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
package org.doublegsoft.protosys.plugin;

import com.doublegsoft.jcommons.lang.HashObject;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metaui.PageDefinition;
import com.doublegsoft.jcommons.metaui.WidgetDefinition;
import com.doublegsoft.jcommons.metaui.layout.Position;
import com.doublegsoft.jcommons.utils.Strings;
import io.doublegsoft.guidbase.GuidbaseAttr;
import io.doublegsoft.guidbase.GuidbaseContainer;
import io.doublegsoft.guidbase.GuidbaseWidget;

import java.io.IOException;

/**
 * The plugin interface need to be implemented by any other detailed plugins.
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 * @since 1.0
 */
public interface Plugin {

  /**
   * Generates the prototype source code for the misuml file.
   *
   * @param model        the data model definition
   * @param outputRoot   the output root directory
   * @param templateRoot the template root directory or uri resources
   * @param globals      the global variables applied to template engine
   * @throws IOException in case of any errors
   */
  void prototype(ModelDefinition model, String outputRoot, String templateRoot, HashObject globals) throws IOException;

  default WidgetDefinition convertToWidget(GuidbaseWidget widget, PageDefinition page) {
    WidgetDefinition retVal = new WidgetDefinition();
    retVal.setPage(page);
    // id
    retVal.setId(widget.id());
    // type
    retVal.setType(widget.type());
    if (!Strings.isEmpty(widget.attr("pos"))) {
      retVal.setPosition(Position.at(widget.attr("pos")));
    } else {
      retVal.setPosition(Position.at(widget.attr("position")));
    }
    retVal.setSize(retVal.getPosition().getSize());
    retVal.addOption("type", widget.type());
    // process
    if (!Strings.isEmpty(widget.process())) {
      String process = widget.process();
      process = process.replace('{', ' ').replace('}', ' ');
      // TODO: RE-IMPLEMENT IT
//      ProcessModel processModel = ProcessModel.createInstance(process);
//      retVal.addOption("processModel", processModel);
//      retVal.setProcess(widget.process());
    }
    // options
    for (GuidbaseAttr attr : widget.attrs()) {
      retVal.addOption(attr.name(), attr.value());
    }
    // children
    if (!widget.primitive()) {
      GuidbaseContainer container = (GuidbaseContainer) widget;
      for (GuidbaseWidget child : container.children()) {
        retVal.addWidget(convertToWidget(child, page));
      }
    }
    return retVal;
  }

}
