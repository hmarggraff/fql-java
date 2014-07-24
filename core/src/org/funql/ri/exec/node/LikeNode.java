package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

/**
 */
public class LikeNode extends BinaryNode
{
    public LikeNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftValue = left.getValue(env, from);
        Object rightValue = operand.getValue(env, from);
        if (leftValue == null)
        {
            return rightValue != null; //null < everything
        }
        else if (leftValue instanceof String && rightValue instanceof String)
        {
            String lv = (String) leftValue;
            String rv = (String) rightValue;
            return match(lv, rv, 0, 0);
        }
        throw fqlDataException("Comparing classes " + leftValue.getClass() + " with " + rightValue.getClass());
    }

    protected boolean match(final String source, String matcher, int sx, int px)
    {
        while (sx < source.length() && px < matcher.length())
        {
            char pc = matcher.charAt(px);
            if (pc == '*')
                return matchStar(source, matcher, sx, ++px);
            else if (pc == '\\')
            {
                px++;
                pc = matcher.charAt(px);
                if (pc == source.charAt(sx))
                {
                    px++;
                    sx++;
                }
                else
                    return false;
            }
            else if (pc == '?' || pc == source.charAt(sx))
            {
                px++;
                sx++;
            }
            else
                return false;
        }
        while (px < matcher.length() && matcher.charAt(px) == '*')
        {
            px++;
        }
        return sx >= source.length() && px >= matcher.length();
    }

    protected boolean matchStar(final String source, String matcher, int st, int px)
    {
        int sx = source.length();
        while (sx >= st && !match(source, matcher, sx, px))
        {
            sx--;
        }
        return sx >= st;
    }

    @Override
    public String getOperator() {
        return "like";
    }

}
