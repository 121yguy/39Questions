import {createPinia, defineStore} from "pinia";
import {ref} from "vue";
import {createPersistedState} from "pinia-persistedstate-plugin";

import {createApp} from "vue";
import app from '@/App.vue'

const pinia = createPinia();
const persist = createPersistedState();

pinia.use(persist);
export const useUserInfoStore = defineStore(
    'userInfo',
    () => {
        const userInfo = ref({
            userId: null,
            nickName: '',
            icon: '',
            about: '',
        });

        const setUserInfo = (value) => {
            userInfo.value = value;
        }

        const removeUserInfo = () => {
            userInfo.value = {
                userId: null,
                nickName: '',
                icon: '',
                about: '',
            };
        }

        return {
            userInfo,setUserInfo,removeUserInfo
        }
    },
    {
        persist: true
    }
)