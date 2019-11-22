package hmm;

import hmm.reader.GpsDataReader;
import hmm.reader.RoadDataReader;
import hmm.reader.TrajectoryDataReader;
import hmm.utils.OfflineMapMatching;
import hmm.storage.Printer;
import java.io.*;
import java.util.*;

public class MapMatching {
    /**
     * 地图匹配功能测试
     * 测试文件默认路径为./data/map-matching-test/1 可以在reader/DataReader中修改
     */
    public static void mapMatchingTest() throws IOException{
        // 读取地图数据
        List roadEdges = new RoadDataReader("road_network.txt").getData();

        // 读取GPS轨迹数据
        List gpsMeasurements = new GpsDataReader("gps_data.txt").getData(60);

        // 进行地图匹配
        OfflineMapMatching mm = new OfflineMapMatching(
                gpsMeasurements,
                roadEdges,
                30);
        List result = mm.run();

        // 输出匹配结果
        Printer printer = new Printer();
        printer.save(result, "./data", "result.txt");
    }

    /**
     * 对北京地区的轨迹数据进行地图匹配
     */
    public static void trajectoryMapMatching() throws IOException{
        // 加载北京的地图数据
        List roadEdges = new RoadDataReader(
                "./data/map-data",
                "road_network_beijing.txt").getData();

        // 读取轨迹数据
        List gpsMeasurements = new TrajectoryDataReader(
                "./data/trajectory/000/Trajectory/",
                "20081023025304.plt").getData(60);

        // 进行地图匹配
        OfflineMapMatching mm = new OfflineMapMatching(
                gpsMeasurements,
                roadEdges,
                15);
        List result = mm.run();

        // 输出匹配结果
        Printer printer = new Printer();
        printer.save(result,
                "./data/trajectory-map-matched",
                "20081023025304.txt");
    }

    public static void main(String args[]) throws IOException {
//        mapMatchingTest();
        trajectoryMapMatching();
    }
}
