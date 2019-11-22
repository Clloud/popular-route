package hmm.types;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;

import java.util.*;

import static hmm.utils.Helper.*;

public class RoadEdgeIndex {

    public RTree<T1, Geometry> tree;

    public RoadEdgeIndex() {
        tree = RTree.star().create();
    }

    /**
     * Add new road edge to index.
     */
    public void add(RoadEdge roadEdge) {
        for (int i = 0; i < roadEdge.line.size() - 1; i++) {
            Point currentPoint = roadEdge.line.get(i);
            Point nextPoint = roadEdge.line.get(i + 1);
            tree = tree.add(new T1(roadEdge, i), Geometries.line(
                    currentPoint.longitude,
                    currentPoint.latitude,
                    nextPoint.longitude,
                    nextPoint.latitude
            ));
        }
    }

    /**
     * Returns geometry entries within the given radius [m].
     */
    public Collection<RoadPosition> search(GpsMeasurement gpsMeasurement, double radius) {
        Point point = gpsMeasurement.position;
        final double deltaLon = distanceToLongitude(point, radius);
        final double deltaLat = distanceToLatitude(radius);
        // get road edge within the range
        List<Entry<T1, Geometry>> entries =
                tree.search(Geometries.rectangle(
                        point.longitude - deltaLon,
                        point.latitude - deltaLat,
                        point.longitude + deltaLon,
                        point.latitude + deltaLat))
                        .toList().toBlocking().first();
        return getRoadPosition(entries, point);
    }

    private Collection<RoadPosition> getRoadPosition(List<Entry<T1, Geometry>> entries, Point point) {
        Collection result = new ArrayList();
        HashMap<Long, T2> map = new HashMap<>();

        for (Entry<T1, Geometry> entry : entries) {
            if (!map.containsKey(entry.value().roadEdge.edgeId))
                map.put(entry.value().roadEdge.edgeId, new T2(entry.value().roadEdge));
            map.get(entry.value().roadEdge.edgeId).add(entry.geometry(), entry.value().lineIndex);
        }

        // traverse the map
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            RoadEdge roadEdge = ((T2) entry.getValue()).roadEdge;
            List line = roadEdge.line;
            List<T3> candidates = ((T2) entry.getValue()).candidates;
            double minDistance = Double.MAX_VALUE;
            Point candidatePoint = new Point(999, 999);
            int candidateIndex = -1;
            double fraction = -1;

            // find foot point on the candidate line segment
            for (int i = 0; i < candidates.size(); i++) {
                int index = (int) candidates.get(i).lineIndex;
                Point point2 = (Point) line.get(index);
                Point point3 = (Point) line.get(index + 1);
                if (!hasFootPoint(point, point2, point3))
                    continue;
                Point footPoint = findFootPoint(point, point2, point3);
                double distance = computeDistance(point, footPoint);
                if (distance < minDistance) {
                    candidatePoint = footPoint;
                    minDistance = distance;
                    candidateIndex = index;
                    double d = computeDistance(candidatePoint, point2);
                    fraction = (roadEdge.roadLengthArr.get(candidateIndex) + d)
                            / roadEdge.roadLength;
                }
                // find valid candidate point
                if (candidatePoint.longitude != 999) {
                    result.add(new RoadPosition(roadEdge.edgeId, candidateIndex, fraction,
                            candidatePoint));
                }
            }
        }
        return result;
    }
}

class T1 {
    public RoadEdge roadEdge;
    public long lineIndex;

    public T1(RoadEdge re, long index) {
        roadEdge = re;
        lineIndex = index;
    }
}

class T2 {
    public RoadEdge roadEdge;
    public List<T3> candidates = new ArrayList();

    public T2(RoadEdge re) {
        roadEdge = re;
    }

    public void add(Geometry g, long index) {
        candidates.add(new T3(g, index));
    }
}

class T3 {
    Geometry geometry;
    long lineIndex;

    public T3(Geometry g, long index) {
        geometry = g;
        lineIndex = index;
    }
}
