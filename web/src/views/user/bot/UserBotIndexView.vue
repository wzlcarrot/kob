<script setup>
   import { useUserStore } from '@/store/user';
   import { ref } from 'vue';
   import $ from 'jquery';
   import { Modal } from 'bootstrap/dist/js/bootstrap';
   import { VAceEditor } from 'vue3-ace-editor';
   import ace from 'ace-builds';
   //导入语法高亮插件
   import 'ace-builds/src-noconflict/mode-java'

   ace.config.set(
    "basePath", 
    "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")
   
   

   const userStore = useUserStore();
   let bots = ref([]);

   let title = ref("");
   let description = ref("");
   let content = ref("");
   let message = ref("");

   //刷新一下bot
   const refresh_bots = () => {
      $.ajax ({
         url: "http://127.0.0.1:3000/user/bot/getList/",
         type: "get",
         headers: {
            Authorization: "Bearer " + userStore.token,
         },
         success(resp){
            bots.value = resp;
         },
         error(resp){
            console.log(resp);
         }
         
      });
   }

   refresh_bots();

   //定义一个创建bot的函数
   const add_bot = () => {
      message.value = "";
      $.ajax ({
         url: "http://127.0.0.1:3000/user/bot/add/",
         type: "post",
         headers: {
            Authorization: "Bearer " + userStore.token,
         },
         //title description content这些是传递给后端的数据
         data:{
            title: title.value,
            description: description.value,
            content: content.value
         },
         success(resp){
            console.log("message:"+resp.message);
            if(resp.message==="success"){
               //如果创建成功，则更新bot         
               refresh_bots();
               //清空表单数据
               title.value = "";
               description.value = "";
               content.value = "";
               Modal.getInstance("#add-bot-btn").hide();
            }
            else{
               message.value = resp.message;
             
            }
         },
         error(resp){
            console.log(resp);
         }
   
      });
   }

   //定义一个删除bot的函数
   const remove_bot = (bot) => {
      $.ajax({
         url: "http://127.0.0.1:3000/user/bot/remove/",
         type: "post",
         headers: {
            Authorization: "Bearer " + userStore.token,
         },
         data:{
            bot_id:bot.id
         },
         success(resp){
            //如果删除成功，则更新bot
            if(resp.message==="success"){
               refresh_bots();
               console.log(resp);
            }
            else{
               console.log(resp)
            }
         },
         error(resp){
            console.log(resp);
         }
      });
   }

   //定义一个修改bot的函数
   const update_bot = (bot) => {
  
      $.ajax({
         url: "http://127.0.0.1:3000/user/bot/update/",
         type: "post",
         headers: {
            Authorization: "Bearer " + userStore.token,
         },
         data:{
            bot_id:bot.id,
            title:bot.title,
            description:bot.description,
            content:bot.content
         },
         success(resp){
            
            //如果修改成功，则更新bot
            if(resp.message==="success"){
               refresh_bots();
               //关闭模态框
               Modal.getInstance('#update-bot-modal-' + bot.id).hide();
               console.log(resp);
            }
            else{
               message.value = resp.message;
            }
         },
         error(resp){
            console.log(resp);
         }
      });
   }
     

</script>

<template>
   <div class="container">
       <div class="row">
         <div class="col-3">
            <div class="card" style="margin-top: 20px;">
               <div class="card-body">
                  <img :src="userStore.photo" alt="" style="width: 100%;">
               </div>
            </div>
         </div>

         <div class="col-9">
            <div class="card" style="margin-top: 20px;">
               <div class="card-header">
                  <span style="font-size: 120%;">我的bot</span>
                  <!--float-end是向右对齐-->
                  <!-- Button trigger modal -->
                  <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn" style="font-size: 120%;">
                  创建Bot
                  </button>

                  <!-- Modal -->
                  <div class="modal fade" id="add-bot-btn" tabindex="-1">
                     <div class="modal-dialog modal-xl">
                        <div class="modal-content">
                           <div class="modal-header">
                              <h1 class="modal-title fs-5" id="exampleModalLabel">创建Bot</h1>
                              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                           </div>
                           <div class="modal-body">
                              <div class="mb-3">
                                 <label for="add-bot-title" class="form-label">名称</label>
                                 <input type="text" v-model="title" class="form-control" id="add-bot-title" placeholder="请输入bot名称">
                              </div>
                              <div class="mb-3">
                                 <label for="add-bot-description" class="form-label">简介</label>
                                 <textarea class="form-control" v-model="description" id="add-bot-description" rows="3" placeholder="请输入bot简介"></textarea>
                              </div>

                              <div class="mb-3">
                                 <label for="add-bot-code" class="form-label">bot代码</label>
                                 <VAceEditor
                                    v-model:value="content"
                                    @init="editorInit"
                                    lang="java"
                                    theme="textmate"
                                    :options="{fontSize: 20}" 
                                    style="height: 300px" />
                              </div>
                           </div>
                           <div class="modal-footer">
                              <div class="message">{{message}}</div>
                              <button type="button" class="btn btn-primary" @click="add_bot">创建</button>
                              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                          
                           </div>
                        </div>
                     </div>
                  </div>
               </div>

               <div class="card-body">
                  <table class="table table-hover">
                  <thead>
                     <tr>
                        <th scope="col">Bot名称</th>
                        <th scope="col">创建时间</th>
                        <th scope="col">操作</th>
                     </tr>
                  </thead>
                  <tbody>
                    <tr v-for="bot in bots" :key="bot.id">
                     <td>{{bot.title}}</td>
                     <td>{{bot.createtime}}</td>
                     <td>
                        <!--如果属性是表达式，则需要加上:-->
                        <button type="button" class="btn btn-secondary" style="margin-right: 10px" data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-' + bot.id">修改Bot</button>
                        
                        <!--modal-->
                        <div class="modal fade" :id="'update-bot-modal-' + bot.id" tabindex="-1">
                           <div class="modal-dialog modal-xl">
                              <div class="modal-content">
                                 <div class="modal-header">
                                    <h1 class="modal-title fs-5" id="exampleModalLabel">修改Bot</h1>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                 </div>
                                 <div class="modal-body">
                                    <div class="mb-3">
                                       <label for="update-bot-title" class="form-label">名称</label>
                                       <input type="text" v-model="bot.title" class="form-control" id="update-bot-title" placeholder="请输入bot名称">
                                    </div>
                                    <div class="mb-3">
                                       <label for="update-bot-description" class="form-label">简介</label>
                                       <textarea class="form-control" v-model="bot.description" id="update-bot-description" rows="3" placeholder="请输入bot简介"></textarea>
                                    </div>

                                    <div class="mb-3">
                                       <label for="update-bot-code" class="form-label">bot代码</label>
                                       <!--修改字体大小-->
                                       <VAceEditor
                                          v-model:value="bot.content"
                                          @init="editorInit"
                                          lang="java"
                                          theme="textmate"
                                       
                                          :options="{fontSize: 20}" 
                                          style="height: 300px" />

                                    </div>
                                 </div>
                                 <div class="modal-footer">
                                    <div class="message">{{message}}</div>
                                    <button type="button" class="btn btn-primary" @click="update_bot(bot)">修改</button>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                              
                                 </div>
                              </div>
                           </div>
                        </div>
             
                        
                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除Bot</button>
                     </td>
                    </tr>
                  </tbody>
                  </table>
               </div>
            </div>
         </div>
       </div>
   </div>
   
</template>
  
<style scoped>
.message {
   color:red;
}
</style>