package org.fqlsource.util;

public class NamedImpl implements Named
{
    protected String name;

    public NamedImpl(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int compareTo(Named s)
    {
        return name.compareTo(s.getName());
    }

}
