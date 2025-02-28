import {createRouter, createWebHashHistory, createWebHistory} from "vue-router";

// index子路由
import index from '@/views/IndexView.vue';
import HomeView from "@/views/HomeView.vue";
import LoginAndRegisterView from "@/views/LoginAndRegisterView.vue";
import UserView from "@/views/UserView.vue";
import TestView from "@/views/TestView.vue";
import QuestionView from "@/views/QuestionView.vue";
import QuestionAndAnswerView from "@/views/QuestionAndAnswerView.vue";
import SearchView from "@/views/SearchView.vue";
import EditUserInfoView from "@/views/EditUserInfoView.vue";
import ManageView from "@/views/ManageView.vue";
import NotFoundView from "@/views/NotFoundView.vue";
import FavoritesFolderView from "@/views/FavoritesFolderView.vue";
import DraftView from "@/views/DraftView.vue";
import QuestionWithoutAnswerView from "@/views/QuestionWithoutAnswerView.vue";
import ForgetPasswordView from "@/views/ForgetPasswordView.vue";
import CommentView from "@/views/CommentView.vue";

const routes = [
    {path: '/home', redirect: '/',component: index,children: [
            {path: '/',component: HomeView},
            {path: '/user/:uid/:tab?',component: UserView,beforeEnter: (to, from, next) => {
                    const uid = to.params.uid;
                    if (!uid || isNaN(Number(uid))) {
                        next('/404');
                    } else {
                        next();
                    }
                }},
            {path: '/search', component: SearchView},
            {path: '/user/edit', component: EditUserInfoView},
            {path: '/draft/:tab?', component: DraftView},
            {path: '/question/waiting', component: QuestionWithoutAnswerView},
            {path: '/comments', component: CommentView},
            {path: '/favoritesFolder/:fid', component: FavoritesFolderView},
        ]},
    {path: '/question/:id', component: QuestionView},
    {path: '/login', component: LoginAndRegisterView},
    {path: '/forgetPassword', component: ForgetPasswordView},
    {path: '/test/:pathMatch(.*)', component: TestView},
    {path: '/question/:qid/:aid', component: QuestionAndAnswerView},
    {path: '/manage/:tab?', component: ManageView},
    {path: '/:pathMatch(.*)*', component: NotFoundView},
]
const router = createRouter({
    routes: routes,
    history: createWebHistory()
})

export default router