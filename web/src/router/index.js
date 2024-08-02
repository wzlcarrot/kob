import { createRouter, createWebHistory } from 'vue-router'
import NotFound from '../views/error/NotFound.vue'
import PkIndexView from '../views/pk/PkIndexView.vue'
import RankListIndexView from '../views/ranklist/RankListIndexView.vue'
import UserBotIndexView from '../views/user/bot/UserBotIndexView.vue'
import RecordIndexView from '../views/record/RecordIndexView.vue'
import UserAccountLoginView from '@/views/user/account/UserAccountLoginView.vue'
import UserAccountRegisterView from '@/views/user/account/UserAccountRegisterView.vue'
import RecordContentView from '@/views/record/RecordContentView.vue'

import { useUserStore } from '@/store/user'

const routes = [
  {
    path:"/",
    name:"home",
    redirect:"/pk/",
    meta:{
      requestAuth:true //true表示home页面需要授权
    }
  },
  {
    path:"/pk/",
    name:"pk_index",
    component:PkIndexView,
    meta:{
      requestAuth:true //true表示pk页面需要授权
    }
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/record/:recordId/",
    name: "record_content",
    component: RecordContentView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path:"/ranklist/",
    name:"ranklist_index",
    component:RankListIndexView,
    meta:{
      requestAuth:true //true表示ranklist页面需要授权
    }
  },
  {
    path:"/user/bot/",
    name:"user_bot_index",
    component:UserBotIndexView,
    meta:{
      requestAuth:true //true表示bot页面需要授权
    }
  },
  {
    path:"/user/account/login/",
    name:"user_account_login",
    component:UserAccountLoginView,
    meta:{
      requestAuth:false //false表示login页面不需要授权
    }
  },
  {
    path:"/user/account/register/",
    name:"user_account_register",
    component:UserAccountRegisterView,
    meta:{
      requestAuth:false //false表示register页面不需要授权
    }
  },
  {
    path:"/404/",
    name:"not_found",
    component:NotFound,
    meta:{
      requestAuth:false //false表示404页面不需要授权
    }
  },
  {
    path:"/:catchAll(.*)",
    redirect:"/404/"
  }
]

//创建路由
const router = createRouter({
  history: createWebHistory(),
  routes
})

//添加登录验证
router.beforeEach((to,from,next)=>{
  let jwt_token=localStorage.getItem('jwt_token');
  let userStore = useUserStore();
  if(jwt_token){
    
    userStore.token = jwt_token;
    //获取个人信息
     userStore.getInfo();
    next();
  }else{
    //将要走的页面需要授权，并且未登录，则到登录页面
    if(to.meta.requestAuth&&!userStore.is_login)
      next({name:"user_account_login"});
    else
    //反之，则默认走到下一个页面
      next();
  }
})

export default router
