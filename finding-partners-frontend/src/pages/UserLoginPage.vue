<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="userAccount"
          name="userAccount"
          label="账号"
          placeholder="请输入账号"
          :rules="[{ required: true, message: '请填写用户名' }]"
      />
      <van-field
          v-model="userPassword"
          type="password"
          name="userPassword"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请填写密码' }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>

</template>

<script lang="ts" setup>

import {ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {useRouter} from "vue-router";

const router = useRouter();

const userAccount = ref('');
const userPassword = ref('');
const onSubmit = async () => {
 const res = await myAxios.post("/user/login", {
   userAccount: userAccount.value,
   userPassword: userPassword.value,
  })
  console.log('res = ', res);
 if(res.code === 0 && res.data){
   router.replace('/');
 } else {
   console.log("登录失败")
 }
};



</script>

<style scoped>

</style>