package com.cyh.hao_vcs.service.impl;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.config.QiNiuConfig;
import com.cyh.hao_vcs.service.FileService;
import com.cyh.hao_vcs.utils.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Value("${qiniu.accessKey}")
    private String ACCESS_KEY;

    @Value("${qiniu.secretKey}")
    private String SECRET_KEY;

    //要上传的空间
    @Value("${qiniu.bucket}")
    private String bucketName;

    @Value("${qiniu.cdns}")
    private String CDNS;

    @Autowired
    private ObjectMapper objectMapper;

    private Auth auth;

    private final UploadManager uploadManager;

    private static final int successStatus = 200;

    @Autowired
    public void init(@Value("${qiniu.accessKey}") String ACCESS_KEY,
                     @Value("${qiniu.secretKey}") String SECRET_KEY) {
        //密钥配置
        this.auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    }

    FileServiceImpl() {
        // 构造一个带指定Zone对象的配置类,不同的七云牛存储区域调用不同的zone
        Configuration cfg = new Configuration(Zone.zone1());

        //创建上传对象
        uploadManager = new UploadManager(cfg);
    }


    public String getUpToken() {
        return auth.uploadToken(bucketName);
    }

    @Override
    public R saveImage(MultipartFile file) {
        try {
            // 判断是否有合法的文件后缀
            String suffix = getSuffix(file);
            if (Objects.isNull(suffix)) {
                return null;
            }
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
            // 调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());
            // 打印返回的信息
            if (res.isOK() && res.isJson()) {
                // 返回这张存储照片的地址
                return R.success(fileName);
            } else {
                return R.error("上传失败");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return R.error("上传失败");
        }
    }

    private String getSuffix(MultipartFile file) {
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String suffix = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        return FileUtil.isPicAllowed(suffix) ? suffix : null;
    }

    public boolean renameFile(String oldName, String newName) {
        BucketManager bucketManager = new BucketManager(auth, new Configuration(Zone.zone1()));
        try {
            Response response = bucketManager.move(QiNiuConfig.bucket, oldName, QiNiuConfig.bucket, newName);
            return !Objects.isNull(response) && Objects.equals(successStatus, response.statusCode);
        } catch (QiniuException e) {
            System.out.println(e.response.toString());
        }
        return false;
    }

    public boolean deleteFile(String fileName) {
        BucketManager bucketManager = new BucketManager(auth, new Configuration(Zone.zone1()));
        try {
            Response response = bucketManager.delete(QiNiuConfig.bucket, fileName);
            return !Objects.isNull(response) && Objects.equals(successStatus, response.statusCode);
        } catch (QiniuException e) {
            System.out.println(e.response.toString());
        }
        return false;
    }
}
