package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.FunqlConnection;
import org.funql.ri.data.FunqlDriver;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.util.NamedIndex;

import java.util.HashMap;
import java.util.Map;

public class ConnectClause extends NamedIndex implements FqlStatement {
    protected final Map<String, String> config;
    private final int row;
    private final int col;


    public ConnectClause(String conn_name, int connectionIndex, HashMap<String, String> config, int row, int col) {
        super(conn_name, connectionIndex);
        this.config = config;
        this.row = row;
        this.col = col;
    }

    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException {
        final String driverClassName = config.get("driver");
        try {
            FunqlDriver driver = env.getDriver(driverClassName);
            final FunqlConnection conn = driver.openConnection(name, config);
            env.setConnectionAt(index, conn);
        } catch (Exception e) {
            throw new FqlDataException("Driver named " + driverClassName + " could not be loaded.", e, row, col);
        }
        return null;
    }
}
