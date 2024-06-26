<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
        v-model="editUser.currentValue"
        :name="editUser.editKey"
        :label="editUser.editName"
        :placeholder="`请输入${editUser.editName}`"
        />
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-cell-group>
  </van-form>
</template>

<script setup lang="ts">


import {useRoute} from "vue-router";
import { ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {getCurrentUser} from "../services/user.ts";

const route = useRoute();
const editUser = ref({
  editKey: route.query.editKey,
  editName: route.query.editName,
  currentValue: route.query.currentValue
})



const onSubmit = async () => {
  const currentUser = await getCurrentUser();
  if (!currentUser){
    return;
  }
  //todo: 将editKey、editName、currentValue提交到后台
  const res = await myAxios.post("/user/update",{
    'id': currentUser.id,
    [editUser.value.editKey as string]: editUser.value.currentValue,
  })
  console.log(res);
}


</script>

<style scoped>

</style>