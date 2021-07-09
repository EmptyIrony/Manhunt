package cn.charlotte.plugin.util.chat;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

public final class Coerce {
    private static final Pattern listPattern = Pattern.compile("^([(\\[{]?)(.+?)([)\\]}]?)$");
    private static final String[] listPairings = new String[]{"([{", ")]}"};

    private Coerce() {
    }

    public static double format(double value) {
        return BigDecimal.valueOf(value).setScale(2, 4).doubleValue();
    }

    public static String toString(@Nullable Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.getClass().isArray() ? toList(obj).toString() : obj.toString();
        }
    }

    public static Optional<String> asString(@Nullable Object obj) {
        if (obj instanceof String) {
            return Optional.of((String)obj);
        } else {
            return obj == null ? Optional.empty() : Optional.of(obj.toString());
        }
    }

    public static List<?> toList(@Nullable Object obj) {
        if (obj == null) {
            return Collections.emptyList();
        } else if (obj instanceof List) {
            return (List)obj;
        } else {
            Class<?> clazz = obj.getClass();
            if (clazz.isArray()) {
                return clazz.getComponentType().isPrimitive() ? primitiveArrayToList(obj) : Arrays.asList((Object[])obj);
            } else {
                return parseStringToList(obj.toString());
            }
        }
    }

    public static Optional<List<?>> asList(@Nullable Object obj) {
        if (obj == null) {
            return Optional.empty();
        } else if (obj instanceof List) {
            return Optional.of((List)obj);
        } else {
            Class<?> clazz = obj.getClass();
            if (clazz.isArray()) {
                return clazz.getComponentType().isPrimitive() ? Optional.of(primitiveArrayToList(obj)) : Optional.of(Arrays.asList((Object[])obj));
            } else {
                return Optional.of(parseStringToList(obj.toString()));
            }
        }
    }

    public static boolean toBoolean(@Nullable Object obj) {
        if (obj == null) {
            return false;
        } else {
            return obj instanceof Boolean ? (Boolean)obj : obj.toString().trim().matches("^(1|true|yes)$");
        }
    }

    public static Optional<Boolean> asBoolean(@Nullable Object obj) {
        if (obj instanceof Boolean) {
            return Optional.of((Boolean)obj);
        } else {
            return obj instanceof Byte ? Optional.of((Byte)obj != 0) : Optional.empty();
        }
    }

    public static int toInteger(@Nullable Object obj) {
        if (obj == null) {
            return 0;
        } else if (obj instanceof Number) {
            return ((Number)obj).intValue();
        } else {
            String strObj = sanitiseNumber(obj);
            Integer iParsed = Ints.tryParse(strObj);
            if (iParsed != null) {
                return iParsed;
            } else {
                Double dParsed = Doubles.tryParse(strObj);
                return dParsed != null ? dParsed.intValue() : 0;
            }
        }
    }

    public static Optional<Integer> asInteger(@Nullable Object obj) {
        if (obj == null) {
            return Optional.empty();
        } else if (obj instanceof Number) {
            return Optional.of(((Number)obj).intValue());
        } else {
            try {
                return Optional.ofNullable(Integer.valueOf(obj.toString()));
            } catch (NullPointerException | NumberFormatException var4) {
                String strObj = sanitiseNumber(obj);
                Integer iParsed = Ints.tryParse(strObj);
                if (iParsed == null) {
                    Double dParsed = Doubles.tryParse(strObj);
                    return dParsed == null ? Optional.empty() : Optional.of(dParsed.intValue());
                } else {
                    return Optional.of(iParsed);
                }
            }
        }
    }

    public static double toDouble(@Nullable Object obj) {
        if (obj == null) {
            return 0.0D;
        } else if (obj instanceof Number) {
            return ((Number)obj).doubleValue();
        } else {
            Double parsed = Doubles.tryParse(sanitiseNumber(obj));
            return parsed != null ? parsed : 0.0D;
        }
    }

    public static Optional<Double> asDouble(@Nullable Object obj) {
        if (obj == null) {
            return Optional.empty();
        } else if (obj instanceof Number) {
            return Optional.of(((Number)obj).doubleValue());
        } else {
            try {
                return Optional.ofNullable(Double.valueOf(obj.toString()));
            } catch (NullPointerException | NumberFormatException var3) {
                String strObj = sanitiseNumber(obj);
                Double dParsed = Doubles.tryParse(strObj);
                return dParsed == null ? Optional.empty() : Optional.of(dParsed);
            }
        }
    }

    public static float toFloat(@Nullable Object obj) {
        if (obj == null) {
            return 0.0F;
        } else if (obj instanceof Number) {
            return ((Number)obj).floatValue();
        } else {
            Float parsed = Floats.tryParse(sanitiseNumber(obj));
            return parsed != null ? parsed : 0.0F;
        }
    }

    public static Optional<Float> asFloat(@Nullable Object obj) {
        if (obj == null) {
            return Optional.empty();
        } else if (obj instanceof Number) {
            return Optional.of(((Number)obj).floatValue());
        } else {
            try {
                return Optional.ofNullable(Float.valueOf(obj.toString()));
            } catch (NullPointerException | NumberFormatException var3) {
                String strObj = sanitiseNumber(obj);
                Double dParsed = Doubles.tryParse(strObj);
                return dParsed == null ? Optional.empty() : Optional.of(dParsed.floatValue());
            }
        }
    }

    public static short toShort(@Nullable Object obj) {
        if (obj == null) {
            return 0;
        } else if (obj instanceof Number) {
            return ((Number)obj).shortValue();
        } else {
            try {
                return Short.parseShort(sanitiseNumber(obj));
            } catch (NumberFormatException var2) {
                return 0;
            }
        }
    }

    public static Optional<Short> asShort(@Nullable Object obj) {
        if (obj == null) {
            return Optional.empty();
        } else if (obj instanceof Number) {
            return Optional.of(((Number)obj).shortValue());
        } else {
            try {
                return Optional.ofNullable(Short.parseShort(sanitiseNumber(obj)));
            } catch (NullPointerException | NumberFormatException var2) {
                return Optional.empty();
            }
        }
    }

    public static byte toByte(@Nullable Object obj) {
        if (obj == null) {
            return 0;
        } else if (obj instanceof Number) {
            return ((Number)obj).byteValue();
        } else {
            try {
                return Byte.parseByte(sanitiseNumber(obj));
            } catch (NumberFormatException var2) {
                return 0;
            }
        }
    }

    public static Optional<Byte> asByte(@Nullable Object obj) {
        if (obj == null) {
            return Optional.empty();
        } else if (obj instanceof Number) {
            return Optional.of(((Number)obj).byteValue());
        } else {
            try {
                return Optional.ofNullable(Byte.parseByte(sanitiseNumber(obj)));
            } catch (NullPointerException | NumberFormatException var2) {
                return Optional.empty();
            }
        }
    }

    public static long toLong(@Nullable Object obj) {
        if (obj == null) {
            return 0L;
        } else if (obj instanceof Number) {
            return ((Number)obj).longValue();
        } else {
            try {
                return Long.parseLong(sanitiseNumber(obj));
            } catch (NumberFormatException var2) {
                return 0L;
            }
        }
    }

    public static Optional<Long> asLong(@Nullable Object obj) {
        if (obj == null) {
            return Optional.empty();
        } else if (obj instanceof Number) {
            return Optional.of(((Number)obj).longValue());
        } else {
            try {
                return Optional.ofNullable(Long.parseLong(sanitiseNumber(obj)));
            } catch (NullPointerException | NumberFormatException var2) {
                return Optional.empty();
            }
        }
    }

    public static char toChar(@Nullable Object obj) {
        if (obj == null) {
            return '\u0000';
        } else if (obj instanceof Character) {
            return (Character)obj;
        } else {
            try {
                return obj.toString().charAt(0);
            } catch (Exception var2) {
                return '\u0000';
            }
        }
    }

    public static Optional<Character> asChar(@Nullable Object obj) {
        if (obj == null) {
            return Optional.empty();
        } else if (obj instanceof Character) {
            return Optional.of((Character)obj);
        } else {
            try {
                return Optional.of(obj.toString().charAt(0));
            } catch (Exception var2) {
                return Optional.empty();
            }
        }
    }

    private static String sanitiseNumber(Object obj) {
        String string = obj.toString().trim();
        if (string.length() < 1) {
            return "0";
        } else {
            Matcher candidate = listPattern.matcher(string);
            if (listBracketsMatch(candidate)) {
                string = candidate.group(2).trim();
            }

            int decimal = string.indexOf(46);
            int comma = string.indexOf(44, decimal);
            if (decimal > -1 && comma > -1) {
                return sanitiseNumber(string.substring(0, comma));
            } else {
                return string.indexOf(45, 1) != -1 ? "0" : string.replace(",", "").split(" ")[0];
            }
        }
    }

    private static boolean listBracketsMatch(Matcher candidate) {
        return candidate.matches() && listPairings[0].indexOf(candidate.group(1)) == listPairings[1].indexOf(candidate.group(3));
    }

    private static List<?> primitiveArrayToList(Object obj) {
        if (obj instanceof boolean[]) {
            return Booleans.asList((boolean[])obj);
        } else if (obj instanceof char[]) {
            return Chars.asList((char[])obj);
        } else if (obj instanceof byte[]) {
            return Bytes.asList((byte[])obj);
        } else if (obj instanceof short[]) {
            return Shorts.asList((short[])obj);
        } else if (obj instanceof int[]) {
            return Ints.asList((int[])obj);
        } else if (obj instanceof long[]) {
            return Longs.asList((long[])obj);
        } else if (obj instanceof float[]) {
            return Floats.asList((float[])obj);
        } else {
            return obj instanceof double[] ? Doubles.asList((double[])obj) : Collections.emptyList();
        }
    }

    private static List<?> parseStringToList(String string) {
        Matcher candidate = listPattern.matcher(string);
        if (!listBracketsMatch(candidate)) {
            return Collections.emptyList();
        } else {
            List<String> list = Lists.newArrayList();
            String[] var3 = candidate.group(2).split(",");
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String part = var3[var5];
                if (part != null) {
                    list.add(part);
                }
            }

            return list;
        }
    }
}

