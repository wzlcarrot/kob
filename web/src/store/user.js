import { defineStore } from 'pinia';
import $ from 'jquery';
import  { ref } from 'vue';


export const useUserStore = defineStore('user',()=>{
  
    let id =  ref(""); //id号
    let username = ref(""); //用户名
    let photo = ref(""); //头像
    let token = ref(""); //令牌号
    let is_login = ref(false); //是否登录
    let pulling_info = ref(true); // 是否正在从云端拉取信息
    
    //登录
    const login = (data) =>{
    
         $.ajax({
          url: "http://127.0.0.1:3000/user/account/token/",
          type: "post",
          data: {
            username: data.username,
            password: data.password,
          },
          success(resp){
            if(resp.message==="success"){
              localStorage.setItem("jwt_token", resp.token);
              //登录成功生成了token
              token.value = resp.token;
              is_login.value = true;
              console.log(is_login.value);
              console.log("token: "+resp.token);
             
            }
          },
          error(resp){
            is_login.value = false;
            console.log(is_login.value)
            console.log(resp);
          }
        });
      
    }
    
    //获取用户信息
    const getInfo = () =>{  

      $.ajax({  
          url: "http://127.0.0.1:3000/user/account/info/",  
          type: "get",  
          headers: {  
              Authorization: "Bearer " + token.value,  
          },  
          success(resp) {  
              if (resp.message === "success") {  
                  id.value = resp.id;  
                  username.value = resp.username;  
                  photo.value = resp.photo;  
                  is_login.value = true;  
                  console.log("id:"+id.value);
                  console.log("photo:"+photo.value);
              } else {  
                  console.log(resp);
              }  
          },  
          error(resp) {  
            console.log(resp);
          }  
      });  
  }

  //退出
  const logout = ()=>{
    id.value = "";
    username.value = "";
    photo.value = "";
    token.value = "";
    is_login.value = false;
  }
      return {id, username,photo,token,is_login,pulling_info,login,getInfo,logout};
  
});