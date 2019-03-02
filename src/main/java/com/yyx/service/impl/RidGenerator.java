package com.yyx.service.impl;

import com.yyx.dao.PropertyDao;
import com.yyx.model.Property;
import com.yyx.util.LxiiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RidGenerator {
    private static final String RID_NAME = "nextRidDec";
    private static final int RID_LENGTH = 7;

    private PropertyDao propertyDao;

    @Autowired
    public RidGenerator(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    String generate() {
        Property property = propertyDao.find(RID_NAME);
        long decRid = Long.parseLong(property.getValue());
        if(decRid >= LxiiUtil.ceiling(RID_LENGTH)) {
            throw new RuntimeException("No space for new restaurants.");
        }
        String rid = LxiiUtil.convert(decRid, RID_LENGTH);
        property.setValue(String.valueOf(decRid + 1));
        return rid;
    }
}
