import axios from "axios";
import {ElMessage} from "element-plus";
import {useUserInfoStore} from "@/stores/userInfo.js";

const baseURL = '/api'
const instance = axios.create({baseURL});
instance.interceptors.response.use(
    result =>{
        // console.log(result.data)
        return result.data;
    },
    async err => {
        if (err.response && err.response.status === 401) {
            let date = new Date();
            date.setDate(date.getDate() - 1);
            document.cookie = 'token' + '=' + null + ';expires=' + date;
            ElMessage.error('未登录，请登录后再试');
            // await router.replace('/login')
            useUserInfoStore().removeUserInfo();
            return err.response;
        }
        console.log(err);
    }
)
export default instance;