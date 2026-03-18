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

import com.doublegsoft.jcommons.metabean.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The reference expression parser.
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 * @since 1.0
 */
public class ReferenceExpression {

  private List<Condition> conditions = new ArrayList<>();

  private static ReferenceExpression parse(String expr) {
    ReferenceExpression retVal = new ReferenceExpression();
    String[] lines = expr.split(";");
    for (String line : lines) {
      if (line.trim().length() == 0) {
        continue;
      }
      line = line.trim();
      String[] conditionAndRef = line.split(":");
      Condition cond = new Condition();
      String refExpr = null;
      if (conditionAndRef.length == 1) {
        refExpr = conditionAndRef[0];
      } else if (conditionAndRef.length == 2) {
        String[] columnAndConstant = conditionAndRef[0].split("=");
        cond.column = columnAndConstant[0].trim();
        cond.constant = columnAndConstant[1].trim().replaceAll("@", "");
        refExpr = conditionAndRef[1];
      }
      refExpr = refExpr.trim();
      Ref ref = new Ref();
      ref.table = refExpr.substring(0, refExpr.indexOf("(")).trim();
      String boundedExpr = refExpr.substring(refExpr.indexOf("(") + 1, refExpr.indexOf(")"));
      for (String each : boundedExpr.split(",")) {
        Bounded bounded = new Bounded();
        if (each.contains("=")) {
          String[] columnAndValue = each.split("=");
          bounded.column = columnAndValue[0].trim();
          if (columnAndValue[1].indexOf("@") == 0) {
            bounded.constant = columnAndValue[1].trim().replaceAll("@", "");
          } else {
            bounded.anotherColumn = columnAndValue[1].trim();
          }
        } else {
          bounded.column = each.trim();
        }
        ref.boundeds.add(bounded);
      }
      cond.ref = ref;
      if (line.endsWith("*")) {
        ref.style = "*";
      }
      retVal.conditions.add(cond);
    }
    return retVal;
  }

  public static void build(String expr, AttributeDefinition attr, ModelDefinition model) {
    ReferenceExpression re = parse(expr);
    for (Condition cond : re.conditions) {
      ObjectDefinition directObj = model.findObjectByPersistenceName(cond.ref.table);
      if (directObj == null) {
        // throw new NullPointerException("not found table: " + cond.ref.table);
        return;
      }
      attr.addRelationship(directObj, getRelationshipStyle(cond.ref.style));
      RelationshipDefinition rel = attr.getDirectRelationship();
      for (Bounded bounded : cond.ref.boundeds) {
        AttributeDefinition boundedAttr = model.findAttributeByPersistenceNames(cond.ref.table, bounded.column);
        attr.addRelationship(boundedAttr, getRelationshipStyle(cond.ref.style));
        if (boundedAttr == null) {
          throw new NullPointerException("not found column: " + cond.ref.table + " " + bounded.column);
        }
        if (bounded.constant != null) {
          rel.addBoundedByPersistenceName(attr.getParent().getPersistenceName(), cond.column, cond.constant, boundedAttr, bounded.constant);
        } else {
          if (bounded.anotherColumn != null && !bounded.anotherColumn.isEmpty()) {
            AttributeDefinition selfAttr = model.findAttributeByPersistenceNames(attr.getParent().getPersistenceName(), bounded.anotherColumn);
            rel.addBoundedByPersistenceName(attr.getParent().getPersistenceName(), cond.column, cond.constant, boundedAttr, selfAttr);
          } else {
            rel.addBoundedByPersistenceName(attr.getParent().getPersistenceName(), cond.column, cond.constant, boundedAttr, attr);
          }
        }
      }
    }
  }

  private static RelationshipStyle getRelationshipStyle(String style) {
    if ("*".equals(style)) {
      return RelationshipStyle.MANY_TO_ONE;
    }
    return RelationshipStyle.ONE_TO_ONE;
  }

  private static class Condition {
    String column = "";

    String constant;

    Ref ref;
  }

  private static class Bounded {
    String column = "";

    String anotherColumn = "";

    String constant;
  }

  private static class Ref {
    String table;

    List<Bounded> boundeds = new ArrayList<>();

    String style = "1";
  }

}
