<template>
  <template v-if="user">
  <van-cell title="昵称" is-link to="/user/edit" :value="user.username"  @click="toEdit('username', '昵称', user.username)"/>
  <van-cell title="账号"  :value="user.userAccount" />
  <van-cell title="头像" is-link to="/user/edit" >
    <img style="height: 48px"  :src="user.avatarUrl" />
  </van-cell>
  <van-cell title="性别" is-link  on :value="user.gender" @click="toEdit('gender', '性别',user.gender)"/>
  <van-cell title="电话" is-link  to="/user/edit" :value="user.phone"  @click="toEdit('phone', '电话',user.phone)"/>
  <van-cell title="邮箱" is-link  to="/user/edit" :value="user.email"  @click="toEdit('email', '邮箱', user.email)"/>
  <van-cell title="星球编号" :value="user.planetCode"/>
  <van-cell title="注册时间" :value="user.createTime"/>
  </template>
</template>

<script setup lang="ts">


import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../services/user.ts"
//
// const user = {
//   id: 1,
//   username: 'lnc',
//   userAccount: 'answer',
//   avatarUrl: '',
//   gender:'男',
//   phone: '12354640012',
//   email: '12321@qq.com',
//   planetCode: '001',
//   createTime: new Date(),
// }

const router = useRouter();

const user = ref();


//在vue实例首次加载之后执行的操作
onMounted( async () =>{
  user.value = await getCurrentUser();

})

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