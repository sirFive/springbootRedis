package com.xie.redis.springbootredis.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Component
public class RedisMapper {

    private static String oldTime = null;

    @Autowired
    JedisCluster jedisCluster;



    /**更据key值查找*/
    public Object getByKey(String key){

        String str=jedisCluster.get(key);

        return str;

    }

    /**判断key是否存在*/
    public boolean hasKey(String key)
    {
        return jedisCluster.exists(key);
    }

    /**根据key获取Value*/
    public String get(String key) {

        return jedisCluster.get(key);
    }


    /**
     * 设置缓存，并且自己指定过期时间
     * @param key
     * @param value
     * @param expireTime 过期时间
     */
    public void setWithExpireTime( String key, String value, int expireTime) {
        jedisCluster.set(key,value);
        jedisCluster.pexpire(key,expireTime);
    }



    /**
     * 删除指定key的缓存
     * @param key
     */
    public void delete(String key) {
        long o =jedisCluster.del(key);
        System.out.println(o+"...");
    }


    /**
     * 删除所有key的缓存
     *
     */
    public void deleteALl(String key) {

        Set<String> set = jedisCluster.hkeys(key);
        System.out.println("删除的key length " + set.size());
        for (String s : set) {
            System.out.println("删除的key" + key);
            jedisCluster.del(s);
        }
    }




    /**
     * 频率控制方法1  100ms调用一次
     *
     */
    public Boolean freControlOne() {

        /**reids取值*/
        String value=get("getTime_"+100+"ms");
        System.out.println("频率控制方法1  100ms调用一次 redis取值："+value);
        if (value == null){
            /**表明Value已经过期 也就是时间间隔超过100ms 可以在调用 再次设置值*/
            setWithExpireTime("getTime_"+100+"ms","100ms",100);
            return true;
        }

        /**Value没有过期 还不能进行调用*/
        return false;

    }



    /**
     * 频率控制方法2  一天调用10次
     *
     */
    public Boolean freControlTwo() {

        String  newTime=new SimpleDateFormat("yyyy-MM-dd").format(new Date());


        //没有计数前得到的是Value 存进去的Value值
        String value=get("getTime_"+newTime);
        /**已经过了一天*/
        if (!newTime.equalsIgnoreCase(oldTime)){
            jedisCluster.del("getTime_"+oldTime);
        }

        if (value == null){
            /**第一调用 还未超过10次*/
            /**每次访问接口增加 1 */
            oldTime=newTime;
            /**value初始值是 1 以后的每次访问 value都会加 1  */
            jedisCluster.set("getTime_"+newTime,"1");

            return true;
        }

        String redisValue=jedisCluster.get("getTime_"+newTime);
        if (Long.parseLong(redisValue) >=  10){
            /**超出一天的 接口调用限度*/
            return false;
        }

        /**增加1 */
        jedisCluster.incrBy("getTime_"+newTime,1);

        return true;

    }



}
