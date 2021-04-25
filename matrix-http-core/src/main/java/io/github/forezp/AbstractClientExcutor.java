package io.github.forezp;


import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 参考https://blog.csdn.net/zzq900503/article/details/89711999
 */
public abstract class AbstractClientExcutor {

    abstract void handeleRequest(HttpUriRequest httpRequest, Map<String, String> headers, ResonseCallBack ResonseCallBack);

    public void postJson(String url, Object params, ResonseCallBack ResonseCallBack) {
        this.postJson(url, params, null, ResonseCallBack);
    }

    public void postJson(String url, Object params, Map<String, String> headers, ResonseCallBack ResonseCallBack) {
        url = CommonUtils.decorateUrl(url);
        String value = SerializerExecutor.toJson(params);

        HttpEntity entity = new StringEntity(value, "utf-8");

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("content-type", "application/json;charset=utf-8");
        httpPost.setEntity(entity);
        handeleRequest(httpPost, headers, ResonseCallBack);
    }

    /**
     * 无参数的get请求
     *
     * @param url             请求地址地址
     * @param ResonseCallBack 回调
     */
    public void get(String url, ResonseCallBack ResonseCallBack) {
        this.get(url, null, null, ResonseCallBack);
    }


    /**
     * 无参数的get请求
     *
     * @param url             请求地址地址
     * @param ResonseCallBack 回调
     */
    public void get(String url, Map<String, Object> params, ResonseCallBack ResonseCallBack) {
        this.get(url, params, null, ResonseCallBack);
    }

    /**
     * get请求，参数放在map里
     *
     * @param url    请求地址
     * @param params 参数map
     */
    public void get(String url, Map<String, Object> params, Map<String, String> headers, ResonseCallBack ResonseCallBack) {

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
        }
        try {
            URIBuilder builder = new URIBuilder(url);
            builder.setParameters(pairs);
            HttpGet httpGet = new HttpGet(builder.build());
            handeleRequest(httpGet, headers, ResonseCallBack);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


    public void postForm(String url, ResonseCallBack ResonseCallBack) {
        this.postForm(url, null, null, ResonseCallBack);
    }

    public void postForm(String url, Map<String, Object> params, ResonseCallBack ResonseCallBack) {
        this.postForm(url, params, null, ResonseCallBack);
    }

    /**
     * 带参数的post请求
     *
     * @param url             请求diz地址
     * @param params          请求参数参数
     * @param ResonseCallBack 回调
     */
    public void postForm(String url, Map<String, Object> params, Map<String, String> headers, ResonseCallBack ResonseCallBack) {
        // 1. 声明httppost
        url = CommonUtils.decorateUrl(url);
        HttpPost httpPost = new HttpPost(url);

        // 2.封装请求参数，请求数据是表单
        // 声明封装表单数据的容器
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        if (params != null) {

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                // 封装请求参数到容器中
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
        }
        // 创建表单的Entity类
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(parameters, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 3. 把封装好的表单实体对象设置到HttpPost中
        httpPost.setEntity(entity);

        handeleRequest(httpPost, headers, ResonseCallBack);

    }

    public void putForm(String url, ResonseCallBack ResonseCallBack) {
        this.putForm(url, null, null, ResonseCallBack);
    }

    public void putForm(String url, Map<String, Object> params, ResonseCallBack ResonseCallBack) {
        this.putForm(url, params, null, ResonseCallBack);
    }

    public void putForm(String url, Map<String, Object> params, Map<String, String> headers, ResonseCallBack ResonseCallBack) {
        // 1. 声明httpput
        url = CommonUtils.decorateUrl(url);
        HttpPut httpPut = new HttpPut(url);

        // 2.封装请求参数，请求数据是表单
        if (params != null) {
            // 声明封装表单数据的容器
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                // 封装请求参数到容器中
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            // 创建表单的Entity类
            UrlEncodedFormEntity entity = null;
            try {
                entity = new UrlEncodedFormEntity(parameters, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // 3. 把封装好的表单实体对象设置到HttpPost中
            httpPut.setEntity(entity);
        }

        handeleRequest(httpPut, headers, ResonseCallBack);

    }


    public void deleteForm(String url, ResonseCallBack ResonseCallBack) {
        this.deleteForm(url, null, null, ResonseCallBack);
    }

    public void deleteForm(String url, Map<String, Object> params, ResonseCallBack ResonseCallBack) {
        this.deleteForm(url, params, null, ResonseCallBack);
    }

    public void deleteForm(String url, Map<String, Object> params, Map<String, String> headers, ResonseCallBack ResonseCallBack) {
        // 1. 声明httpput
        url = CommonUtils.decorateUrl(url);
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        // 2.封装请求参数，请求数据是表单
        if (params != null) {
            // 声明封装表单数据的容器
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                // 封装请求参数到容器中
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            // 创建表单的Entity类
            UrlEncodedFormEntity entity = null;
            try {
                entity = new UrlEncodedFormEntity(parameters, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // 3. 把封装好的表单实体对象设置到HttpPost中

            httpDelete.setEntity(entity);

        }
        handeleRequest(httpDelete, headers, ResonseCallBack);
    }


    public void upload(String url, File file, ResonseCallBack resonseCallBack) {
        upload(url, file, null, null, resonseCallBack);
    }

    public void upload(String url, File file, Map<String, Object> params, ResonseCallBack resonseCallBack) {
        upload(url, file, params, null, resonseCallBack);
    }

    public void upload(String url, File file, Map<String, Object> params, Map<String, String> headers, ResonseCallBack resonseCallBack) {
        url = CommonUtils.decorateUrl(url);
        HttpPost httpPost = new HttpPost(url);
        // 把文件转换成流对象FileBody
        FileBody fileBody = new FileBody(file);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart("file", fileBody);
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue().toString());
            }
        }
        HttpEntity reqEntity = builder.build();
        httpPost.setEntity(reqEntity);
        handeleRequest(httpPost, headers, resonseCallBack);
    }
}



