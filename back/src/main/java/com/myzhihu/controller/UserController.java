package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.constant.RegisterCode;
import com.myzhihu.domain.dto.LoginUserDTO;
import com.myzhihu.domain.dto.Register;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.dto.UserInfoDTO;
import com.myzhihu.domain.entity.User;
import com.myzhihu.domain.entity.UserInfo;
import com.myzhihu.service.UserInfoService;
import com.myzhihu.service.UserService;
import com.myzhihu.service.impl.UserSearchServiceImpl;
import com.myzhihu.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    UserSearchServiceImpl userSearchService;
    UserService userService;
    UserInfoService userInfoService;

    @PostMapping("/login")
    public ResponseEntity<Result<UserInfo>> login(@RequestBody LoginUserDTO loginUserDTO) {

        UserInfo userInfo = userService.login(loginUserDTO);
        if (userInfo == null)
            return ResponseEntity
                    .status(200)
                    .body(Result.error("登录失败，用户名或密码错误"));

        String token = JwtUtils.createJwtWithUidAndDeviceId(userInfo.getUserId(), loginUserDTO.getDeviceId());
        ResponseCookie cookie = ResponseCookie
                .from("token", token)
//                .httpOnly(true)
                .secure(false)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Result.success(userInfo));
    }

    @PostMapping("/logout")
    public Result<Boolean> logout(@CookieValue(value = "token") String token) {
        Map<String, Claim> payload = JwtUtils.getJwtPayload(token);
        Map<String, Object> userMap = payload.get("user").asMap();
        int uid = (int) userMap.get("userId");
        String deviceId = (String) userMap.get("deviceId");
        return userService.logout(uid, deviceId) ? Result.success(true) : Result.error("服务器错误，请稍后再试");
    }

    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody Register register){
        switch (userService.register(register)) {
            case RegisterCode.ILLEGAL_DATA -> {
                return Result.error("注册失败，数据非法");
            }
            case RegisterCode.VERIFY_INCORRECT -> {
                return Result.error("注册失败，验证码错误");
            }
            case RegisterCode.VERIFY_NOT_EXIST -> {
                return Result.error("注册失败，验证码不存在");
            }
            case RegisterCode.ERROR -> {
                return Result.error("注册失败，出现错误，请稍后再试");
            }
            default -> {
                return Result.success(true);
            }
        }
    }

    @GetMapping("/{account}")
    public Result<Boolean> registerCheck(@PathVariable("account") String account){
        return Result.success(userService.checkEmail(account));
    }

    @GetMapping("/checkToken")
    public Result<Boolean> checkToken(@CookieValue(value = "token", defaultValue = "", required = false) String token){
        return userService.checkToken(token) ? Result.success(true) : Result.error("token解析失败");
    }

    @PostMapping("/getVerify")
    public Result<Boolean> generateVerify(@RequestParam String account) {
        return userService.generateRegisterVerify(account) ? Result.success(true) : Result.error("该账户已存在");
    }

    @PostMapping("/forgetPassword")
    public Result<Boolean> forgetPassword(@RequestParam("email") String email) {
        return userService.generateFindPasswordVerify(email) ? Result.success(true) : Result.error("该账户不存在");
    }

    @PostMapping("/updatePassword")
    public Result<Boolean> updatePassword(String verify, String password, String email) {
        return userService.updatePassword(email, password, verify) ? Result.success(true) : Result.error("验证码错误");
    }

    @GetMapping("/manage")
    @PreAuthorize("hasAuthority('article:manage')")
    public Result<Boolean> manageCheck(){
        return Result.success(true);
    }

    @GetMapping("/search")
    public Result<List<UserInfo>> searchUsers(@RequestParam String keywords, @RequestParam int page) {
        List<UserInfo> res = userSearchService.doSearch(keywords, page, 6);
        return Result.success(res);
    }

    @GetMapping("/getUserInfo")
    public Result<UserInfo> getUserInfo(@CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claimMap.get("user").asMap().get("userId");
        return Result.success(userInfoService.getUserInfoByUid(uid));
    }

    @GetMapping("/getUserInfoById/{uid}")
    public Result<UserInfoDTO> getUserInfoById(@PathVariable("uid")Integer uid, @CookieValue(value = "token", defaultValue = "") String token) {
        int myUid = 0;
        if (!token.isEmpty()) {
            Map<String, Claim> claimMap = JwtUtils.getJwtPayload(token);
            myUid = (Integer) claimMap.get("user").asMap().get("userId");
        }
        UserInfoDTO wrappedUserInfo = userInfoService.getWrappedUserInfoByUid(uid, myUid);
        return wrappedUserInfo != null ? Result.success(wrappedUserInfo) : Result.error("该用户不存在");
    }

}
