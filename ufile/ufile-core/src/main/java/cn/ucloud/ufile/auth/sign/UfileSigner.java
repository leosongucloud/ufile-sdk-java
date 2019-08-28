package cn.ucloud.ufile.auth.sign;

import cn.ucloud.ufile.compat.base64.Base64StdEncoderCompat;
import cn.ucloud.ufile.util.Base64;
import cn.ucloud.ufile.util.Encryptor;
import cn.ucloud.ufile.util.HexFormatter;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

/**
 * Ufile 默认签名器
 *
 * @author: joshua
 * @E-mail: joshua.yin@ucloud.cn
 * @date: 2018/11/7 17:03
 */
public final class UfileSigner implements Signer {
    /**
     * 兼容Java 1.8以下的Base64 标准编码器接口
     */
    @Deprecated
    private Base64StdEncoderCompat base64;

    /**
     * 构造方法
     * 默认使用Java 1.8的Base64 标准编码器
     */
    public UfileSigner() {
    }

    /**
     * 构造方法 (若您的运行环境在Java 1.8以下，请使用该方法)
     *
     * @param base64 兼容Java 1.8以下的Base64 标准编码器接口
     */
    @Deprecated
    public UfileSigner(Base64StdEncoderCompat base64) {
        this.base64 = base64;
    }

    @Override
    public String signature(String key, String data) throws UfileSignatureException {
        byte[] hmacSha1 = null;
        try {
            hmacSha1 = Encryptor.Hmac_SHA1(key, data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UfileSignatureException(e);
        }

        if (hmacSha1 == null || hmacSha1.length == 0)
            throw new UfileSignatureException("Encrypt Hmac-SHA1 failed!");

        return Base64.getEncoder().encodeToString(hmacSha1);
    }

    @Override
    public String signatureBucket(String data) throws UfileSignatureException {
        byte[] sha1 = new byte[0];
        try {
            sha1 = Encryptor.SHA1(data.getBytes(Charset.forName("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new UfileSignatureException(e);
        }

        if (sha1 == null || sha1.length == 0)
            throw new UfileSignatureException("Encrypt SHA1 of signature is failed!");

        return HexFormatter.formatByteArray2HexString(sha1, false);
    }
}
