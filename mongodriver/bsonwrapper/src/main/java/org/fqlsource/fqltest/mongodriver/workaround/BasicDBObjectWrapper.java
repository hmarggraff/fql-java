package org.fqlsource.fqltest.mongodriver.workaround;

import com.mongodb.BasicDBObject;

/**
 */
public class BasicDBObjectWrapper {
    BasicDBObject target = new BasicDBObject();
    public void put(String key, Object val) {
        target.put(key, val);
    }

    public BasicDBObject getTarget() {
        return target;
    }
}
