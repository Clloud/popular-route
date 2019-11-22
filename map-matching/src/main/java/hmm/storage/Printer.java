package hmm.storage;

import com.bmw.hmm.SequenceState;
import hmm.types.RoadPosition;

import java.io.*;
import java.util.*;

public class Printer {

    /**
     * 将地图匹配结果保存到文件
     *
     * @param result
     * @param fileName
     * @throws IOException
     */
    public void save(List<SequenceState> result, String filePath,
                     String fileName) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(filePath, fileName)));

        out.write("EdgeId\tLineIndex\tFraction\tLongitude\tLatitude\r\n");
        for (SequenceState ss : result) {
            RoadPosition state = (RoadPosition) ss.state;
            String text = String.format("%d\t%d\t%.14f\t%.14f\t%.14f\r\n",
                    state.edgeId,
                    state.lineIndex,
                    state.fraction,
                    state.position.longitude,
                    state.position.latitude);
            out.write(text);
        }
        out.close();
    }
}
