<template>
  <div>
    <v-layout xs12>
      <InitHeader></InitHeader>
    </v-layout>
    <v-layout xs12 align-center justify-center>
      <h1>注册为顾客</h1>
    </v-layout>
    <v-layout xs12>
      <v-flex xs1 sm2 md3></v-flex>
      <v-flex>
        <v-stepper v-model="step" vertical>
          <v-stepper-step :complete="step > 1" step="1">
            填写注册信息
          </v-stepper-step>
          <v-stepper-content step="1">
            <v-form ref="signUpForm" lazy-validation>
              <v-text-field label="电子邮件地址"
                            :rules="emailRules"
                            :counter="40"
                            v-model="email"
              ></v-text-field>
              <v-text-field label="密码"
                            type="password"
                            :rules="pwdRules"
                            :counter="20"
                            v-model="pwd"
              ></v-text-field>
              <v-text-field label="请再次输入密码"
                            type="password"
                            :rules="pwdCheckRules"
                            :counter="20"
              ></v-text-field>
              <v-text-field label="用户名"
                            :counter="20"
                            :rules="nameRules"
                            v-model="name"
              ></v-text-field>
              <v-text-field label="联系电话"
                            :rules="phoneRules"
                            :counter="11"
                            v-model="phone"
              ></v-text-field>
              <v-layout row wrap xs12 justify-end>
                <v-btn color="success" large @click="signUp">提交</v-btn>
              </v-layout>
              <v-snackbar v-model="snackbar" top>
                {{errMsg}}
                <v-btn color="pink" flat @click="snackbar = ''">关闭</v-btn>
              </v-snackbar>
            </v-form>
          </v-stepper-content>
          <v-stepper-step step="2">
            验证电子邮箱
          </v-stepper-step>
          <v-stepper-content step="2">
            <p align="left">
              我们已经向您的邮箱{{email}}发送了一封邮件，请打开您的电子邮箱，查看最新邮件，
              点击其中的链接来验证邮箱，注册即可成功。
            </p>
          </v-stepper-content>
        </v-stepper>
      </v-flex>
      <v-flex xs1 sm2 md3></v-flex>
    </v-layout>
  </div>
</template>

<script>
import InitHeader from '@/components/InitHeader'
export default {
  name: 'SignUp',
  components: {InitHeader},
  data: function () {
    return {
      email: '',
      pwd: '',
      name: '',
      phone: '',
      emailRules: [
        email => !!email || '请填写电子邮件地址',
        email => /.+@.+/.test(email) || '电子邮件地址格式不正确',
        email => (email && email.length <= 40) || '电子邮件地址不得超过40个字符'
      ],
      pwdRules: [
        pwd => !!pwd || '请设置登录密码',
        pwd => (pwd && pwd.length >= 6 && pwd.length <= 20) || '密码长度应为6-20个字符'
      ],
      pwdCheckRules: [
        secondPwd => secondPwd === this.pwd || '两次输入的密码不一致'
      ],
      nameRules: [
        name => !!name || '请输入用户名',
        name => (name && name.length <= 20) || '用户名不得超过20个字符'
      ],
      phoneRules: [
        phone => !!phone || '请输入联系电话',
        phone => (phone && /[0-9]*/.test(phone) && phone.length === 11) || '联系电话格式不正确'
      ],
      errMsg: '',
      snackbar: false,
      step: 1
    }
  },
  methods: {
    signUp: function () {
      if (this.$refs.signUpForm.validate()) {
        this.$ajax({
          url: '/customer/sign-up',
          method: 'post',
          params: {
            'email': this.email,
            'pwd': this.pwd,
            'name': this.name,
            'phone': this.phone
          }
        }).then(res => {
          switch (res.data.result) {
            case 0:
              this.step = 2
              this.errMsg = ''
              this.snackbar = false
              break
            case 1:
              this.errMsg = '该邮箱已注册，不可重复注册'
              this.snackbar = true
              break
            default:
              this.errMsg = '未知错误'
              this.snackbar = true
              break
          }
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
