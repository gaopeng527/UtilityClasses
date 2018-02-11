import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by gaojianren on 2017/7/14.
 */
public class DeepParseString {

    private static final int PEEKED_VALUE_NONE = 0;   // 什么都没取到
    private static final int PEEKED_NEGATIVE_SIGN = 1; // 取到负号
    private static final int PEEKED_VALUE_INTEGER = 2; // 取到整形
    private static final int PEEKED_DECIMAL_POINT = 3; // 取到小数点
    private static final int PEEKED_VALUE_FLOAT = 4;      // 取到浮点型
    private static final int PEEKED_EXPONENT_CHAR = 5;  // 取到指数e或者E
    private static final int PEEKED_EXPONENT_SIGN = 6; // 取到指数的正负号
    private static final int PEEKED_VALUE_DOUBLE = 7; // 取到双精度型

    private static final int DETECT_TYPE_STRING = 8;
    private static final int DETECT_TYPE_LONG = 9;
    private static final int DETECT_TYPE_DOUBLE = 10;

    /**
     * 解析字符串的主方法
     *
     * @param str 要解析的字符串
     * @return 如果是JSON对象，返回解析后的JSONObject；如果是JSON数组，返回解析后的JSONArray；如果是jsonp，返回解析jsonp的结果；
     * 否则，返回数值型解析结果
     * @author gaojianren
     */
    public static Object deepParseString(String str) {
        if (str.length() <= 0) {
            return str;
        }
        char startChar = str.charAt(0);
        switch (startChar) {
            case '{':
                try {
                    JSONObject data = JSON.parseObject(str);
                    return parseMap(data);
                } catch (Exception e) {
                    return str;
                }
            case '[':
                try {
                    JSONArray data = JSON.parseArray(str);
                    return parseArray(data);
                } catch (Exception e) {
                    return str;
                }
            default:
                // 判断是否是jsonp
                if (str.charAt(str.length() - 1) == ')') {
                    int i = str.indexOf('(');
                    if (i < 0) {
                        return str;
                    }
                    String subStr = str.substring(i + 1, str.length() - 1);
                    if (subStr.length() <= 0) {
                        return str;
                    }
                    if (subStr.charAt(0) == '{') {
                        try {
                            JSONObject data = JSON.parseObject(subStr);
                            return parseMap(data);
                        } catch (Exception e) {
                            return str;
                        }
                    } else if (subStr.charAt(0) == '[') {
                        try {
                            JSONArray data = JSON.parseArray(subStr);
                            return parseArray(data);
                        } catch (Exception e) {
                            return str;
                        }
                    }
                }
        }
        return parseNumberStr(str);
    }

    /**
     * 解析Map<String, Object>对象
     *
     * @param map 要解析的Map<String, Object>对象
     * @return 解析后的Map<String, Object>对象
     * @author gaojianren
     */
    public static Map<String, Object> parseMap(Map<String, Object> map) {
        for (String key : map.keySet()) {
            Object obj = map.get(key);
            if (obj instanceof Map) {
                map.put(key, parseMap((Map<String, Object>) obj));
            } else if (obj instanceof List) {
                map.put(key, parseArray((List<Object>) obj));
            } else if (obj instanceof String) {
                map.put(key, deepParseString((String) obj));
            }
        }
        return map;
    }

    /**
     * 解析List<Object>对象
     *
     * @param list 要解析的List<Object>对象
     * @return 解析后的List<Object>对象
     * @author gaojianren
     */
    private static List<Object> parseArray(List<Object> list) {
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if (obj instanceof Map) {
                list.set(i, parseMap((Map<String, Object>) obj));
            } else if (obj instanceof List) {
                list.set(i, parseArray((List<Object>) obj));
            } else if (obj instanceof String) {
                list.set(i, deepParseString((String) obj));
            }
        }
        return list;
    }

    /**
     * 解析字符串
     *
     * @param str 要解析的字符串
     * @return 如果是数值型的字符串，返回对应的数值；否则，返回原字符串
     * @author gaojianren
     */
    public static Object parseNumberStr(String str) {
        int type = detectType(str);
        if (type == DETECT_TYPE_LONG) {
            long value = Long.parseLong(str);
            // 判断是否可以表示成整形
            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                return (int) value;
            }
            return value;
        } else if (type == DETECT_TYPE_DOUBLE) {
            double value = Double.parseDouble(str);
            return value;
        }

        return str;
    }

    /**
     * 尝试以数值形式解析字符串，并返回字符串类型
     *
     * @param str 要解析的字符串
     * @return 返回字符串类型
     * @author gaojianren
     */
    private static int detectType(String str) {
        long value = 0L; // 存放中间过程中读取的数值，先按负数存储
        boolean negative = false;  // 是否是负数
        boolean fitsInLong = true; // 是否匹配长整型
        int alreadyPeek = PEEKED_VALUE_NONE; // 已经取到字段情况
        int i = 0; // 当前索引位置

        for (i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '+':
                    if (alreadyPeek != PEEKED_EXPONENT_CHAR) {
                        return DETECT_TYPE_STRING;
                    }

                    alreadyPeek = PEEKED_EXPONENT_SIGN;
                    break;
                case '-':
                    if (alreadyPeek == PEEKED_VALUE_NONE) {
                        negative = true;
                        alreadyPeek = PEEKED_NEGATIVE_SIGN;
                    } else {
                        if (alreadyPeek != PEEKED_EXPONENT_CHAR) {
                            return DETECT_TYPE_STRING;
                        }

                        alreadyPeek = PEEKED_EXPONENT_SIGN;
                    }
                    break;
                case '.':
                    if (alreadyPeek != PEEKED_VALUE_INTEGER) {
                        return DETECT_TYPE_STRING;
                    }

                    alreadyPeek = PEEKED_DECIMAL_POINT;
                    break;
                case 'E':
                case 'e':
                    if (alreadyPeek != PEEKED_VALUE_INTEGER && alreadyPeek != PEEKED_VALUE_FLOAT) {
                        return DETECT_TYPE_STRING;
                    }

                    alreadyPeek = PEEKED_EXPONENT_CHAR;
                    break;
                default:
                    if (c < 48 || c > 57) { // 字符不在'0'~'9'之间
                        return DETECT_TYPE_STRING;
                    }

                    if (alreadyPeek != PEEKED_NEGATIVE_SIGN && alreadyPeek != PEEKED_VALUE_NONE) {
                        if (alreadyPeek == PEEKED_VALUE_INTEGER) {  // 已取为整数，又取到数字
                            if (value == 0L) {  // 如果之前取到的整数值为0，说明在字符串开头出现了0
                                return DETECT_TYPE_STRING;
                            }

                            long newValue = value * 10L - (long) (c - 48);
                            // 判断是否超过长整型范围，非精确判断
                            fitsInLong &= value > -922337203685477580L || value == -922337203685477580L && newValue < value;
                            value = newValue;
                        } else if (alreadyPeek == PEEKED_DECIMAL_POINT) {
                            alreadyPeek = PEEKED_VALUE_FLOAT;
                        } else if (alreadyPeek == PEEKED_EXPONENT_CHAR || alreadyPeek == PEEKED_EXPONENT_SIGN) {
                            alreadyPeek = PEEKED_VALUE_DOUBLE;
                        }
                    } else { // 为长整形计算赋初值
                        value = (long) (-(c - 48));
                        alreadyPeek = PEEKED_VALUE_INTEGER;
                    }
            }
        }

        if (alreadyPeek != PEEKED_VALUE_INTEGER || !fitsInLong || value == Long.MIN_VALUE && !negative) {
            if (alreadyPeek != PEEKED_VALUE_INTEGER && alreadyPeek != PEEKED_VALUE_FLOAT && alreadyPeek != PEEKED_VALUE_DOUBLE) {
                // 对"1."这种写法做特殊处理
                if (alreadyPeek == PEEKED_DECIMAL_POINT && i == str.length()) {
                    return DETECT_TYPE_DOUBLE;
                }
                return DETECT_TYPE_STRING;
            } else {
                // 如果整形超过长整型范围，按字符串存储
                if (alreadyPeek == PEEKED_VALUE_INTEGER && !fitsInLong) {
                    return DETECT_TYPE_STRING;
                }
                return DETECT_TYPE_DOUBLE;
            }
        } else {
            return DETECT_TYPE_LONG;
        }
    }

}
