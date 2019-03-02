<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-card>
        <v-card-title>
          <h1 align="left">删除账户后不可恢复，确认要删除该账户吗？</h1>
        </v-card-title>
        <v-card-actions>
          <v-layout justify-end>
            <v-btn color="error" @click="cancelAccount">确认删除</v-btn>
            <v-btn color="success" @click="$router.push('/customer/home')">取消</v-btn>
          </v-layout>
        </v-card-actions>
      </v-card>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
  </v-layout>
</template>

<script>
export default {
  name: 'AccountCancel',
  methods: {
    cancelAccount: function () {
      this.$ajax({
        url: '/customer/cancel',
        method: 'post'
      }).then(res => {
        this.$router.push('/')
      })
    }
  }
}
</script>

<style scoped>

</style>
