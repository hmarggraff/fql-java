package org.funql.ri.exec.clause;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.FqlMapContainer;
import org.funql.ri.data.FunqlConnection;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.EntryPointSlot;

import java.util.ArrayList;

public class RefClause implements FqlStatement {

    private final String targetName;
    private final EntryPointSlot entryPoint;
    private final ArrayList<String> fieldpath;
    private final boolean single;

    public RefClause(String targetName, EntryPointSlot entryPoint, ArrayList<String> fieldpath, boolean single) {
	this.targetName = targetName;
	this.entryPoint = entryPoint;
	this.fieldpath = fieldpath;
	this.single = single;
    }

    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException {
	FunqlConnection funqlConnection = env.getConnection(entryPoint.getIndex());
	FqlMapContainer mapContainer = funqlConnection.useMap(targetName, fieldpath, single);

	env.putMapContainer(entryPoint.getEntryPointIndex(), mapContainer);
	return null;
    }
}
