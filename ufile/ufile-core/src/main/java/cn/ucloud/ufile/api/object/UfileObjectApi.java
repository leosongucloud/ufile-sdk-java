package cn.ucloud.ufile.api.object;

import cn.ucloud.ufile.auth.ObjectAuthorizer;
import cn.ucloud.ufile.api.UfileApi;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.http.HttpClient;
import cn.ucloud.ufile.util.Encoder;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Ufile 对象存储相关API基类
 *
 * @author: joshua
 * @E-mail: joshua.yin@ucloud.cn
 * @date: 2018/11/13 11:22
 */
public abstract class UfileObjectApi<T> extends UfileApi<T> {
    protected ObjectAuthorizer authorizer;
    protected ObjectConfig objectConfig;

    /**
     * 构造方法
     *
     * @param authorizer   Object授权器
     * @param objectConfig ObjectConfig {@link ObjectConfig}
     * @param httpClient   Http客户端
     */
    public UfileObjectApi(ObjectAuthorizer authorizer, ObjectConfig objectConfig, HttpClient httpClient) {
        super(httpClient, null);
        this.authorizer = authorizer;
        this.objectConfig = objectConfig;
    }

    /**
     * 生成最终API域名
     *
     * @param bucketName bucket名称
     * @param keyName    对象名称
     * @return API域名
     */
    protected String generateFinalHost(String bucketName, String keyName) throws UfileClientException {
        if (objectConfig == null)
            return null;

        try {
            if (objectConfig.isCustomDomain()) {
                keyName = Encoder.urlEncode(keyName, "UTF-8");
                host = String.format("%s/%s", objectConfig.getCustomHost(), keyName);
            } else {
                bucketName = Encoder.urlEncode(bucketName, "UTF-8");
                String region = Encoder.urlEncode(objectConfig.getRegion(), "UTF-8");
                String proxySuffix = Encoder.urlEncode(objectConfig.getProxySuffix(), "UTF-8");
                keyName = Encoder.urlEncode(keyName, "UTF-8");
                host = new StringBuilder(objectConfig.getProtocol().getValue())
                        .append(String.format("%s.%s.%s/%s", bucketName, region, proxySuffix, keyName)).toString();
            }
        } catch (UnsupportedEncodingException e) {
            throw new UfileClientException("Occur error during URLEncode bucketName and keyName", e);
        }

        return host;
    }

    protected String readResponseBody(Response response) {
        if (response == null)
            return null;
        ResponseBody body = response.body();
        if (body == null)
            return null;

        try {
            return body.string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
