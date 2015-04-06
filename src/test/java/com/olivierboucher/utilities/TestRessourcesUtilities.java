package com.olivierboucher.utilities;

import org.mockito.internal.util.io.IOUtil;

import java.io.InputStream;

/**
 * Created by olivierboucher on 15-04-05.
 */
public class TestRessourcesUtilities {
    public static String readStreamToEnd(InputStream iStream) {
        StringBuilder build = new StringBuilder();
        for (String line : IOUtil.readLines(iStream)) {
            build.append(line).append(System.lineSeparator());
        }

        return build.toString();
    }
}
