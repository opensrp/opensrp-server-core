package org.opensrp.domain;

import org.junit.Assert;
import org.junit.Test;

public class CSVRowConfigTest {

    @Test
    public void testConfigAccessors(){

        CSVRowConfig csvRowConfig = new CSVRowConfig();
        csvRowConfig.setColumnName("column_name");
        Assert.assertEquals("column_name", csvRowConfig.getColumnName());

        csvRowConfig.setFieldMapping("client.birthdate");
        Assert.assertEquals("client.birthdate", csvRowConfig.getFieldMapping());

        csvRowConfig.setRequired(true);
        Assert.assertTrue(csvRowConfig.isRequired());

        csvRowConfig.setRegex("1235");
        Assert.assertEquals("1235",csvRowConfig.getRegex());
    }

    @Test
    public void testConfigValidation(){
        CSVRowConfig csvRowConfig = new CSVRowConfig();
        csvRowConfig.setRequired(true);
        csvRowConfig.setRegex("[0-9]+");

        // number value
        Assert.assertTrue(csvRowConfig.validate("12345"));

        // string with invalid characters is false
        Assert.assertFalse(csvRowConfig.validate("asdasd"));

        csvRowConfig.setRequired(false);
        // an empty string is fine if not required
        Assert.assertTrue(csvRowConfig.validate(""));

    }
}
