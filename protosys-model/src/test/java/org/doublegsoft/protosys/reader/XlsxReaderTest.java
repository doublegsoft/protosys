package org.doublegsoft.protosys.reader;

import com.doublegsoft.jcommons.metabean.AttributeDefinition;
import com.doublegsoft.jcommons.metabean.ModelDefinition;
import com.doublegsoft.jcommons.metabean.RelationshipDefinition;
import com.doublegsoft.jcommons.metabean.RelationshipStyle;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gg on 2016/9/1.
 */
public class XlsxReaderTest {

    @Ignore
    public void testComplexModel() throws Exception {
        Map<String, String> mappings = new HashMap<>();
        mappings.put(MetaReader.OBJECT_PERSISTENCE_NAME, "表名");
        mappings.put(MetaReader.OBJECT_NAME, "表说明");
        mappings.put(MetaReader.OBJECT_ROLE, "表类型");
        mappings.put(MetaReader.ATTRIBUTE_NAME, "字段说明");
        mappings.put(MetaReader.ATTRIBUTE_PERSISTENCE_NAME, "字段名");
        mappings.put(MetaReader.ATTRIBUTE_DATA_TYPE, "数据类型");
        mappings.put(MetaReader.ATTRIBUTE_UNIT, "单位");
        mappings.put(MetaReader.ATTRIBUTE_NULLABLE, "可以为空");
        mappings.put(MetaReader.ATTRIBUTE_DEFAULT_VALUE, "默认值");
        mappings.put(MetaReader.ATTRIBUTE_SYSTEM, "列属性");
        mappings.put(MetaReader.ATTRIBUTE_IDENTIFIABLE, "主键序号");
        mappings.put(MetaReader.ATTRIBUTE_RELATIONSHIP, "关联表达式");
        mappings.put(MetaReader.ATTRIBUTE_EXTENSION, "列属性");
        XlsxReader reader = new XlsxReader(mappings);
        ModelDefinition model = reader.readFrom(new FileInputStream("E:/local/works/kehaoinfo.com/sws/doc/智慧水利系统数据库表结构.xlsx"));
        System.out.println("总共有" + model.getObjects().length + "个对象");

        Assert.assertTrue("测试主键为是。", model.findAttributeByPersistenceNames("wrp_rsr_bsin", "rscd").getConstraint().isIdentifiable());
        Assert.assertFalse("测试主键为否。", model.findAttributeByPersistenceNames("wrp_rsr_bsin", "rsnm").getConstraint().isIdentifiable());

        Assert.assertFalse("测试可以为空为否。", model.findAttributeByPersistenceNames("wrp_rsr_bsin", "rsnm").getConstraint().isNullable());
        // Assert.assertTrue("测试可以为空为是。", model.findAttributeByPersistenceNames("wrp_rsr_bsin", "alias").getConstraint().isNullable());

        AttributeDefinition attr = model.findAttributeByPersistenceNames("wrp_rsr_hych", "rscd");
        RelationshipDefinition rel = attr.getDirectRelationship();
        Assert.assertTrue(rel.getTargetAttribute().equals(model.findAttributeByPersistenceNames("wrp_rsr_bsin", "rscd")));
        Map<AttributeDefinition, AttributeDefinition> boundeds = rel.getBoundedTargetAttributesAndSelfAttributes();
        Assert.assertEquals("这个表达式只可能有一个", 1, boundeds.size());
        Assert.assertEquals("和getTargetAttribute应该一致", rel.getTargetAttribute(), boundeds.keySet().iterator().next());

        attr = model.findAttributeByPersistenceNames("wrp_rsr_bsin", "rscd");
        System.out.println("wrp_rsr_bsin共被关联" +attr.getIndirectRelationships().size() + "次");

        attr = model.findAttributeByPersistenceNames("sws_insp_rec", "wrpcd");
        rel = attr.getDirectRelationship();
        Map<Object, AttributeDefinition> prerequisites = rel.listPrerequisites();
        for (Map.Entry<Object, AttributeDefinition> prerequisite : prerequisites.entrySet()) {
            System.out.println(prerequisite.getValue() + "   " + prerequisite.getKey());
        }
        boundeds = rel.getBoundedTargetAttributesAndSelfAttributes();
        Assert.assertTrue("有前置条件不能直接找到绑定的属性", boundeds.isEmpty());

        for (Map.Entry<Object, AttributeDefinition> prerequisite : prerequisites.entrySet()) {
            boundeds = rel.getBoundedTargetAttributesAndSelfAttributes(prerequisite.getValue(), prerequisite.getKey());
            Assert.assertFalse("通过前置条件能够找到绑定的属性", boundeds.isEmpty());
            for (Map.Entry<AttributeDefinition, AttributeDefinition> bounded : boundeds.entrySet()) {
                System.out.println(bounded.getKey() + "   " + bounded.getValue());
            }
        }

        attr = model.findAttributeByPersistenceNames("sws_insp_rec", "prtcd");
        rel = attr.getDirectRelationship();
        boundeds = rel.getBoundedTargetAttributesAndSelfAttributes();
        Assert.assertEquals("巡检记录表的巡检部位字段", 2, boundeds.size());
        for (Map.Entry<AttributeDefinition, AttributeDefinition> bounded : boundeds.entrySet()) {
            System.out.println(bounded.getKey() + "   " + bounded.getValue());
        }
        Assert.assertEquals("巡检记录表的巡检记录字段和巡检部位表的关系是多对一", RelationshipStyle.MANY_TO_ONE, rel.getStyle());
    }

}
