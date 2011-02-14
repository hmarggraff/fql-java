package org.fqlsource.parser;

abstract class Res
{
    public static String msg(String key, String param)
    {
        return key + " " + param;
    }

    public static String str(String s)
    {
        return s;
    }
}
