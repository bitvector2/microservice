package org.bitvector.microservice_test;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Hashtable;
import java.util.Map;

public class Utility {

    public static JsonArray resultSet2JsonArray(ResultSet rs) throws Exception {
        JsonArray json = new JsonArray();
        ColumnDefinitions cd = rs.getColumnDefinitions();
        Row rd;

        while (rs.iterator().hasNext()) {
            rd = rs.iterator().next();

            int numColumns = cd.size();
            JsonObject obj = new JsonObject();

            for (int i = 0; i < numColumns; i++) {
                String column_name = cd.getName(i);

                if (cd.getType(i) == DataType.text()) {
                    obj.put(column_name, rd.getString(column_name));
                } else {
                    obj.put(column_name, rd.getObject(column_name));
                }

            }

            json.add(obj);
        }

        return json;
    }

    public static Hashtable<String, Object> jsonArray2InsertValues(JsonArray arr) throws Exception {
        Hashtable<String, Object> insertValues = new Hashtable<String, Object>();
        JsonObject element = new JsonObject();

        for (int i = 0; i < arr.size(); i++) {
            element = arr.getJsonObject(i);
            element.fieldNames();
        }


        return insertValues;
    }

}
