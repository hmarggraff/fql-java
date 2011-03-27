package org.fqlsource.exec;

public abstract class ContainerActivationClause implements FqlStatement
{
    protected String alias;
    protected final int connectionIndex;
    protected String streamName;

    public ContainerActivationClause(int connectionIndex, String alias, String streamName)
    {
        this.connectionIndex = connectionIndex;
        this.alias = alias;
        this.streamName = streamName;
    }

    public String getAlias()
    {
        return alias;
    }
}
