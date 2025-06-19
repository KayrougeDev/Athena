package fr.kayrouge.athena.common.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CTextUtil {

    public static String replaceTokens(String text, Map<String, String> repl) {
        Pattern p = Pattern.compile("\\{(.+?)}");
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            String val = repl.getOrDefault(key, m.group(0));
            m.appendReplacement(sb, Matcher.quoteReplacement(val));
        }
        m.appendTail(sb);
        return sb.toString();
    }

}
