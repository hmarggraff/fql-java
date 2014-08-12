package org.funql.ri.exec;

import org.funql.ri.util.NamedIndex;
import org.jetbrains.annotations.NotNull;

/** Object to find an entry point in the environment.
 * the base index is the connection and the entry point index, the entry point in the connection
 */
public class ContainerSlot extends NamedIndex {
    @NotNull
    private final String containerName;
    @NotNull
    final int containerIndex;

    public ContainerSlot(String connectionName, int connectionSlot, String containerName, int containerIndex) {
        super(connectionName, connectionSlot);
        this.containerName = containerName;
        this.containerIndex = containerIndex;
    }

    public ContainerSlot(@NotNull NamedIndex connectionSlot, String containerName, int iteratorCount) {
        this(connectionSlot.getName(), connectionSlot.index, containerName, iteratorCount);
    }

    @NotNull public int getContainerIndex() {
        return containerIndex;
    }

    @NotNull public String getContainerName() {
        return containerName;
    }
}
