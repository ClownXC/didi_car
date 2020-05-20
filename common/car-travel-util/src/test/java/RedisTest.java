import com.cartravel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisTest {
    public static void main(String[] args) {
        JedisPool pool = JedisUtil.getPool();
        Jedis resource = pool.getResource();
        System.out.println("resource:"+resource);
    }

}
