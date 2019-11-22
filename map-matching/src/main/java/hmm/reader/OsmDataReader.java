package hmm.reader;

import hmm.types.Point;
import hmm.types.RoadEdge;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

public class OsmDataReader extends DataReader {

    public Document document;

    public Map<Long, Point> pointMap = new HashMap<>();

    public List<RoadEdge> roadEdges = new ArrayList<>();

    public OsmDataReader(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public OsmDataReader(String fileName) {
        this.fileName = fileName;
    }

    public List<RoadEdge> getData() {
        SAXReader reader = new SAXReader();
        try {
            document = reader.read(new File(filePath, fileName));
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        /*
         * *.osm file example:
         * <?xml version="1.0" encoding="UTF-8"?>
         * <osm>
         *     <node id="1239221256" lat="32.4523921" lon="119.8949583"/>
         *     ...
         *     <way id="107904583">
         *         <nd ref="1239221278"/>
         *         <nd ref="1239221279"/>
         *         ...
         *         <tag k="highway" v="secondary"/>
         *     </way>
         *     ...
         * </osm>
         */

        // traverse osm document tree
        Element osm = document.getRootElement();
        Iterator it = osm.elementIterator();
        while (it.hasNext()) {
            Element node = (Element) it.next();
            switch (node.getName()) {
                case "node":
                    addNode(node);
                    break;
                case "way":
                    if (isHighway(node)) addWay(node);
                    break;
            }
        }

        return roadEdges;
    }

    private void addNode(Element node) {
        long id = Long.parseLong(node.attributeValue("id"));
        double longitude = Double.parseDouble(node.attributeValue("lon"));
        double latitude = Double.parseDouble(node.attributeValue("lat"));
        pointMap.put(id, new Point(longitude, latitude));
    }

    private void addWay(Element node) {
        long edgeId = Long.parseLong(node.attributeValue("id"));

        Iterator it = node.elementIterator();
        List<Point> line = new ArrayList<>();
        List<Long> nodeIds = new ArrayList<>();

        while (it.hasNext()) {
            Element nd = (Element) it.next();
            if (!nd.getName().equals("nd")) continue;
            long nodeId = Long.parseLong(nd.attributeValue("ref"));
            nodeIds.add(nodeId);
            // avoid NullPointerException
            Point point = pointMap.get(nodeId);
            if (point != null) line.add(point);
        }

        roadEdges.add(new RoadEdge(edgeId, nodeIds.get(0), nodeIds.get(nodeIds.size() - 1),
                true, line));
    }

    private boolean isHighway(Element node) {
        List<Element> tags = node.elements("tag");
        for (Element tag : tags) {
            String s = tag.attributeValue("k");
            if (s.equals("highway")) return true;
        }
        return false;
    }
}
