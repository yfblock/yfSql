package io.github.yfblock.yfSql.Utils;

public class ParamUtil {
    public static String targetToString(Object obj) {
        return obj.toString();
    }

    public static boolean isBasicType(String typeName) {
        if(typeName == null) return false;
        if(typeName.equals("String")) return true;
        else if(typeName.equals("Integer") || typeName.equals("int")) return true;
        else if(typeName.equals("Float") || typeName.equals("float")) return true;
        else if(typeName.equals("Double") || typeName.equals("double")) return true;
        else if(typeName.equals("BigDecimal")) return true;
        else if(typeName.equals("Object")) return true;
        else if(typeName.equals("Time")) return true;
        else if(typeName.equals("Timestamp")) return true;
        else if(typeName.equals("DateTime")) return true;
        else if(typeName.equals("LocalDateTime")) return true;

        return false;
    }
}
