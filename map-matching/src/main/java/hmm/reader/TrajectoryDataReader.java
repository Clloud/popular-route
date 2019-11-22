package hmm.reader;

import hmm.types.GpsMeasurement;
import hmm.types.Point;

import java.io.*;
import java.util.*;

public class TrajectoryDataReader extends GpsDataReader {

    public TrajectoryDataReader(String filePath, String fileName) {
        super(filePath, fileName);
    }

    public TrajectoryDataReader(String fileName) {
        super(fileName);
    }

    /**
     * 获取文件中的GPS轨迹数据
     */
    public List<GpsMeasurement> getData() throws IOException {
        File file = new File(filePath, fileName);
        BufferedReader in = new BufferedReader(new FileReader(file));
        String s;
        int lines = 0;

        while ((s = in.readLine()) != null) {
            // 前6行是无效数据，跳过
            lines++;
            if (lines <= 6) continue;
            /* 每行有效轨迹数据的格式为:
                Field 1: Latitude
                Field 2: Longitude
                Field 3: 0
                Field 4: Altitude in feet (-777 if not valid)
                Field 5: Date - number of days (with fractional part) that have passed since 12/30/1899
                Field 6: Date(UTC)
                Field 7: Time(UTC)
            */
            String t[] = s.split(",");
            Point point = new Point(Double.parseDouble(t[1]), Double.parseDouble(t[0]));
            GpsMeasurement gpsMeasurement = new GpsMeasurement(seconds(t[6]), point);
            if (filter(point)) {
                gpsMeasurements.add(gpsMeasurement);
            }
        }
        return gpsMeasurements;
    }
}
