package org.funql.ri.exec.node;

import org.funql.ri.data.*;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;

import java.util.ArrayList;

public class RefClause implements FqlStatement
{

    private final ArrayList<String> targetPath;
    private final EntryPointSlot entryPoint;
    private final ArrayList<String> fieldpath;

    public RefClause(ArrayList<String> targetPath, EntryPointSlot entryPoint, ArrayList<String> fieldpath)
    {
	this.targetPath = targetPath;
	this.entryPoint = entryPoint;
	this.fieldpath = fieldpath;
    }

    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException
    {
	FunqlConnection funqlConnection = env.getConnection(entryPoint.getIndex());
	FqlMapContainer mapContainer = funqlConnection.useMap(fieldpath);

	env.setMapContainer(entryPoint.getEntryPointIndex(), mapContainer);
	return null;
    }
}
