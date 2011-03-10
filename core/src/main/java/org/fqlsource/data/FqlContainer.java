package org.fqlsource.data;

import org.fqlsource.exec.RunEnv;
import org.fqlsource.util.Named;

public interface FqlContainer<ConnectionType extends FqlConnection> extends Named
{
    /**
     * the connection that this source belongs to.
     *
     * @return the owning connection
     */
    ConnectionType getConnection();

    Object getObject(RunEnv runEnv, Object from, String member);
}
