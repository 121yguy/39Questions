import instance from "@/utils/request.js";
import qs from "qs";

export async function getUserInfo() {
    return await instance.get("/users/getUserInfo");
}

export async function getUserInfoById(uid) {
    return await instance.get("/users/getUserInfoById/" + uid);
}

export async function subscribe(uid) {
    return await instance.post("/subscribe?followedId=" + uid)
}

export async function cancelSubscribe(uid) {
    return await instance.delete("/subscribe?followedId=" + uid)
}

export async function getFansByUid(uid) {
    return await instance.get("/subscribe/getFansByUid/" + uid)
}

export async function getFollowedByUid(uid) {
    return await instance.get("/subscribe/getFollowedByUid/" + uid)
}

export async function getIsSubscribe(followedId) {
    return await instance.get('/subscribe/isSubscribe/' + followedId);
}
export async function getFollowersList(uid, page) {
    return await instance.get('/subscribe/getFollowersList?page=' + page + '&uid=' + uid);
}

export async function getFansList(uid, page) {
    return await instance.get('/subscribe/getFansList?page=' + page + '&uid=' + uid);
}

export async function searchUsers(keywords, page) {
    return await instance.get('/users/search?page=' + page + '&keywords=' + keywords);
}

export async function updateToken() {
    return await instance.post('/commons/updateToken');
}
export async function manage() {
    return await instance.get('/users/manage');
}

export async function forgetPassword(email) {
    return await instance.post('/users/forgetPassword?email=' + email);
}

export async function updatePassword(email, password, verify) {
    return await instance.post('/users/updatePassword', qs.stringify({
        email: email,
        password: password,
        verify: verify
    }),
        {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
}

export async function addPendingUserInfo(userInfo) {
    return await instance.post('/pendingUserInfos/addPendingUserInfo', JSON.stringify({
        userId: userInfo.userId,
        nickName: userInfo.nickName,
        about: userInfo.about,
        icon: userInfo.icon,
        background: userInfo.background
    }), {
        headers: {
            'content-type': 'application/json'
        }
    });
}

