package org.fqlsource.data;

import org.fqlsource.exec.RunEnv;
import org.fqlsource.util.Named;

/**
 */
public interface FqlDataSource<ConnectionType extends DefaultFqlConnection> extends Iterable, Named
{
    ConnectionType getConnection();

    Object getObject(RunEnv runEnv, Object from, String member);
}
