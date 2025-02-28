package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.dao.PendingUserInfoDao;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.entity.UserInfo;
import com.myzhihu.service.DraftService;
import com.myzhihu.service.ImageService;
import com.myzhihu.utils.ImagesUtils;
import com.myzhihu.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/pendingUserInfos")
public class PendingUserInfoController {

    @Resource(name = "pendingUserInfoService")
    private DraftService<UserInfo, Object> draftService;

    @PostMapping("/addPendingUserInfo")
    public Result<Boolean> addPendingUserInfo(@RequestBody UserInfo userInfo, @CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        int uid = (int) claimMap.get("user").asMap().get("userId");
        return draftService.addDraft(userInfo, uid, true) ? Result.success(true) : Result.error("你已经提交过该请求了，请耐心等待审核~");
    }

}
