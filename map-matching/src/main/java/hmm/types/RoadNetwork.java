package hmm.types;

import java.io.Serializable;
import java.util.*;

public class RoadNetwork implements Serializable {

    public Set<Long> nodes = new HashSet<>();
    private HashMap<Long, HashMap<Long, Edge>> edges = new HashMap<>();
    public HashMap<Long, Point> roadNodes = new HashMap<>();
    public HashMap<Long, RoadEdge> roadEdges = new HashMap<>();

    public class Node {
        public final long nodeId;
        public double cost;
        public Node parent;

        public Node(long nodeId, double cost) {
            this.nodeId = nodeId;
            this.cost = cost;
            this.parent = null;
        }

        public Node(long nodeId, double cost, Node parent) {
            this(nodeId, cost);
            this.parent = parent;
        }
    }

    public class Edge {
        public final long edgeId;
        public double weight;

        public Edge(long edgeId, double weight) {
            this.edgeId = edgeId;
            this.weight = weight;
        }
    }

    /**
     * Construct the road network through roadEdges.
     */
    public RoadNetwork(List<RoadEdge> roadEdges) {
        for (RoadEdge re : roadEdges) {
            this.roadEdges.put(re.edgeId, re);
            // add node
            nodes.add(re.fromNodeId);
            roadNodes.put(re.fromNodeId, re.line.get(0));
            nodes.add(re.toNodeId);
            roadNodes.put(re.toNodeId, re.line.get(re.line.size() - 1));
            // add edge
            Edge edge = new Edge(re.edgeId, re.roadLength);
            addEdge(re.fromNodeId, re.toNodeId, edge);
            if (re.twoWay) addEdge(re.toNodeId, re.fromNodeId, edge);
        }
    }

    private void addEdge(long fromNode, long toNode, Edge edge) {
        HashMap<Long, Edge> t = edges.get(fromNode);
        if (t == null)
            t = new HashMap<>();
        t.put(toNode, edge);
        edges.put(fromNode, t);
    }

    private double dijkstra(long fromNodeId, long toNodeId) {
        Comparator<Node> comparator = Comparator.comparingDouble(x -> x.cost);
        Queue<Node> pq = new PriorityQueue<>(comparator);
        Map<Long, Node> map = new HashMap<>();

        // initialize the priority queue
        for (long nodeId : nodes) {
            Node t = nodeId == fromNodeId ?
                    new Node(nodeId, 0) :
                    new Node(nodeId, Double.POSITIVE_INFINITY);
            pq.add(t);
            map.put(t.nodeId, t);
        }

        while (pq.size() > 0) {
            // extract node u with the minimum cost
            Node u = pq.poll();

            // find node u's neighbours
            List<Node> neighbours = new ArrayList<>();
            // handle NullPointerException
            HashMap<Long, Edge> e = edges.get(u.nodeId);
            if (e == null) break;
            for (Long t : e.keySet())
                neighbours.add(map.get(t));

            // refresh the cost
            for (Node v : neighbours) {
                double newCost = u.cost + getWeight(u, v);
                if (newCost < v.cost) {
                    pq.remove(v);
                    v.cost = newCost;
                    v.parent = u;
                    pq.add(v);
                }
            }

        }
        return map.get(toNodeId).cost;
    }

    private double getWeight(Node u, Node v) {
        return edges.get(u.nodeId).get(v.nodeId).weight;
    }

    public double computePathDistance(RoadPosition from, RoadPosition to) {
        // on the same road edge
        if (from.edgeId == to.edgeId)
            return (from.fraction - to.fraction) * roadEdges.get(from.edgeId).roadLength;

        // on different road edges
        Set<Long> nodesCopy = nodes;
        HashMap<Long, HashMap<Long, Edge>> edgesCopy = edges;

        // add two virtual nodes
        addVirtualNode(from, -1);
        addVirtualNode(to, -2);
        double result = dijkstra(-1, -2);

        nodes = nodesCopy;
        edges = edgesCopy;

        return result;
    }

    private void addVirtualNode(RoadPosition rp, long nodeId) {
        RoadEdge re = roadEdges.get(rp.edgeId);
        long s = re.fromNodeId;
        long t = re.toNodeId;
        Edge e1 = new Edge(0, re.roadLength * rp.fraction);
        Edge e2 = new Edge(-1, re.roadLength * (1 - rp.fraction));

        nodes.add(nodeId);
        addEdge(s, nodeId, e1);
        addEdge(nodeId, t, e2);
        if (re.twoWay) {
            addEdge(nodeId, s, e1);
            addEdge(t, nodeId, e2);
        }
    }
}
