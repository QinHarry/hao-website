package com.hao.website.blog.controller.admin;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dto.RestResponse;
import com.hao.website.blog.entity.Comment;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.service.ICommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin/comments")
public class CommentController extends BaseController {

    @Autowired
    private ICommentService commentService;

    @GetMapping(value = "")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int pageSize,
                        HttpServletRequest request) {
        User user = this.user(request);
        Page<Comment> comments = commentService.getComments(user, page, pageSize);
        request.setAttribute("comments", comments);
        return "admin/comment_list";
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public RestResponse delete(@RequestParam int id) {
        String result = commentService.deleteById(id);
        if (!WebConstant.SUCCESS_RESULT.equals(result)) {
            return RestResponse.fail(result);
        }
        return RestResponse.ok();
    }

    @RequestMapping(value = "/status")
    @ResponseBody
    public RestResponse check(@RequestParam int id, @RequestParam String status) {
        try {
            Optional<Comment> comment = commentService.getComment(id);
            if (comment.isPresent()) {
                comment.get().setStatus(status);
                commentService.save(comment.get());
            } else {
                RestResponse.fail("Fail");
            }
        } catch (Exception e) {
            RestResponse.fail("Fail");
        }
        return RestResponse.ok();
    }
}
