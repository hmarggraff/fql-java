package org.funql.ri.exec.node;

import org.funql.ri.util.NamedIndex;

/**
 */
public class EntryPointSlot extends NamedIndex
{
    private final String entryPointName;
    final int entryPointIndex;

    public EntryPointSlot(String name, int index, String entryPointName, int entryPointIndex)
    {
	super(name, index);
	this.entryPointName = entryPointName;
	this.entryPointIndex = entryPointIndex;
    }

    public EntryPointSlot(NamedIndex nix, String entryPointName, int iteratorCount)
    {
	this(nix.getName(), nix.index, entryPointName, iteratorCount);
    }

    public int getEntryPointIndex()
    {
	return entryPointIndex;
    }

    public String getEntryPointName()
    {
	return entryPointName;
    }
}
