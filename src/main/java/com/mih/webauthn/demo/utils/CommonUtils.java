package com.mih.webauthn.demo.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonUtils {
    public static String toStackTrace(Exception e)

    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            e.printStackTrace(pw);
            return sw.toString();
        } catch(Exception e1) {
            return "";
        }
    }
}
