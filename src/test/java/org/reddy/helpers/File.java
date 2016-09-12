package org.reddy.helpers;

import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by dreddy on 8/26/2016.
 */
public class File {
    public static String readFile(String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), Charset.defaultCharset());
    }
}
