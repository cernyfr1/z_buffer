package objectdata.solids;

import transforms.Col;

public class Part {

    private final Topology topology;
    private final int offset;
    private final int count;
    private final Col color;

    public Part(Topology topology, int offset, int count) {
        this.topology = topology;
        this.offset = offset;
        this.count = count;
        this.color = null;
    }

    public Part(Topology topology, int offset, int count, Col color) {
        this.topology = topology;
        this.offset = offset;
        this.count = count;
        this.color = color;
    }

    public Topology getTopology() {
        return topology;
    }

    public int getOffset() {
        return offset;
    }

    public int getCount() {
        return count;
    }
    public Col getColor() {
        return color;
    }
}
