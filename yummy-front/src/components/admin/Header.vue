<template>
  <HeaderBase>
    <v-btn flat @click="$router.push('/admin')">
      <h3>处理订单</h3>
    </v-btn>
    <v-btn flat @click="$router.push('/admin/restaurants')">
      <h3>审核餐馆</h3>
    </v-btn>
    <v-btn flat @click="$router.push('/admin/statistics')">
      <h3>统计数据</h3>
    </v-btn>
    <v-btn flat @click="signOut">
      <h3>退出</h3>
    </v-btn>
  </HeaderBase>
</template>

<script>
import HeaderBase from '@/components/util/HeaderBase'
export default {
  name: 'AdminHeader',
  components: {HeaderBase},
  methods: {
    signOut: function () {
      this.$ajax({
        url: '/admin/sign-out',
        method: 'post'
      }).then(this.$router.push('/'))
    }
  }
}
</script>

<style scoped>

</style>
