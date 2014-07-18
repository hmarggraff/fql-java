package org.funql.ri.exec.node;

import org.funql.ri.util.NamedIndex;

/**
 * Created by hmf on 17.07.2014.
 */
public class EquiJoinChecker implements NodeVisitor
{
    NamedIndex left;
    NamedIndex right;

    boolean dependentOnLeft;
    boolean dependentOnRight;

    public EquiJoinChecker(NamedIndex left, NamedIndex right)
    {
        this.left = left;
        this.right = right;
    }

    public boolean isDependentOnLeft()
    {
        return dependentOnLeft;
    }

    public boolean isDependentOnRight()
    {
        return dependentOnRight;
    }

    @Override
    public boolean process(FqlNodeInterface node)
    {
        if (node instanceof ContainerNameNode)
        {
            final ContainerNameNode containerNameNode = (ContainerNameNode) node;
            final NamedIndex against = containerNameNode.getContainerIndex();
            dependentOnLeft = dependentOnLeft || left.getIndex() == against.getIndex();
            dependentOnRight = dependentOnRight || right.getIndex() == against.getIndex();
            return dependentOnLeft && dependentOnRight;
        }
        return false;
    }
}
