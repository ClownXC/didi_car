package com.cartravel.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 地图相关操作工具方法
 */
public class MapUtil {
    private final static Logger logger = LoggerFactory.getLogger(MapUtil.class);

    //定义高德地图开发者key(前提需要在高德地图开发者中心注册成为开发者，然后申请开发者key)
    private static String key = "ba1539fb52a33d97d645b9692d5fa4b8";

    /**
     * 阿里云api 根据经纬度获取地址
     *
     * @param lng
     * @param lat
     * @return
     */
    public static String getAdd(String lng, String lat) {
        StringBuffer s = new StringBuffer();
        s.append("key=").append(key).append("&location=").append(lng).append(",").append(lat);
        String res = sendPost("http://restapi.amap.com/v3/geocode/regeo", s.toString());
        logger.info(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONObject jsonObject1 = jsonObject.getJSONObject("regeocode");
        String add = jsonObject1.get("formatted_address").toString();
        return add;
    }

    /**
     * 获取城市县区
     *
     * @param districtName
     * @return
     */
    public static JSONArray getDistricts(String districtName, String districtCode) {
        StringBuffer s = new StringBuffer();
        s.append("key=").append(key).append("&location=").append("&keywords=" + districtName).append("&subdistrict=1")
                .append("&extensions=all");
        String res = sendGet("https://restapi.amap.com/v3/config/district", s.toString());
        logger.info(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        String count = jsonObject.getString("count");

        return jsonObject.getJSONArray("districts");
    }

    public static void parseDistrictInfo(JSONArray districts, String targeCitycode, List<District> districtList) {
        if (null != districts && (!districts.isEmpty())) {

            Iterator<Object> iterator = districts.iterator();
            while (iterator.hasNext()) {
                JSONObject next = (JSONObject) (iterator.next());

                String name = next.getString("name");
                String citycode = next.getString("citycode");
                String polyline = next.getString("polyline");
                String level = next.getString("level");
                logger.warn("name:" + name);

                logger.warn("polyline:" + polyline);
                logger.warn("citycode:" + citycode);

                if (level.equals("city")) {//如果级别是城市,继续进行区/县解析
                    JSONArray district = next.getJSONArray("districts");
                    parseDistrictInfo(district, null,districtList);
                } else if (level.equals("district")) {
                    if (null != targeCitycode && targeCitycode.equals(citycode)) {
                        districtList.add(new District(citycode,name,polyline));
                        return;
                    }

                    if (null == polyline) {
                        JSONArray cityDistrict = getDistricts(name, null);
                        parseDistrictInfo(cityDistrict, citycode,districtList);
                    }
                    continue;
                }
            }
        }
    }

    /**
     * 阿里云api 根据经纬度获取所在城市
     *
     * @param log
     * @param lat
     * @return
     */
    public static String getCity(String log, String lat) {
        // log 大 lat 小
        // 参数解释: 纬度,经度 type 001 (100代表道路，010代表POI，001代表门址，111可以同时显示前三项)
        String urlString = "http://gc.ditu.aliyun.com/regeocoding?l=" + lat + "," + log + "&type=010";
        String res = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream
                    (), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                res += line + "\n";
            }
            in.close();
            JSONObject jsonObject = JSONObject.parseObject(res);
            JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("addrList"));
            JSONObject j_2 = JSONObject.parseObject(jsonArray.get(0).toString());
            String allAdd = j_2.getString("admName");
            String arr[] = allAdd.split(",");
            res = arr[1];
        } catch (Exception e) {
            logger.info("error in wapaction,and e is " + e.getMessage());
        }
        logger.info(res);
        return res;
    }

    /**
     * 高德api 根据地址获取经纬度
     *
     * @param name
     * @return
     */
    public static String getLatAndLogByName(String name) {
        StringBuffer s = new StringBuffer();
        s.append("key=" + key + "&address=" + name);
        String res = sendPost("http://restapi.amap.com/v3/geocode/geo", s.toString());
        logger.info(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONArray jsonArray = jsonObject.getJSONArray("geocodes");
        JSONObject location = (JSONObject) jsonArray.get(0);
        String add = location.get("location").toString();
        return add;
    }

    /**
     * 高德api 根据地址获取经纬度
     *
     * @return
     */
    public static String getAddByAMAP(String log, String lat) {
        StringBuffer s = new StringBuffer();
        s.append("key=").append(key).append("&location=").append(log).append(",").append(lat);
        String res = sendPost("http://restapi.amap.com/v3/geocode/regeo", s.toString());
        logger.info(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONObject jsonObject1 = jsonObject.getJSONObject("regeocode");
        String add = jsonObject1.get("formatted_address").toString();
        return add;
    }


    /**
     * 高德api 坐标转换---转换至高德经纬度
     *
     * @param lng
     * @param lat
     * @param type
     * @return
     */
    public static String convertLocations(String lng, String lat, String type) {
        StringBuffer s = new StringBuffer();
        s.append("key=").append(key).append("&locations=").append(lng).append(",").append(lat).append("&coordsys=");
        if (type == null) {
            s.append("gps");
        } else {
            s.append(type);
        }
        String res = sendPost("http://restapi.amap.com/v3/assistant/coordinate/convert", s.toString());
        logger.info(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        String add = jsonObject.get("locations").toString();
        return add;
    }


    public static String getAddByName(String name) {
        // log 大 lat 小
        // 参数解释: 纬度,经度 type 001 (100代表道路，010代表POI，001代表门址，111可以同时显示前三项)
        String urlString = "http://gc.ditu.aliyun.com/geocoding?a=" + name;
        String res = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                res += line + "\n";
            }
            in.close();
            JSONObject jsonObject = JSONObject.parseObject(res);
            String lon = jsonObject.getString("lon");
            String lat = jsonObject.getString("lat");
            System.err.println(jsonObject);
            res = getNearbyAdd(lon, lat);
        } catch (Exception e) {
            logger.info("error in wapaction,and e is " + e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    public static String getNearbyAdd(String log, String lat) {
        String add = sendGet("http://ditu.amap.com/service/regeo", "longitude=" + log + "&latitude=" + lat +
                "&type=010");
        logger.info(add);
        return add;
    }

    /**
     * 高德api 关键字模糊查询
     *
     * @param keyWord
     * @param city
     * @return
     */
    public static String getKeywordsAddByLbs(String keyWord, String city) {
        StringBuffer s = new StringBuffer();
        s.append("key=" + key + "&keywords=");
        if (keyWord.contains(" ")) {
            String[] str = keyWord.split(" ");
            for (int i = 0; i < str.length; i++) {
                if (i == 0) {
                    s.append(str[i]);
                } else {
                    s.append("+" + str[i]);
                }
            }
        } else {
            s.append(keyWord);
        }
        s.append("&city=" + city);
        s.append("offset=10&page=1");
        String around = sendPost("http://restapi.amap.com/v3/place/text", s.toString());
        logger.info(around);
        return around;
    }

    /**
     * 高德api 经纬度/关键字 附近地标建筑及地点查询
     *
     * @param log
     * @param lat
     * @param keyWord
     * @return
     */
    public static String getAroundAddByLbs(String log, String lat, String keyWord) {
        String around = sendPost("http://restapi.amap.com/v3/place/around",
                "key=" + key + "&location=" + log + "," + lat + "&keywords=" + keyWord +
                        "&radius=2000&offset=10&page=1");
        logger.info(around);
        return around;
    }

    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                logger.info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * GET请求数据
     *
     * @param get_url url地址
     * @param content key=value形式
     * @return 返回结果
     * @throws Exception
     */
    public static String sendGetData(String get_url, String content) throws Exception {
        String result = "";
        URL getUrl = null;
        BufferedReader reader = null;
        String lines = "";
        HttpURLConnection connection = null;
        try {
            if (content != null && !content.equals(""))
                get_url = get_url + "?" + content;
            // get_url = get_url + "?" + URLEncoder.encode(content, "utf-8");
            getUrl = new URL(get_url);
            connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();
            // 取得输入流，并使用Reader读取
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码
            while ((lines = reader.readLine()) != null) {
                result = result + lines;
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
                reader = null;
            }
            connection.disconnect();
        }
    }

    /**
     * @param POST_URL url地址
     * @param content  key=value形式
     * @return 返回结果
     * @throws Exception
     */
    public static String sendPostData(String POST_URL, String content) throws Exception {
        HttpURLConnection connection = null;
        DataOutputStream out = null;
        BufferedReader reader = null;
        String line = "";
        String result = "";
        try {
            URL postUrl = new URL(POST_URL);
            connection = (HttpURLConnection) postUrl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            // Post 请求不能使用缓存
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();

            out = new DataOutputStream(connection.getOutputStream());
            // content = URLEncoder.encode(content, "utf-8");
            // DataOutputStream.writeBytes将字符串中的16位的unicode字符�?8位的字符形式写道流里�?
            out.writeBytes(content);
            out.flush();
            out.close();
            // 获取结果
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码
            while ((line = reader.readLine()) != null) {
                result = result + line;
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) {
                out.close();
                out = null;
            }
            if (reader != null) {
                reader.close();
                reader = null;
            }
            connection.disconnect();
        }
    }

    /*
     * 过滤掉html里不安全的标签，不允许用户输入这些标�?
     */
    public static String htmlFilter(String inputString) {
        // return inputString;
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        Pattern p_script;
        java.util.regex.Matcher m_script;

        try {
            String regEx_script = "<[\\s]*?(script|style)[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?(script|style)[\\s]*?>";
            String regEx_onevent = "on[^\\s]+=\\s*";
            String regEx_hrefjs = "href=javascript:";
            String regEx_iframe = "<[\\s]*?(iframe|frameset)[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?(iframe|frameset)" +
                    "[\\s]*?>";
            String regEx_link = "<[\\s]*?link[^>]*?/>";

            htmlStr = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
            htmlStr = Pattern.compile(regEx_onevent, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
            htmlStr = Pattern.compile(regEx_hrefjs, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
            htmlStr = Pattern.compile(regEx_iframe, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
            htmlStr = Pattern.compile(regEx_link, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");

            textStr = htmlStr;

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;
    }

    //https://restapi.amap.com/v3/config/district?parameters
    public static void main(String[] args) {
        List<District> districtList = new ArrayList<>();
        JSONArray districts = getDistricts("海口市", null);
        parseDistrictInfo(districts, null,districtList);
        logger.warn(JSONObject.toJSONString(districtList));
    }
}


