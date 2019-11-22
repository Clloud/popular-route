package hmm.reader;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DataReader {
    // 测试文件根目录
    public String filePath = "./data/map-matching-test/1";

    // 测试文件名
    public String fileName;

    /**
     * 将字符串表示的时间转换成Date对象
     *
     * @param time 表示时间的字符串hh:mm:ss
     * @return Date
     */
    public static Date seconds(String time) {
        String t[] = time.split(":");
        int second = Integer.parseInt(t[0]) * 3600 + Integer.parseInt(t[1]) * 60 + Integer.parseInt(t[2]);
        Calendar c = new GregorianCalendar(2009, 0, 17);
        c.add(Calendar.SECOND, second);
        return c.getTime();
    }
}
