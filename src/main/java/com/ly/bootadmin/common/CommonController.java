package com.ly.bootadmin.common;

import com.ly.bootadmin.common.bean.FileInfo;
import com.ly.bootadmin.config.BaseSetting;
import com.ly.bootadmin.sys.service.ISysPrivilegeService;
import com.ly.bootadmin.utils.JsonResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.LocalDate;

/**
 * @Author: linyun 664162337@qq.com
 * @Date: 2018/11/16 016 16:02
 */
@Slf4j
@RestController
@RequestMapping("/commons")
public class CommonController {

    @Autowired
    private BaseSetting setting;

    @Autowired
    private ISysPrivilegeService privilegeService;

    /**
     * 文件上传功能，支持多文件，返回数组，
     *
     * @param request
     * @return
     */
    @RequestMapping("/upload")
    public Object upload(@RequestParam("files") MultipartFile[] files, HttpServletRequest request) {
        if (files == null || files.length > 0) {
            return JsonResp.fail("上传文件为空");
        }
        log.info("IP:{},进行文件上传——开始", request.getRemoteAddr());
        FileInfo info = FileInfo.builder().build();
        for (MultipartFile file : files) {
            if (!file.isEmpty() && setting != null) {
                try {
                    String path = setting.getUploadFileSavePath();
                    if (StringUtils.isEmpty(path)) {
                        log.error("没有配置默认的上传文件存储地址");
                        return JsonResp.fail("文件服务器发生错误");
                    }
                    log.info("path:" + path);
                    String savePath = path + LocalDate.now().getYear() + LocalDate.now().getMonthValue() + LocalDate.now().getDayOfMonth() + (path.endsWith("/") ? "" : "/");

                    boolean createDirs = true;
                    if (!new File(savePath).exists()) {
                        log.info("文件夹不存在，创建...");
                        createDirs = new File(savePath).mkdirs();
                    }
                    if(createDirs){
                        String filePath = savePath + RandomUtils.nextInt(1000, 9000) + "_" + file.getOriginalFilename();
                        BufferedOutputStream out = new BufferedOutputStream(
                                new FileOutputStream(new File(filePath)));
                        out.write(file.getBytes());
                        out.flush();
                        out.close();

                        info.setSavePath(savePath);
                        info.setContentType(file.getContentType());
                        info.setSize(file.getSize());
                        info.setName(file.getOriginalFilename());
                        info.setSaved(1);
                        info.setUploaded(0);
                    }else{
                        return JsonResp.fail("创建文件夹失败.");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return JsonResp.fail("FileNotFoundException", info);
                } catch (IOException e) {
                    e.printStackTrace();
                    return JsonResp.fail("IOException", info);
                } catch (NullPointerException e) {
                    log.error("IP:{},上传文件时获取根路径失败!", request.getRemoteAddr());
                    e.printStackTrace();
                    return JsonResp.fail("上传文件时获取根路径失败", info);
                }
            }else{
                return JsonResp.fail("上传失败");
            }
        }
        return JsonResp.fail("上传失败");
    }
}
