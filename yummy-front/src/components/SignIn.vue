<template>
  <div>
    <v-layout row xs12>
      <InitHeader></InitHeader>
    </v-layout>
    <v-layout row wrap justify-center>
      <v-flex xs1 sm2 md3></v-flex>
      <v-flex>
        <v-card class="elevation-10">
          <v-card-title primary-title>
            <v-layout row wrap justify-center>
              <h1>登录</h1>
            </v-layout>
          </v-card-title>
          <v-divider/>
          <v-card-text>
            <v-form ref="signInForm" lazy-validation>
              <v-layout row wrap justify-start>
                <v-select :items="userRoles" label="请选择身份" v-model="role"></v-select>
              </v-layout>
              <v-layout v-if="role !== userRoles[2]" row wrap justify-start>
                <v-text-field
                  :label="role === userRoles[0] ? '电子邮件' : 'ID'"
                  :rules="role === userRoles[0] ? emailRules : idRules"
                  v-model="username"
                ></v-text-field>
              </v-layout>
              <v-layout row wrap justify-start>
                <v-text-field
                  label="密码"
                  :rules="pwdRules"
                  type="password"
                  v-model="pwd"
                ></v-text-field>
              </v-layout>
              <v-layout row wrap align-center justify-end>
                <v-flex p-x-1>
                  <router-link to="/customer/sign-up">&lt; 注册为顾客</router-link>
                </v-flex>
                <v-flex p-x-1>
                  <router-link to="/restaurant/sign-up">&lt; 注册为商家</router-link>
                </v-flex>
                <v-flex p-x-1>
                  <v-btn color="success" large @click="signIn">立即登录</v-btn>
                </v-flex>
              </v-layout>
            </v-form>
          </v-card-text>
          <v-snackbar v-model="snackbar" top>
            {{errMsg}}
            <v-btn color="pink" flat @click="snackbar = ''">关闭</v-btn>
          </v-snackbar>
        </v-card>
      </v-flex>
      <v-flex xs1 sm2 md3></v-flex>
    </v-layout>
  </div>
</template>

<script>
import InitHeader from '@/components/InitHeader'
export default {
  name: 'SignIn',
  components: {InitHeader},
  data: function () {
    return {
      userRoles: ['顾客', '商家', '管理员'],
      role: '顾客',
      username: '',
      pwd: '',
      emailRules: [
        email => !!email || '请输入电子邮件地址',
        email => /.+@.+/.test(email) || '电子邮件地址格式不正确'
      ],
      idRules: [
        id => !!id || '请输入商家ID'
      ],
      pwdRules: [
        pwd => !!pwd || '请输入密码'
      ],
      errMsg: '',
      snackbar: false
    }
  },
  methods: {
    signIn: function () {
      if (this.$refs.signInForm.validate()) {
        switch (this.role) {
          case this.userRoles[0]:
            this.customerSignIn()
            break
          case this.userRoles[1]:
            this.restaurantSignIn()
            break
          case this.userRoles[2]:
            this.adminSignIn()
            break
          default:
            this.errMsg = 'Action not supported.'
            break
        }
      }
    },
    customerSignIn: function () {
      this.$ajax({
        url: '/customer/sign-in',
        method: 'post',
        params: {
          email: this.username,
          pwd: this.pwd
        }
      }).then((res) => {
        switch (res.data.result) {
          case 0:
            this.$router.push('/customer/home')
            this.errMsg = ''
            this.snackbar = false
            break
          case 1:
            this.errMsg = '帐号不存在'
            this.snackbar = true
            break
          case 2:
            this.errMsg = '密码错误'
            this.snackbar = true
            break
          default:
            this.errMsg = '未知错误'
            this.snackbar = true
            break
        }
      })
    },
    restaurantSignIn: function () {
      this.$ajax({
        url: '/restaurant/sign-in',
        method: 'post',
        params: {
          id: this.username,
          pwd: this.pwd
        }
      }).then((res) => {
        switch (res.data.result) {
          case 0:
            this.$router.push('/restaurant/home')
            this.errMsg = ''
            this.snackbar = false
            break
          case 1:
            this.errMsg = '帐号不存在'
            this.snackbar = true
            break
          case 2:
            this.errMsg = '密码错误'
            this.snackbar = true
            break
          default:
            this.errMsg = '未知错误'
            this.snackbar = true
            break
        }
      })
    },
    adminSignIn: function () {
      this.$ajax({
        url: '/admin/sign-in',
        method: 'post',
        params: {
          pwd: this.pwd
        }
      }).then((res) => {
        switch (res.data.result) {
          case 0:
            this.$router.push('/admin')
            this.errMsg = ''
            this.snackbar = false
            break
          case 1:
            this.errMsg = '密码错误'
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
</script>

<style scoped>

</style>
