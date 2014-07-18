package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.FunqlConnection;
import org.funql.ri.data.NamedValues;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.util.MultiMapBoolean;
import org.funql.ri.util.NamedValuesImpl;

import java.util.Iterator;
import java.util.List;

/**
 * Created by hmf on 17.07.2014.
 */
public class JoinClause implements FqlStatement
{
    private final String containerName;
    private final FqlNodeInterface joinExpression;
    private final EntryPointSlot connectionSlot;
    private final EntryPointSlot upstreamSlot;
    private final boolean outerLeft;
    private final boolean outerRight;
    private final String[] names;

    public JoinClause(String containerName, String alias, EntryPointSlot connectionSlot, FqlNodeInterface joinExpression, EntryPointSlot upstreamSlot, boolean outerLeft, boolean outerRight)
    {
        this.containerName = containerName;
        this.connectionSlot = connectionSlot;
        this.joinExpression = joinExpression;
        this.upstreamSlot = upstreamSlot;
        this.outerLeft = outerLeft;
        this.outerRight = outerRight;
        names = new String[]{upstreamSlot.getEntryPointName(), alias};
    }

    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException
    {
        // if joinExpression is equiJoin process hash join otherwise full product
        if (joinExpression instanceof EqualsNode)
        {
            final EqualsNode equalsNode = (EqualsNode) joinExpression;
            final EquiJoinChecker equiJoinCheckerLeft = new EquiJoinChecker(upstreamSlot, connectionSlot);
            final boolean leftClean = equalsNode.left.visit(equiJoinCheckerLeft);
            if (leftClean)
            {
                final EquiJoinChecker equiJoinCheckerRight = new EquiJoinChecker(upstreamSlot, connectionSlot);
                final boolean rightClean = equalsNode.left.visit(equiJoinCheckerRight);
                if (equiJoinCheckerLeft.isDependentOnLeft() && equiJoinCheckerRight.isDependentOnRight() ||
                      equiJoinCheckerRight.isDependentOnLeft() && equiJoinCheckerRight.isDependentOnLeft())
                {
                    return doEquiJoin(env, precedent, equiJoinCheckerLeft.isDependentOnLeft() && equiJoinCheckerRight.isDependentOnRight(), equalsNode);
                }

            }


        }
        throw new RuntimeException("Currently only equi-joins without crossings are implemented");
    }

    private FqlIterator doEquiJoin(final RunEnv env, final FqlIterator precedent, final boolean leftRight, final EqualsNode equalsNode)
    {
        final MultiMapBoolean<Object, NamedValues> leftHash = getLeftvaluesAsMultiMap(env, leftRight, equalsNode);

        return new FqlIterator()
        {
            List<NamedValues> leftValues;
            int leftIx;
            NamedValues rightValue;
            Iterator<MultiMapBoolean.ListBoolPair<NamedValues>> remainingLefts;
            Iterator<NamedValues> remainingLeftsInner;

            public NamedValues next() throws FqlDataException
            {
                for (; ; )
                {
                    if (remainingLefts != null){
                        if (remainingLeftsInner.hasNext())
                            return new NamedValuesImpl(names, new NamedValues[]{remainingLeftsInner.next(), null});
                        if (remainingLefts.hasNext()){
                            final MultiMapBoolean.ListBoolPair<NamedValues> nextRemaining = remainingLefts.next();
                            remainingLeftsInner = nextRemaining.getValues().iterator();
                            continue;
                        }
                        return FqlIterator.sentinel;

                    }
                    if (leftValues == null || leftIx >= leftValues.size())
                    {
                        rightValue = precedent.next();
                        if (rightValue == FqlIterator.sentinel) {   // TODO check if we need to return more e.g. when we have an outer join
                            if (outerRight){
                                remainingLefts = leftHash.values().iterator();
                                continue;
                            }
                            else
                                return FqlIterator.sentinel;
                        }
                        try
                        {
                            env.pushObject(rightValue);
                            final Object value;
                            if (leftRight)
                                value = equalsNode.operand.getValue(env, rightValue);
                            else
                                value = equalsNode.left.getValue(env, rightValue);
                            leftIx = 0;
                            final MultiMapBoolean.ListBoolPair<NamedValues> listBoolPair = leftHash.get(value);
                            leftValues = listBoolPair.getValues();
                            listBoolPair.setSeen(true);
                            if (leftValues == null) {                                // not found
                                if (outerLeft)
                                    return new NamedValuesImpl(names, new NamedValues[]{null, rightValue});
                                else
                                    continue;
                            }

                        }
                        finally
                        {
                            env.popObject();
                        }
                    }
                    NamedValues ret = new NamedValuesImpl(names, new NamedValues[]{leftValues.get(leftIx), rightValue});
                    leftIx++;
                    return ret;
                }
            }
        };

    }

    private MultiMapBoolean<Object, NamedValues> getLeftvaluesAsMultiMap(RunEnv env, boolean leftRight, EqualsNode equalsNode)
    {
        FunqlConnection funqlConnection = env.getConnection(connectionSlot.getIndex());
        FqlIterator listContainer = funqlConnection.getIterator(containerName);
        final MultiMapBoolean<Object, NamedValues> leftHash = new MultiMapBoolean<>();
        for (; ; )
        {
            final NamedValues next = listContainer.next();
            if (next == FqlIterator.sentinel) break;
            try
            {
                env.pushObject(next);
                final Object value;
                if (leftRight)
                    value = equalsNode.left.getValue(env, next);
                else
                    value = equalsNode.operand.getValue(env, next);
                leftHash.add(value, next);
            }
            finally
            {
                env.popObject();
            }
        }
        return leftHash;
    }


}
