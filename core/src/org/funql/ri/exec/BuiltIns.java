package org.funql.ri.exec;

import org.funql.ri.data.FqlDataException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 */
public enum BuiltIns implements FqlBuiltinFunction, Comparable<BuiltIns> {
    //TODO function ref as method on a value i.e. a.b.ref(container) or a.b.ref(conainer, joinfield)
    //TODO method fields to return iterator over fields

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
                return env.outerObjects.peek();
            else throw new IllegalArgumentException(badArgs(argvals, name()));
        }


    },
    up {
        @Override
        public Object val(RunEnv env, Object from, Object[] argvals) throws FqlDataException {
            if (env.outerObjects.size() <= 1)
                throw new IllegalStateException("Function up can only be used in a nested query.");
            if (argvals.length == 0)
                return env.outerObjects.get(env.outerObjects.size()-2);
            else if (argvals.length == 1 && argvals[0] instanceof Number){
                int level = ((Number)argvals[0]).intValue();
                if (level < 0)
                    throw new IllegalArgumentException("function it: nesting must be > 0");
                if (level >= env.outerObjects.size()-1)
                    throw new IllegalArgumentException("function it: nesting " + env.outerObjects.size() + " is less than argument " + level);
                return env.outerObjects.get(env.outerObjects.size()-level-1);
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

    static Map<String, BuiltIns> parameterless = new HashMap<>();
    static Map<String, BuiltIns> allFunctions = new HashMap<>();
    static {
        parameterless.put(it.name(), it);
        parameterless.put(up.name(), up);
        for (BuiltIns b : BuiltIns.values()) {

            parameterless.put(b.name(), b);
        }
    }

    public static BuiltIns get(String nam){
        return allFunctions.get(nam);
    }

    public static BuiltIns getParameterless(String nam){
        return parameterless.get(nam);
    }

}
