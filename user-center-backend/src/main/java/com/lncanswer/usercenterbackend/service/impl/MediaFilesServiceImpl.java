package com.lncanswer.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.lncanswer.usercenterbackend.common.ErrorCode;
import com.lncanswer.usercenterbackend.exception.BusinessException;
import com.lncanswer.usercenterbackend.mapper.MediaFilesMapper;
import com.lncanswer.usercenterbackend.model.domain.MediaFiles;
import com.lncanswer.usercenterbackend.model.domain.dto.UploadFileParamsDto;
import com.lncanswer.usercenterbackend.model.domain.dto.UploadFileResultDto;
import com.lncanswer.usercenterbackend.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/14 14:34
 */
@Slf4j
@Service
public class MediaFilesServiceImpl extends ServiceImpl<MediaFilesMapper,MediaFiles> implements MediaFileService {


    /**
     *获取配置文件中的minio存储桶信息
     */
    @Value("${minio.bucket.files}")
    private String bucketFiles;

    @Value("${minio.bucket.videofiles}")
    private String bucketVideoFiles;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 注入minioClient对象
     */
    @Resource
    MinioClient minioClient;

    @Resource
    MediaFilesMapper mediaFilesMapper;

    //事务优化，注入代理对象 非事务方法调用事务方法事务不生效，通过提升为接口，用代理对象来调用
     //延迟注入解决循环依赖 TODO 后续优化
//    @Resource
//    MediaFileService currentPoxt;

    /**
     * 上传文件
     * @param uploadFileParamsDto 请求参数封装对象
     * @param localFilePath 文件磁盘路径
     * @param objectName 存储桶名称
     * @return
     */
    @Override
    public UploadFileResultDto uploadFile(UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName) {
        //根据路径创建File 用来对此文件进行md5加密
        File file = new File(localFilePath);
        if (!file.exists()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件不存在");
        }
        //获取文件名称
        String filename = uploadFileParamsDto.getFilename();
        //获取文件的后缀
        String extension = filename.substring(filename.lastIndexOf("."));
        //自定义方法来获取文件类型
        String mimeType = getMimeType(extension);

        //获取文件的Md5值
        String fileMd5 = getFileMd5(file);

        //文件的默认路径
        String defaultFoladerPath = getDefaultFoladerPath();

        //上传到minio中bucket桶的对象名
        if (StringUtils.isEmpty(objectName)){
            objectName = defaultFoladerPath + fileMd5 + extension;
        }

        //将文件上传到minio中
        boolean b = addMediaFilesToMinIO(localFilePath, mimeType, bucketFiles, objectName);

        //设置文件大小
        uploadFileParamsDto.setFileSize(file.length());
        //将文件信息存储到数据库中
        MediaFiles mediaFiles = addMediaFilesToDb(fileMd5, uploadFileParamsDto, bucketFiles, objectName);
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles,uploadFileResultDto);

        //设置返回对象中的图片地址 endpoint + bucket + objectName
        uploadFileResultDto.setImageUrl(endpoint + "/" + uploadFileResultDto.getBucket() + "/" + uploadFileResultDto.getFilePath());
        return uploadFileResultDto;
    }

    /**
     * 根据文件后缀获取文件类型
     * @param extension
     */
    private String getMimeType(String extension) {
        if (extension == null){
            extension = "";
        }
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extension!= null){
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /**
     * 根据文件获取文件的md5值
     * @param file
     */
    private String getFileMd5(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            //利用工具类进行md5加密
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            log.info("fileMd5 = {}",fileMd5);
            return fileMd5;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件的m默认路径
     * @return
     */
    private String getDefaultFoladerPath() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String folder = simpleDateFormat.format(new Date()).replace("-","/") + "/";
        return folder;
    }


    /**
     *上传文件到 minio中
     * @param localFilePath 文件路径
     * @param mimeType   文件类型
     * @param bucketFiles 文件桶 --存储位置
     * @param objectName   对象名称
     * @return
     */
    private boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucketFiles, String objectName) {
        try {
            System.out.println(bucketFiles);
            UploadObjectArgs uploadFile = UploadObjectArgs.builder().bucket(bucketFiles).object(objectName).filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            //MinioClient委托上传
            minioClient.uploadObject(uploadFile);
            log.info("上传文件成功,bucket：{},objectName:{}",bucketFiles,objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件失败，错误原因：{}",e.getMessage(),e);

        }
        return false;
    }

    /**
     *存储文件信息到数据库中
     * @param fileMd5 文件md5值
     * @param uploadFileParamsDto 上传对象参数
     * @param bucketFiles 文件桶
     * @param objectName  文件名
     * @return
     */
   // @Transactional
    public MediaFiles addMediaFilesToDb(String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucketFiles, String objectName) {
        //从数据库中查询文件是否已经存在，md5值是数据库mediaFile表的主键
        MediaFiles mediaFiles =  mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null){
            //创建一个MediaFiles存储到数据库中
            mediaFiles = new MediaFiles();
            //将基本信息拷贝
            BeanUtils.copyProperties(uploadFileParamsDto,mediaFiles);
            //设置其他信息
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setBucket(bucketFiles);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setAuditStatus("1");
            mediaFiles.setStatus("2");
            //设置完成保存文件信息到数据库
            int insertNum = mediaFilesMapper.insert(mediaFiles);
            if (insertNum <=0){
                log.error("保存文件信息到数据库失败:{}", mediaFiles);
                throw new BusinessException(ErrorCode.UPDATE_ERROR,"保存文件信息失败");
            }
            log.debug("保存文件信息到数据库成功:{}",mediaFiles);
        }

        //添加到到待处理任务表
        //addWaitingTask(mediaFiles);
        return mediaFiles;
    }

}
