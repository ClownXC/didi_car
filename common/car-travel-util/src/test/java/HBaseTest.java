import com.cartravel.common.Constants;
import com.cartravel.common.TrackPoint;
import com.cartravel.util.HBaseUtil;
import org.apache.hadoop.hbase.client.Connection;

import java.util.List;

public class HBaseTest {

    public static void createTable(String tableName) throws Exception{
        Connection connection = HBaseUtil.getConnection();
        System.out.println(connection.isAborted());
        HBaseUtil.createTable(connection, tableName, Constants.DEFAULT_FAMILY);
        connection.close();
    }

    public static void queryTrackPoint(String tableName)throws Exception{
        List<TrackPoint> rest = HBaseUtil.getRest(tableName, "39a096b71376b82f35732eff6d95779b",
                "1477969147", "1477969976", TrackPoint.class);
        System.out.println(rest.size());
    }

    public static void main(String[] args) throws Exception {
        String tableName = "HTAB_GPS";
        createTable(tableName);
//        queryTrackPoint(tableName);
    }
}
