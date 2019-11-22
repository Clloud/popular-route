package hmm.reader;

import hmm.types.GpsMeasurement;
import hmm.types.Point;

import static hmm.utils.Helper.computeDistance;

import java.io.*;
import java.util.*;

public class GpsDataReader extends DataReader {

    public List<GpsMeasurement> gpsMeasurements = new ArrayList<>();

    public double σ = 4.07;

    public GpsDataReader(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public GpsDataReader(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取文件中的GPS轨迹数据
     */
    public List<GpsMeasurement> getData() throws IOException {
        File file = new File(filePath, fileName);
        BufferedReader in = new BufferedReader(new FileReader(file));
        // 跳过第一行数据
        String s = in.readLine();

        while ((s = in.readLine()) != null) {
            // 每行轨迹数据的格式为:
            // Date (UTC)	Time (UTC)	Latitude	Longitude
            String t[] = s.split("\\t");
            Point point = new Point(Double.parseDouble(t[3]), Double.parseDouble(t[2]));
            GpsMeasurement gpsMeasurement = new GpsMeasurement(seconds(t[1]), point);
            if (filter(point)) {
                gpsMeasurements.add(gpsMeasurement);
            }
        }
        return gpsMeasurements;
    }

    /**
     * 获取指定数量的轨迹点
     */
    public List<GpsMeasurement> getData(int count) throws IOException {
        getData();
        if (count > gpsMeasurements.size()) return gpsMeasurements;
        return gpsMeasurements.subList(0, count);
    }

    /**
     * 过滤掉与前一个轨迹点间距小于2σ的点
     */
    protected boolean filter(Point point) {
        if (gpsMeasurements.size() == 0 ||
                computeDistance(point, gpsMeasurements.get(gpsMeasurements.size() - 1).position) >= 2 * σ)
            return true;
        return false;
    }
}
