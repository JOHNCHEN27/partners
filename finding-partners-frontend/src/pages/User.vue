<template>
  <van-cell title="昵称" is-link to="/user/edit" :value="user.username" />
  <van-cell title="账号"  :value="user.userAccount" />
  <van-cell title="头像" is-link to="/user/edit" >
    <img style=""  :src="user.avatarUrl" />
  </van-cell>
  <van-cell title="性别" is-link  on :value="user.gender" @click="toEdit('gender', '性别',user.gender)"/>
  <van-cell title="电话" is-link  to="/user/edit" :value="user.phone"  @click="toEdit('phone', '电话',user.phone)"/>
  <van-cell title="邮箱" is-link  to="/user/edit" :value="user.email"/>
  <van-cell title="星球编号" :value="user.planetCode"/>
  <van-cell title="注册时间" :value="user.createTime.toISOString()"/>

</template>

<script setup lang="ts">


import {useRouter} from "vue-router";
// import { ref} from "vue";
// import myAxios from "../plugins/myAxios.ts";
// import qs from "qs";

const user = {
  id: 1,
  username: 'lnc',
  userAccount: 'answer',
  avatarUrl: '',
  gender:'男',
  phone: '12354640012',
  email: '12321@qq.com',
  planetCode: '001',
  createTime: new Date(),
}

const router = useRouter();

// const userList = ref([]);


//在vue实例挂载之后执行的操作
// onMounted( async () => {
//   const userListData = await myAxios.get("/user/search/tags",{
//     paramsSerializer: params => {
//       return qs.stringify(params,{indices:false})
//     }
//   })
//       //响应成功
//       .then(function (response) {
//         console.log('/user/search/tags succeed',response);
//         //showSuccessToast("请求成功")
//         return response.data?.data;
//       })
//       //响应失败
//       .catch(function (error) {
//         console.error('/user/search/tags error', error);
//         // showFailToast("请求失败")
//       })
//   console.log(userListData)
//   if (userListData){
//     userListData.forEach(user => {
//       if (user.tags){
//         user.tags = JSON.parse(user.tags);
//       }
//     })
//     userList.value = userListData;
//   }
// })

const toEdit = (editKey: string,editName: string, currentValue:string) => {
  router.push({
    path: '/user/edit',
    query: {
      editKey,
      editName,
      currentValue,
    }
  })
}

</script>

<style scoped>

</style>