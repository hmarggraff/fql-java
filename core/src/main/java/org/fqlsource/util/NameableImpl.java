package org.fqlsource.util;

/**
 */
public class NameableImpl extends NamedImpl
{
    public NameableImpl()
    {
        super(null);
    }

    public NameableImpl(String name)
    {
        super(name);
    }

    public void setName(String newName)
    {
        name = newName;
    }
}
