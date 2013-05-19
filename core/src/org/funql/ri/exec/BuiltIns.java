package org.funql.ri.exec;

import org.funql.ri.data.FqlDataException;

import java.util.Date;

/**
 */
public enum BuiltIns implements FqlBuiltinFunction, Comparable<BuiltIns> {
    date {
        @Override
        public Object val(RunEnv env, Object from, Object[] argvals) throws FqlDataException {
            if (argvals.length == 0)
                return new Date();
            else if (argvals.length == 1) {
                if (argvals[0] instanceof Number) return new Date(((Number)argvals[0]).longValue());
                else throw new IllegalArgumentException(badArgs(argvals, name()));
            }
            else if (argvals.length == 3) {
                if (argvals[0] instanceof Number && argvals[1] instanceof Number && argvals[2] instanceof Number) return new Date(((Number)argvals[0]).intValue(), ((Number)argvals[1]).intValue(), ((Number)argvals[2]).intValue());
                else throw new IllegalArgumentException(badArgs(argvals, name()));
            }
            else throw new IllegalArgumentException(badArgs(argvals, name()));
        }

    },
    it {
        @Override
        public Object val(RunEnv env, Object from, Object[] argvals) throws FqlDataException {
            if (argvals.length == 0)
                return env.iteratorStack.peek();
            else if (argvals.length == 1 && argvals[0] instanceof Number){
                int level = ((Number)argvals[0]).intValue();
                if (level > env.iteratorStack.size())
                    throw new IllegalArgumentException("function it: nesting " + env.iteratorStack.size() + " is less than argument " + level);
                return env.iteratorStack.get(env.iteratorStack.size()-level);
            }
            else throw new IllegalArgumentException(badArgs(argvals, name()));
        }


    };

    private static String badArgs(Object[] argvals, String name) {
        StringBuffer sb = new StringBuffer();
        sb.append("Built in function ").append(name).append( "cannot be called with argument types: (");
        for (int i = 0; i < argvals.length; i++) {
            Object argval = argvals[i];
            if (i > 0 ) sb.append(", ");
            sb.append(argval.getClass().getName());
        }
        sb.append(')');
        return sb.toString();
    }

    public String getName() {
        return name();
    }
}
