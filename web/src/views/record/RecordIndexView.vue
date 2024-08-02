<script setup>
    import ContentField from '@/components/ContentField.vue'
    import { useUserStore } from '@/store/user';
    import $ from 'jquery';
    import { ref } from 'vue';

    const userStore = useUserStore();
  
    //表示当前页面
    let current_page = 1;
    let records = ref([]);
    let total_records = 0;
    //当前所见的页码
    let pages = ref([]);

    console.log(total_records);

    const click_page = page => {
        if (page === -2) page = current_page - 1;
        else if (page === -1) page = current_page + 1;
        //最大页数
        let max_pages = parseInt(Math.ceil(total_records / 10));

        //如果页数在合理范围内，则进行更新
        if (page >= 1 && page <= max_pages) {
            pull_page(page);
        }
    }

    //更新页数
    const update_pages = () => {
        let max_pages = parseInt(Math.ceil(total_records / 10));
        let new_pages = [];

        for (let i = current_page - 2; i <= current_page + 2; i ++ ) {
            //-2 -1 0 1 2 一共有5个页码
            if (i >= 1 && i <= max_pages) {
                new_pages.push({
                    number:i,
                    is_active: i === current_page ? "active" : "",
                });
            }
        }
        //更新页码
        pages.value = new_pages;

    }
    //查询某一个页面的内容
    const pull_page = page => {
        current_page = page;
        $.ajax({
         url: "http://127.0.0.1:3000/record/getList/",
         type: "get",
         headers: {
            Authorization: "Bearer " + userStore.token,
         },
         data:{
            page:page
         },
         success(resp){
            //把resp中的值取出
            console.log(resp);
            records.value = resp.records;
            total_records = resp.records_count;
            update_pages();
         },
         error(resp){
            console.log(resp);
         }
      });
    }
    pull_page(current_page);

    

</script>

<template>
    <ContentField>
        <table class="table table-hover" style="text-align: center;">
            <thead>
                <tr>
                    <th scope="col">A</th>
                    <th scope="col">B</th>
                    <th scope="col">对战结果</th>
                    <th scope="col">对战时间</th>
                </tr>
            </thead>
            <tbody>
            <tr v-for="record in records" :key="record.record.id">
                <td>
                    <img :src="record.a_photo" alt="" class="record-user-photo">
                    &nbsp;
                    <span class="record-user-username">{{record.a_username}}</span>
                </td>
                <td>
                    <img :src="record.b_photo" alt="" class="record-user-photo">
                    &nbsp;
                    <span class="record-user-username">{{record.b_username}}</span>
                </td>
                <td>
                    {{record.result}}
                </td>
                <td>
                    {{record.record.createTime}}
                </td>
            </tr>
            </tbody>
        </table>

        <nav aria-label="Page navigation example" style="float: right;">
            <ul class="pagination">
                <li class="page-item"><a class="page-link" href="#" @click="click_page(-2)">前一页</a></li>
                <li :class="'page-item '+page.is_active" v-for="page in pages" :key="page.number" @click="click_page(page.number)">
                    <a class="page-link" href="#">{{page.number}}</a>
                </li>
             
                <li class="page-item">
                    <a class="page-link" href="#" @click="click_page(-1)">后一页</a>
                </li>
            </ul>
        </nav>
    </ContentField>
</template>
<style scoped>

img.record-user-photo {
    width: 4vh;
    border-radius: 50%;

}
</style>