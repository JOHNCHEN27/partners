import myAxios from "../plugins/myAxios";
import {setCurrentUserState} from "../states/user.ts";

// 获取当前登录用户
export const getCurrentUser = async () => {
    // const currentUser = getCurrentUser();
    // if (currentUser) {
    //     return currentUser;
    // }
    // // 不存在远程获取
    const res =  await myAxios.get('/user/current')
    if(res.code === 0){
        setCurrentUserState(res.data);
        return res.data;
    }
    return null;
}