<template>
  <div>
    <v-layout align-center justify-center v-if="status === 0">
                                                               <h3>
                                                               请稍等，Yummy平台正在验证您的电子邮箱。
                                                               </h3>
                                                               </v-layout>
    <v-layout align-center justify-center v-else-if="status === 1">
                                                                    <h1>
                                                                    恭喜你，电子邮箱验证成功，立即
                                                                    <router-link to="/">登录</router-link>
                                                                    </h1>
                                                                    </v-layout>
    <v-layout align-center justify-center v-else-if="status === 2">
                                                                    <h3>
                                                                    抱歉，用于验证电子邮件地址的密钥已失效。
                                                                    </h3>
                                                                    </v-layout>
    <v-layout align-center justify-center v-else-if="status === 3">
                                                                    <h3>
                                                                    抱歉，用于验证电子邮件的密钥错误，请重试。
                                                                    </h3>
                                                                    </v-layout>
    <v-layout align-center justify-center v-else>
                                                  <h3>
                                                  未知错误，请重试。
                                                  </h3>
                                                  </v-layout>
  </div>
</template>

<script>
export default {
  name: 'EmailVerify',
  data: function () {
    return {
      /**
       * status: 0 for waiting,
       *         1 for success,
       *         2 for already confirmed,
       *         3 for email key not match
       */
      status: 0
    }
  },
  beforeMount () {
    this.$ajax({
      url: '/customer/verify-email',
      method: 'get',
      params: {
        'email': this.$route.query.email,
        'key': this.$route.query.key
      }
    }).then(res => {
      switch (res.data.result) {
        case 0:
          this.status = 1
          break
        case 1:
          this.status = 2
          break
        case 2:
          this.status = 3
          break
        default:
          this.status = 4
          break
      }
    }).catch(err => {
      console.log(err)
      this.status = 4
    })
  }
}
</script>

<style scoped>

</style>
