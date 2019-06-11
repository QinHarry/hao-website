package com.hao.website.blog.controller.admin;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dto.LogActions;
import com.hao.website.blog.dto.RestResponse;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Attach;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.service.IAttachService;
import com.hao.website.blog.service.ILogService;
import com.hao.website.blog.utils.TaleUtils;
import com.hao.website.blog.utils.ViewUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/attach")
public class AttachController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AttachController.class);

    public static final String CLASSPATH = TaleUtils.getUploadFilePath();

    @Autowired
    private IAttachService attachService;

    @Autowired
    private ILogService logService;

    @GetMapping(value = "")
    public String index(HttpServletRequest request,
                        @RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "12") int pageSize) {
        Page<Attach> attaches = attachService.getAttachs(page, pageSize);
        request.setAttribute("attaches", attaches);
        request.setAttribute(Types.ATTACH_URL.getType(),
                ViewUtil.siteOption(Types.ATTACH_URL.getType(), ViewUtil.siteUrl()));
        request.setAttribute("max_file_size", WebConstant.MAX_FILE_SIZE / 1024);
        return "admin/attach";
    }

    @PostMapping(value = "/upload")
    @ResponseBody
    public RestResponse upload(HttpServletRequest request,
                               @RequestParam("file") MultipartFile[] multipartFiles) throws IOException {
        User user = this.user(request);
        List<String> errorFiles = new ArrayList<>();
        try {
            for (MultipartFile multipartFile : multipartFiles) {
                String fileName = multipartFile.getOriginalFilename();
                if (multipartFile.getSize() <= WebConstant.MAX_FILE_SIZE) {
                    String fileKey = TaleUtils.getFileKey(fileName);
                    String fileType = TaleUtils.isImage(multipartFile.getInputStream()) ?
                            Types.IMAGE.getType() : Types.FILE.getType();
                    File file = new File(TaleUtils.CLASSPATH + fileKey);
                    try {
                        FileCopyUtils.copy(multipartFile.getInputStream(), new FileOutputStream(file));
                        attachService.save(fileName, fileKey, fileType, user);
                    } catch (IOException e) {
                        logger.error("fail to copy file: " + e.getMessage());
                        errorFiles.add(fileName);
                    }
                } else {
                    errorFiles.add(fileName);
                }
            }
        } catch (Exception e) {
            return RestResponse.fail(e.getMessage());
        }
        return RestResponse.ok(errorFiles);
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public RestResponse delete(@RequestParam Integer id, HttpServletRequest request) {
        try {
            Optional<Attach> attach = attachService.getById(id);
            if (!attach.isPresent()) {
                return RestResponse.fail("Attach does not exist");
            }
            attachService.deleteById(id);
            new File(TaleUtils.CLASSPATH + attach.get().getFileKey()).delete();
            logService.insertLog(LogActions.DEL_FILE.getAction(),
                    attach.get().getFileKey(), request.getRemoteAddr(), this.getUid(request));
        } catch (Exception e) {
            String msg = "Fail to delete the file";
            logger.error(msg);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }


}
