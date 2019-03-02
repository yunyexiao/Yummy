<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-card>
        <v-card-title>
          <h2>管理个人基础信息</h2>
        </v-card-title>
        <v-card-text>
          <v-text-field
            label="姓名"
            v-model="name"
            :disabled="!editable"
          ></v-text-field>
          <v-text-field
            label="电子邮件地址"
            disabled
            v-model="email"
          ></v-text-field>
          <v-text-field
            label="联系电话"
            :disabled="!editable"
            v-model="phone"
          ></v-text-field>
          <v-text-field
            type="number"
            disabled
            label="等级"
            v-model="lvl"
          ></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="editable = !editable">{{editable ? '取消' : '编辑'}}</v-btn>
          <v-btn color="success" v-show="editable" @click="updateInfo">提交</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
  </v-layout>
</template>

<script>
export default {
  name: 'InfoManage',
  beforeMount: function () {
    this.loadInfo()
  },
  data: function () {
    return {
      email: '',
      name: '',
      phone: '',
      lvl: 0,
      editable: false
    }
  },
  methods: {
    updateInfo: function () {
      this.$ajax({
        url: '/customer/info/name/modify',
        method: 'post',
        params: {
          name: this.name
        }
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        }
      })
      this.$ajax({
        url: '/customer/info/phone/modify',
        method: 'post',
        params: {
          phone: this.phone
        }
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        }
      })
      this.editable = false
    },
    loadInfo: function () {
      this.$ajax({
        url: '/customer/info/get',
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.email = res.data.email
          this.name = res.data.name
          this.phone = res.data.phone
          this.lvl = res.data.level
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
