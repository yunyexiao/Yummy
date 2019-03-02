package com.yyx.service.impl;

import com.yyx.dao.PropertyDao;
import com.yyx.model.Property;
import com.yyx.util.LxiiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class should be used in a service method with @Transactional annotated.
 */
@Component
public class EmailKeyGenerator {
    private static final String NEXT_EMAIL_KEY_NAME = "nextEmailKeyDec";
    private static final int KEY_LENGTH = 10;

    private PropertyDao propertyDao;

    @Autowired
    public EmailKeyGenerator(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    String generate() {
        Property property = propertyDao.find(NEXT_EMAIL_KEY_NAME);
        long decKey = Long.parseLong(property.getValue());
        if(decKey >= LxiiUtil.ceiling(KEY_LENGTH)) {
            throw new RuntimeException("No enough space for new customers.");
        }
        String key = LxiiUtil.convert(decKey, KEY_LENGTH);
        // prepare for next key
        property.setValue(String.valueOf(decKey + 1));

        return key;
    }
}
