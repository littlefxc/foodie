package com.fengxuechao.controller;

import com.fengxuechao.pojo.Users;
import com.fengxuechao.pojo.vo.UsersVO;
import com.fengxuechao.resourse.FileResource;
import com.fengxuechao.service.FdfsService;
import com.fengxuechao.service.center.CenterUserService;
import com.fengxuechao.utils.CookieUtils;
import com.fengxuechao.utils.ResultBean;
import com.fengxuechao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("fdfs")
public class CenterUserController extends BaseController {

    @Autowired
    private FileResource fileResource;

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FdfsService fdfsService;

    @PostMapping("uploadFace")
    public ResultBean uploadFace(
            String userId,
            MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String path = "";
        // 开始文件上传
        if (file != null) {
            // 获得文件上传的文件名称
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {

                // 文件重命名  imooc-face.png -> ["imooc-face", "png"]
                String fileNameArr[] = fileName.split("\\.");

                // 获取文件的后缀名
                String suffix = fileNameArr[fileNameArr.length - 1];

                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")) {
                    return ResultBean.errorMsg("图片格式不正确！");
                }

//                path = fdfsService.upload(file, suffix);

                path = fdfsService.uploadOSS(file, userId, suffix);
                System.out.println(path);
            }
        } else {
            return ResultBean.errorMsg("文件不能为空！");
        }

        if (StringUtils.isNotBlank(path)) {
//            String finalUserFaceUrl = fileResource.getHost() + path;
            String finalUserFaceUrl = fileResource.getOssHost() + path;

            Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);

            UsersVO usersVO = conventUsersVO(userResult);

            CookieUtils.setCookie(request, response, "user",
                    JsonUtils.objectToJson(usersVO), true);
        } else {
            return ResultBean.errorMsg("上传头像失败");
        }

        return ResultBean.ok();
    }

}
