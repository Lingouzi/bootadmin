package com.ly.bootadmin.common.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: linyun 664162337@qq.com
 * @Date: 2018/11/16 016 16:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {
    /**
     * 文件名称
     */
    private String name;
    /**
     * 上传时间
     */
    private Long date;

    /**
     * 文件类别
     */
    private String contentType;
    /**
     * 文件二进制长度
     */
    private Long size;
    /**
     * 网络地址
     */
    private String url;
    /**
     * 本地保存地址
     */
    @JsonIgnore
    private String savePath;

    /**
     * 1:保存成功
     * 0:保存失败
     */
    private Integer saved;
    /**
     * 1:上传成功
     * 0:上传失败
     */
    private Integer uploaded;
}
