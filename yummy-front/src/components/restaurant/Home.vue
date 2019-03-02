<template>
  <div>
    <RestaurantHeader :id="id"></RestaurantHeader>
    <v-alert :value="!valid" type="warning">
      <h3>您的餐馆尚未通过审核，无法发布任何信息。</h3>
    </v-alert>
    <router-view></router-view>
  </div>
</template>

<script>
import RestaurantHeader from '@/components/restaurant/Header'
export default {
  name: 'RestaurantHome',
  components: {RestaurantHeader},
  beforeMount: function () {
    this.$ajax({
      url: '/restaurant/info/get',
      method: 'get'
    }).then(res => {
      if (res.data['AccessDenied']) {
        this.$router.push('/')
      } else {
        this.id = res.data.id
        this.valid = res.data.valid
      }
    })
  },
  data: function () {
    return {
      id: '',
      valid: 0
    }
  }
}
</script>

<style scoped>

</style>
