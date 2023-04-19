package io.github.yfblock.yfSql.Utils;

/**
 * @author yufeng
 */
public class StringUtil {
    /**
     * convert camel case str to underline case.
     * @param camelCaseStr the str will be converted.
     * @return underline case
     */
    public static String toUnderlineCase(String camelCaseStr) {
        if (camelCaseStr == null) {
            return null;
        }
        // convert str to chars array.
        char[] charArray = camelCaseStr.toCharArray();
        StringBuilder buffer = new StringBuilder();
        // process chars.
        for (int i = 0, l = charArray.length; i < l; i++) {
            if (charArray[i] >= 65 && charArray[i] <= 90) {
                buffer.append("_").append(charArray[i] += 32);
            } else {
                buffer.append(charArray[i]);
            }
        }
        return buffer.toString();
    }
}
