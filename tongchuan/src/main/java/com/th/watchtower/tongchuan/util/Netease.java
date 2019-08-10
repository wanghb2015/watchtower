package com.th.watchtower.tongchuan.util;

import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 王虹波
 * @since 1.0, 2019/8/10 19:29
 */
public class Netease {
    public Netease(JSONArray args) {
        try {
            send(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
    //发送验证码的请求路径URL
    private static final String SERVER_URL="https://api.netease.im/sms/sendtemplate.action";
    //网易云信分配的账号，请替换你在管理后台应用下申请的Appkey
    private static final String APP_KEY="e065f9d63ba813e6505b5b3749f9afba";
    //网易云信分配的密钥，请替换你在管理后台应用下申请的appSecret
    private static final String APP_SECRET="4338bc9d8ec3";
    //随机数
    private static final String NONCE="123456";
    //短信模板ID
    private static final String TEMPLATEID="14798533";
    //手机号，接收者号码列表，JSONArray格式，限制接收者号码个数最多为100个
    private static final String MOBILES="['15534448098','18234108735']";
    //短信参数列表，用于依次填充模板，JSONArray格式，每个变量长度不能超过30字,对于不包含变量的模板，不填此参数表示模板即短信全文内容

    public void send(JSONArray args) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVER_URL);
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        /*
         * 参考计算CheckSum的java代码，在上述文档的参数列表中，有CheckSum的计算文档示例
         */
        String checkSum = CheckSumBuilder.getCheckSum(APP_SECRET, NONCE, curTime);

        // 设置请求的header
        httpPost.addHeader("AppKey", APP_KEY);
        httpPost.addHeader("Nonce", NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的的参数，requestBody参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        /*
         * 1.如果是模板短信，请注意参数mobile是有s的，详细参数配置请参考“发送模板短信文档”
         * 2.参数格式是jsonArray的格式，例如 "['13888888888','13666666666']"
         * 3.params是根据你模板里面有几个参数，那里面的参数也是jsonArray格式
         */
        nvps.add(new BasicNameValuePair("templateid", TEMPLATEID));
        nvps.add(new BasicNameValuePair("mobiles", MOBILES));
        System.out.println(args.toJSONString());
        nvps.add(new BasicNameValuePair("params", args.toJSONString()));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);
        /*
         * 1.打印执行结果，打印结果一般会200、315、403、404、413、414、500
         * 2.具体的code有问题的可以参考官网的Code状态表
         */
        System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));

    }

    static class CheckSumBuilder {

        //计算并获取checkSum
        public static String getCheckSum(String appSecret,String nonce,String curTime){
            return encode("SHA",appSecret+nonce+curTime);
        }

        private static String encode(String algorithm,String value){
            if(value == null){
                return null;
            }
            try {
                MessageDigest messageDigest=MessageDigest.getInstance(algorithm);
                messageDigest.update(value.getBytes());
                return getFormattedText(messageDigest.digest());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static String getFormattedText(byte[] bytes){
            int len=bytes.length;
            StringBuilder sb=new StringBuilder(len*2);
            for(int $i=0;$i<len;$i++){
                sb.append(HEX_DIGITS[(bytes[$i]>>4)&0x0f]);
                sb.append(HEX_DIGITS[bytes[$i]&0x0f]);
            }
            return sb.toString();
        }
        private static final char[] HEX_DIGITS={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    }
}
