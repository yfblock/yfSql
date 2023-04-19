package io.github.yfblock.yfSql.Utils;

/**
 * @author yufeng
 */
public class ParamUtil {
    public static boolean isBasicType(String typeName) {
        if(typeName == null) {
            return false;
        }
        switch (typeName) {
            case "String":
            case "Integer":
            case "int":
            case "Float":
            case "float":
            case "Double":
            case "double":
            case "BigDecimal":
            case "Object":
            case "LocalDateTime":
            case "DateTime":
            case "Timestamp":
            case "Time":
                return true;
        }

        return false;
    }
}
