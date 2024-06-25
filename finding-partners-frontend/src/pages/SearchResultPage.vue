<template>
  <van-card
    v-for="user in userList"
    :desc="user.profile"
    :title="`${user.username} `"
    :thumb="user.avatarUrl">

    <template #tags>
      <van-tag plain type="danger" v-for="tag in user.tags" style="margin-right: 8px; margin-top: 8px;">
        {{ tag }}}
      </van-tag>
    </template>

    <template #footer>
      <van-button size="mini">联系我</van-button>
    </template>

  </van-card>
</template>

<script setup >
import {onMounted, ref} from 'vue'
import {useRoute, } from "vue-router";

import qs from 'qs'
import myAxios from "../plugins/myAxios.ts"
//import { showSuccessToast, showFailToast } from '@tsingsu/mui';



const route = useRoute();
// 接受路由传参
const {tags } = route.query

const userList = ref([]);


//在vue实例挂载之后执行的操作
onMounted( async () => {
  const userListData = await myAxios.get("/user/search/tags",{
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
        return response.data?.data;
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


// const mockUser = {
//   id: 1234,
//   username: 'lnc',
//   userAccount: '12345',
//   avatarUrl: 'https://q7.itc.cn/q_70/images03/20240423/6d236fae5c8f44ed9b60d977f32debb7.jpeg',
//   gender: 0,
//   profile: '的撒按时大sadasdsadasddddddddddd grththtyjyujyj',
//   phone: '14123145564',
//   email: '095313@dasda.com',
//   userRole: 0,
//   planetCode: '1234',
//   tags: ['java','c++','emo'],
//   createTime: new Date()
// }


</script>

<style scoped>

</style>