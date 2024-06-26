<template>
  <user-card-list :user-list="userList"/>
  <van-empty v-if="!userList || userList.length < 1" description="数据为空"/>
</template>

<script setup >
import {onMounted, ref} from 'vue'
import {useRoute, } from "vue-router";

import qs from 'qs'
import myAxios from "../plugins/myAxios.ts"
import UserCardList from "../components/UserCardList.vue";
//import { showSuccessToast, showFailToast } from '@tsingsu/mui';



const route = useRoute();
// 接受路由传参
const {tags } = route.query

const userList = ref([]);


//在vue实例挂载之后执行的操作
onMounted( async () => {
  const userListData = await myAxios.get("/user/recommend",{
    params: {
      tagNameList: tags
    },
    paramsSerializer: params => {
      return qs.stringify(params,{indices:false})
    }
  })
      //响应成功
      .then(function (response) {
        console.log('/user/search/tags succeed',response);
        //showSuccessToast("请求成功")
        return response?.data;
      })
      //响应失败
      .catch(function (error) {
        console.error('/user/search/tags error', error);
        // showFailToast("请求失败")
      })
  console.log(userListData)
  if (userListData){
    userListData.forEach(user => {
      if (user.tags){
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
})
</script>
<style scoped>
</style>