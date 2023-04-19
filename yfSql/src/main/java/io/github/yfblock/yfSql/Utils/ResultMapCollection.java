package io.github.yfblock.yfSql.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * ResultMapCollection.
 * Contains the class map as the result.
 * @author yufeng
 */
public class ResultMapCollection {
    /**
     * Use class name as the key.
     * Store result map as the value. 
     * TODO: Result map contains the setters and getters and others information.
     */
    public static Map<String, ResultMap> maps = new HashMap<>();
}
