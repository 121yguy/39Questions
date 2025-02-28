import instance from "@/utils/request.js";
import qs from 'qs';

// 登录
export async function login(account, pass, deviceId) {
    instance.defaults.headers.post['Content-Type'] = 'application/json';
    return await instance.post("/users/login", JSON.stringify({
        user: {
            account: account,
            pass: pass
        },
        deviceId: deviceId
    }))
}


// 注册
export async function register(user, Verify) {
    return await instance.post("/users/register", JSON.stringify({
        user: user,
        verify: Verify
    }),
        {
            headers: {
                'Content-Type': 'application/json'
            }
        })
}

export async function checkRegisterAccount(account){
    return await instance.get("/users/" + account);
}

export async function checkToken() {
    return await instance.get("/users/checkToken");
}

export async function sendVerify(account) {
    if (!account)
        return false;
    return await instance.post("/users/getVerify", qs.stringify({
        account: account
    },
        {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    }))
}

export async function logout() {
    return await instance.post("/users/logout");
}


