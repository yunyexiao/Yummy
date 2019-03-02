<template>
  <div>
    <v-layout xs12>
      <InitHeader></InitHeader>
    </v-layout>
    <v-layout xs12 align-center justify-center>
                                                <h1>注册为商家</h1>
                                                </v-layout>
    <v-layout>
               <v-flex xs1 sm2 md3></v-flex>
               <v-flex>
               <v-stepper v-model="step" vertical>
               <v-stepper-step :complete="step > 1" step="1">
               填写注册信息
               </v-stepper-step>
               <v-stepper-content step="1">
               <v-form ref="signUpForm" lazy-validation>
               <v-text-field label="餐馆名"
               v-model="restaurant.name"
               :rules="inputRules.nameRules"
               :counter="40"
               ></v-text-field>
               <v-text-field label="密码"
               type="password"
               v-model="restaurant.pwd"
               :rules="inputRules.pwdRules"
               :counter="20"
               ></v-text-field>
               <v-text-field label="再次输入密码"
               type="password"
               :rules="inputRules.secondPwdRules"
               :counter="20"
               ></v-text-field>
               <v-text-field label="餐馆类型"
               v-model="restaurant.type"
               :rules="inputRules.typeRules"
               :counter="20"
               ></v-text-field>
               <v-textarea label="餐馆介绍"
               v-model="restaurant.description"
               :rules="inputRules.descriptionRules"
               :counter="100"
               ></v-textarea>
               <v-text-field label="餐馆地址"
               v-model="restaurant.address"
               :rules="inputRules.addressRules"
               :counter="50"
               ></v-text-field>
               <v-text-field label="餐馆所在纬度"
               type="number"
               v-model="restaurant.latitude"
               :rules="inputRules.latitudeRules"
               ></v-text-field>
               <v-text-field label="餐馆所在经度"
               type="number"
               v-model="restaurant.longitude"
               :rules="inputRules.longitudeRules"
               ></v-text-field>
               <v-text-field label="餐馆客服电话"
               v-model="restaurant.phone"
               :rules="inputRules.phoneRules"
               :counter="11"
               ></v-text-field>
               <v-btn color="success" @click="signUp">提交</v-btn>
               </v-form>
               </v-stepper-content>
               <v-stepper-step :complete="step > 2" step="2">
               获取餐馆编号(ID)
               </v-stepper-step>
               <v-stepper-content step="2">
               <p align="left">
               该餐馆的编号为{{restaurant.id}}，该编号用于登录和唯一识别您的餐馆，请牢记。
               </p>
               <v-btn color="success" @click="step++">我记住了</v-btn>
               </v-stepper-content>
               <v-stepper-step step="3">
               等待人工审核
               </v-stepper-step>
               <v-stepper-content step="3">
               <p align="left">
               等管理员审核通过您的餐馆后，您的餐馆才能正式上线。您目前可以使用该帐号
               进行登录操作，但是无法发布任何信息。
               </p>
               <router-link to="/">去登录</router-link>
               </v-stepper-content>
               </v-stepper>
               </v-flex>
               <v-flex xs1 sm2 md3></v-flex>
               </v-layout>
    <v-snackbar v-model="snackbar.show" top>
                                             {{snackbar.msg}}
                                             <v-btn color="pink" flat @click="snackbar.show = false">关闭</v-btn>
                                             </v-snackbar>
  </div>
</template>

<script>
import InitHeader from '@/components/InitHeader'
import inputRules from '@/components/restaurant/input-rules'
export default {
  name: 'SignUp',
  components: {InitHeader},
  data: function () {
    return {
      step: 1,
      restaurant: {
        id: '',
        name: '',
        pwd: '',
        type: '',
        description: '',
        address: '',
        latitude: '',
        longitude: '',
        phone: ''
      },
      inputRules: {
        secondPwdRules: [
          secondPwd => !!secondPwd || '请再次输入密码',
          secondPwd => (secondPwd && secondPwd === this.restaurant.pwd) || '两次输入的密码不一致'
        ],
        ...inputRules
      },
      snackbar: {
        show: false,
        msg: ''
      }
    }
  },
  methods: {
    signUp: function () {
      this.$ajax({
        url: '/restaurant/sign-up',
        method: 'post',
        data: this.restaurant
      }).then(res => {
        if (res.data.id) {
          this.restaurant.id = res.data.id
          this.step = 2
        } else {
          this.snackbar.msg = '服务器错误，请重试'
          this.snackbar.show = true
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
