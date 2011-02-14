package org.fqlsource.util;

/**
 */
public class NameableImpl extends NamedImpl
{
    public NameableImpl()
    {
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
