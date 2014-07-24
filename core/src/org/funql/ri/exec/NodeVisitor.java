package org.funql.ri.exec;

import org.funql.ri.exec.node.FqlNodeInterface;

/**
 * Created by hmf on 17.07.2014.
 */
public interface NodeVisitor
{
    boolean process(FqlNodeInterface node);
}
