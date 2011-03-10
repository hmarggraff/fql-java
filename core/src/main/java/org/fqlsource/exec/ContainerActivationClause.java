package org.fqlsource.exec;

/**
 * Created by IntelliJ IDEA.
 * User: hmf
 * Date: 08.03.11
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
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
