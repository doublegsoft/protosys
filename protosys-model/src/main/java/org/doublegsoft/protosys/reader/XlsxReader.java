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

import com.doublegsoft.jcommons.metabean.*;
import com.doublegsoft.jcommons.metabean.type.DataType;
import com.doublegsoft.jcommons.metabean.type.DomainType;
import com.doublegsoft.jcommons.utils.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.inst2xsd.util.Attribute;
import org.doublegsoft.protosys.ReferenceExpression;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 */
public class XlsxReader implements MetaReader {

  private static final String EMPTY_STRING = "";

  private final Map<String, String> headerMappings = new HashMap<>();

  private final Map<String, Integer> headerAndIndex = new HashMap<>();

  public XlsxReader(Map<String, String> headerMappings) {
    this.headerMappings.putAll(headerMappings);
  }

  public ModelDefinition readFrom(InputStream[] streams) throws IOException {
    ModelDefinition retVal = new ModelDefinition();
    Map<AttributeDefinition, String> totalAttrRefs = new HashMap<>();
    for (InputStream stream : streams) {
      XSSFWorkbook wb = new XSSFWorkbook(stream);
      XSSFSheet sh = wb.getSheetAt(0);
      int rowCount = sh.getLastRowNum();

      List<ObjectDefinition> objs = new ArrayList<>();
      // THE FIRST COLUMN MUST BE HEADER
      XSSFRow headerRow = sh.getRow(0);
      int lastCellNum = headerRow.getPhysicalNumberOfCells();
      for (int i = 0; i < lastCellNum; i++) {
        XSSFCell cell = headerRow.getCell(i);
        if (cell != null) {
          headerAndIndex.put(cell.getStringCellValue(), i);
        }
      }
      Map<AttributeDefinition, String> attrRefs = buildAttributeBaseAndConstraint(sh, rowCount, retVal);
      totalAttrRefs.putAll(attrRefs);
      wb.close();
    }
    for (Map.Entry<AttributeDefinition, String> e : totalAttrRefs.entrySet()) {
      ReferenceExpression.build(e.getValue(), e.getKey(), retVal);
    }
    return retVal;
  }

  @Override
  public ModelDefinition readFrom(InputStream stream) throws IOException {
    ModelDefinition retVal = new ModelDefinition();
    XSSFWorkbook wb = new XSSFWorkbook(stream);
    XSSFSheet sh = wb.getSheetAt(0);
    int rowCount = sh.getLastRowNum();

    // THE FIRST COLUMN MUST BE HEADER
    XSSFRow headerRow = sh.getRow(0);
    int lastCellNum = headerRow.getPhysicalNumberOfCells();
    for (int i = 0; i < lastCellNum; i++) {
      XSSFCell cell = headerRow.getCell(i);
      if (cell != null) {
        headerAndIndex.put(cell.getStringCellValue(), i);
      }
    }
    Map<AttributeDefinition, String> attrRefs = buildAttributeBaseAndConstraint(sh, rowCount, retVal);
    wb.close();
    for (Map.Entry<AttributeDefinition, String> e : attrRefs.entrySet()) {
      ReferenceExpression.build(e.getValue(), e.getKey(), retVal);
    }
    return retVal;
  }

  private int getCellIndex(String modelField) {
    String userHeader = headerMappings.get(modelField);
    if (userHeader == null) {
      return -1;
    }
    if (!headerAndIndex.containsKey(userHeader)) {
      return -1;
    }
    return headerAndIndex.get(userHeader);
  }

  private String getCellString(XSSFRow row, String modelField) {
    Integer index = getCellIndex(modelField);
    if (index == -1) {
      return null;
    }
    XSSFCell cell = row.getCell(index);
    if (cell == null) {
      return null;
    }
    switch (cell.getCellType()) {
      case Cell.CELL_TYPE_STRING:
        return cell.getStringCellValue().trim();
      case Cell.CELL_TYPE_NUMERIC:
        return String.valueOf(cell.getNumericCellValue());
      case Cell.CELL_TYPE_BOOLEAN:
        return String.valueOf(cell.getBooleanCellValue());
      case Cell.CELL_TYPE_BLANK:
      default:
        return null;
    }
  }

  private boolean acceptBoolean(String value, String... accept) {
    if (value == null) {
      return false;
    }
    for (String str : accept) {
      if (str.equalsIgnoreCase(value) || Pattern.matches(str, value)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Builds the attribute base properties and constraint.
   *
   * @param sheet    metadata sheet
   * @param rowCount the row count
   * @param model    the model definition
   * @return the built pair information
   */
  private Map<AttributeDefinition, String> buildAttributeBaseAndConstraint(XSSFSheet sheet, int rowCount, ModelDefinition model) {
    Map<AttributeDefinition, String> retVal = new HashMap<>();
    String previousObjectPersistenceName = null;
    for (int i = 1; i <= rowCount; ++i) {
      XSSFRow row = sheet.getRow(i);
      if (row == null) {
        continue;
      }
      String objectPersistenceName = getCellString(row, OBJECT_PERSISTENCE_NAME);
      if (Strings.isEmpty(objectPersistenceName)) {
        continue;
      }
      String objectText = getCellString(row, OBJECT_TEXT);
      String objectName = getCellString(row, OBJECT_NAME);
      String objectRole = getCellString(row, OBJECT_ROLE);
      String attributeText = getCellString(row, ATTRIBUTE_TEXT);
      String attributeName = getCellString(row, ATTRIBUTE_NAME);
      String attributePersistenceName = getCellString(row, ATTRIBUTE_PERSISTENCE_NAME);


      ObjectDefinition obj = null;
      if (!objectPersistenceName.equals(previousObjectPersistenceName)) {
        previousObjectPersistenceName = objectPersistenceName;
        obj = new ObjectDefinition(objectName, model);
        obj.setPersistenceName(objectPersistenceName);
        obj.setText(objectText);
        if (!Strings.isEmpty(objectRole)) {
          obj.setRole(ObjectRole.getObjectRole(objectRole));
        } else {
          obj.setRole(ObjectRole.ENTITY);
        }
      } else {
        obj = model.findObjectByPersistenceName(objectPersistenceName);
      }
      AttributeDefinition attr = new AttributeDefinition(attributeName, obj);
      attr.setPersistenceName(attributePersistenceName);
      attr.setText(attributeText);

      String attributeRelationship = getCellString(row, ATTRIBUTE_RELATIONSHIP);
      if (!Strings.isEmpty(attributeRelationship)) {
        retVal.put(attr, attributeRelationship);
      }
      String attributeIdentifiable = getCellString(row, ATTRIBUTE_IDENTIFIABLE);
      attr.getConstraint().setIdentifiable(acceptBoolean(attributeIdentifiable, "\\d+"));

      String attributeSystem = getCellString(row, ATTRIBUTE_SYSTEM);
      attr.getConstraint().setSystem(acceptBoolean(attributeSystem, "Y", "TRUE", "T", "S"));

      String attributeUnit = getCellString(row, ATTRIBUTE_UNIT);
      if (!Strings.isEmpty(attributeUnit)) {
        attr.setUnit(attributeUnit);
      }

      String attributeDataType = getCellString(row, ATTRIBUTE_DATA_TYPE);
      if (!Strings.isEmpty(attributeDataType)) {
        attr.setType(new DataType(attributeDataType));
        attr.getConstraint().setDataType(attributeDataType);
      }

      String attributeDefaultValue = getCellString(row, ATTRIBUTE_DEFAULT_VALUE);
      if (!Strings.isEmpty(attributeDefaultValue)) {
        attr.getConstraint().setDefaultValue(attributeDefaultValue);
      }

      String attributeNullable = getCellString(row, ATTRIBUTE_NULLABLE);
      attr.getConstraint().setNullable(acceptBoolean(attributeNullable, "Y", "TRUE", "T", ""));

      String attributeExtension = getCellString(row, ATTRIBUTE_EXTENSION);
      if (attributeExtension != null) {
        attr.getExtension().setExtension(attributeExtension);
      }

      String attributeDomainType = getCellString(row, ATTRIBUTE_DOMAIN_TYPE);
      if (attributeDomainType != null) {
        attr.getConstraint().setDomainType(DomainType.getDomainType(attributeDomainType));
      }

      // test data
      String attributeTestdata = getCellString(row, ATTRIBUTE_TEST_DATA);
      if (attributeTestdata != null) {
        Map<String, String> opts = new HashMap<>();
        opts.put("data", attributeTestdata);
        attr.setLabelledOptions("testdata", opts);
      }
    }
    return retVal;
  }

}
