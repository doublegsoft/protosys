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

import com.doublegsoft.apiml.bean.BeanAPIModelExpressionParser;
import com.doublegsoft.apiml.func.FunctionAPIModelExpressionParser;
import com.doublegsoft.apiml.mvc.MVCAPIModelExpressionParser;
import com.doublegsoft.apiml.rest.RESTAPIModelExpressionParser;
import com.doublegsoft.apiml.sql.SQLAPIModelExpressionParser;
import com.doublegsoft.guimlc.ProcessModel;
import com.doublegsoft.jcommons.lang.HashObject;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metamodel.ApiDefinition;
import com.doublegsoft.jcommons.metamodel.ApplicationApiDefinition;
import com.doublegsoft.jcommons.metamodel.ApplicationDefinition;
import com.doublegsoft.jcommons.metamodel.UsecaseDefinition;
import com.doublegsoft.jcommons.metamodel.func.FunctionApiModelDefinition;
import com.doublegsoft.jcommons.metamodel.mvc.MVCAPIModelDefinition;
import com.doublegsoft.jcommons.metamodel.rest.RestApiModelDefinition;
import com.doublegsoft.jcommons.metamodel.rest.RestApiModelDefinition;
import com.doublegsoft.jcommons.metamodel.sql.SqlApiModelDefinition;
import com.doublegsoft.jcommons.metamodel.sql.SqlApiModelDefinition;
import com.doublegsoft.jcommons.metaui.PageDefinition;
import com.doublegsoft.jcommons.metaui.WidgetDefinition;
import com.doublegsoft.jcommons.metaui.layout.Position;
import com.doublegsoft.jcommons.utils.Strings;
import com.doublegsoft.misuml.MisumlContext;
import io.doublegsoft.guidbase.GuidbaseAttr;
import io.doublegsoft.guidbase.GuidbaseContainer;
import io.doublegsoft.guidbase.GuidbaseContext;
import io.doublegsoft.guidbase.GuidbaseWidget;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * The plugin interface need to be implemented by any other detailed plugins.
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 * @since 1.0
 */
public interface Plugin {

  SQLAPIModelExpressionParser SQL_API_MODEL_EXPRESSION_PARSER = new SQLAPIModelExpressionParser();

  RESTAPIModelExpressionParser REST_API_MODEL_EXPRESSION_PARSER = new RESTAPIModelExpressionParser();

  MVCAPIModelExpressionParser MVC_API_MODEL_EXPRESSION_PARSER = new MVCAPIModelExpressionParser();

  BeanAPIModelExpressionParser BEAN_API_MODEL_EXPRESSION_PARSER = new BeanAPIModelExpressionParser();

  FunctionAPIModelExpressionParser FUNC_API_MODEL_EXPRESSION_PARSER = new FunctionAPIModelExpressionParser();

  /**
   * Generates the prototype source code for the misuml file.
   *
   * @param misumls      the more misuml contexts
   * @param model        the data model definition
   * @param outputRoot   the output root directory
   * @param templateRoot the template root directory or uri resources
   * @param globals      the global variables applied to template engine
   * @throws IOException in case of any errors
   */
  void prototype(MisumlContext[] misumls, ModelDefinition model, String outputRoot, String templateRoot, HashObject globals) throws IOException;

  /**
   * Converts the misuml (especially meta gui) and the model definition (metadata) to the application definition
   * applied for protosys code generation system.
   * <p>
   * The sws plugin uses misuml context (frontend and backend) and model definition (data model), so need the
   * conversion way.
   *
   * @param misuml the misuml context
   * @param model  the model definition (data model)
   * @return the application definition
   * @throws IOException in case of any IO errors
   */
  default ApplicationDefinition convertToApplication(MisumlContext misuml, ModelDefinition model) throws IOException {
    ApplicationDefinition retVal = new ApplicationDefinition();
    retVal.setName(misuml.get("name"));
    retVal.setModel(model);
    int apiCount = misuml.size("api");
    int usecaseCount = misuml.size("usecases");

    // sql, data, view, rest
    ApplicationApiDefinition apiAppSql = new ApplicationApiDefinition();
    ApplicationApiDefinition apiAppData = new ApplicationApiDefinition();
    ApplicationApiDefinition apiAppView = new ApplicationApiDefinition();
    ApplicationApiDefinition apiAppRest = new ApplicationApiDefinition();
    ApplicationApiDefinition apiAppService = new ApplicationApiDefinition();
    ApplicationApiDefinition apiAppDomain = new ApplicationApiDefinition();

    apiAppSql.setName(retVal.getName());
    apiAppSql.setOption("type", "sql");
    apiAppData.setName(retVal.getName());
    apiAppData.setOption("type", "data");
    apiAppView.setName(retVal.getName());
    apiAppView.setOption("type", "view");
    apiAppRest.setName(retVal.getName());
    apiAppRest.setOption("type", "rest");
    apiAppService.setName(retVal.getName());
    apiAppService.setOption("type", "service");
    apiAppDomain.setName(retVal.getName());
    apiAppDomain.setOption("type", "domain");

    for (int i = 0; i < apiCount; i++) {
      ApiDefinition api = new ApiDefinition();
      api.setName(misuml.get("api", i, "name"));
      api.setModule(misuml.get("api", i, "module"));
      api.setDescription(misuml.get("api", i, "description"));

      String sqlModel = misuml.get("api", i, "sqlModel");
      String restModel = misuml.get("api", i, "restModel");
      String viewModel = misuml.get("api", i, "viewModel");
      String dataModel = misuml.get("api", i, "dataModel");
      String serviceModel = misuml.get("api", i, "serviceModel");
      String domainModel = misuml.get("api", i, "domainModel");

//      if (viewModel != null) {
//        // view api model
//        String expr = misuml.get("api", i, "viewModel");
//        MVCAPIModelDefinition mvcApiModel = MVC_API_MODEL_EXPRESSION_PARSER.parse(expr, model);
//        api.setModel(mvcApiModel);
//        api.setName(mvcApiModel.getUri());
//        api.setType("view");
//        apiAppView.addAPI(api);
//      } else if (dataModel != null) {
//        String expr = misuml.get("api", i, "dataModel");
//        // data api model
//        MVCAPIModelDefinition mvcApiModel = MVC_API_MODEL_EXPRESSION_PARSER.parse(expr, model);
//        api.setModel(mvcApiModel);
//        api.setName(mvcApiModel.getUri());
//        api.setType("data");
//        apiAppData.addAPI(api);
//      } else if (restModel != null) {
//        String expr = misuml.get("api", i, "restModel");
//        // rest api model
//        RestApiModelDefinition RESTAPIModel = REST_API_MODEL_EXPRESSION_PARSER.parse(expr, model);
//        api.setModel(RESTAPIModel);
//        api.setType("rest");
//        api.setName(RESTAPIModel.getAction() + "#" + RESTAPIModel.getUri());
//        apiAppRest.addAPI(api);
//      } else if (sqlModel != null) {
//        String expr = misuml.get("api", i, "sqlModel");
//        SQLAPIModelDefinition SQLAPIModel = SQL_API_MODEL_EXPRESSION_PARSER.parse(expr, model);
//        api.setModel(SQLAPIModel);
//        api.setName(SQLAPIModel.getId());
//        api.setType("sql");
//        apiAppSql.addAPI(api);
//      } else if (serviceModel != null) {
//        String expr = misuml.get("api", i, "serviceModel");
//        FunctionAPIModelDefinition serviceApiModel = FUNC_API_MODEL_EXPRESSION_PARSER.parse(expr, model);
//        api.setModel(serviceApiModel);
//        api.setName(serviceApiModel.getId());
//        api.setType("service");
//        apiAppService.addAPI(api);
//      } else if (domainModel != null) {
//        String expr = misuml.get("api", i, "domainModel");
//        // TODO: TO IMPLEMENT DOMAIN MODEL PARSER
////                SQLAPIModelDefinition SQLAPIModel = SQL_API_MODEL_EXPRESSION_PARSER.parse(expr, model);
////                api.setModel(SQLAPIModel);
////                api.setName(SQLAPIModel.getId());
//        api.setType("domain");
//        apiAppSql.addAPI(api);
//      }
    }

    retVal.addAPI(apiAppSql);
    retVal.addAPI(apiAppData);
    retVal.addAPI(apiAppView);
    retVal.addAPI(apiAppRest);
    retVal.addAPI(apiAppService);
    retVal.addAPI(apiAppDomain);

    // graphic user interface (guic)
    for (int i = 0; i < usecaseCount; i++) {
      String module = misuml.get("usecases", i, "module");
      UsecaseDefinition usecase = new UsecaseDefinition(misuml.get("usecases", i, "name"));
      usecase.setModule(module);
      String expr = misuml.get("usecases", i, "view");
      GuidbaseContext guidbase = GuidbaseContext.from(new ByteArrayInputStream(expr.getBytes(StandardCharsets.UTF_8)));
      GuidbaseContainer page = guidbase.page();
      PageDefinition pagedef = new PageDefinition(module);
      pagedef.setId(page.id());
      pagedef.setType("page");
      pagedef.setModule(module);
      pagedef.setName(page.id());
      pagedef.setTitle(page.attr("text"));
      page.attrs().forEach(attr -> {
        pagedef.addOption(attr.name(), attr.value());
      });
      for (GuidbaseWidget widget : page.children()) {
        pagedef.addWidget(convertToWidget(widget, pagedef));
      }
//      usecase.setPage(pagedef);
      retVal.addUsecase(usecase);
    }
    return retVal;
  }

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
      ProcessModel processModel = ProcessModel.createInstance(process);
      retVal.addOption("processModel", processModel);
      retVal.setProcess(widget.process());
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
