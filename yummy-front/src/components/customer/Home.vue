<template>
  <div>
    <CustomerHeader
      :name="info.name"
      :lvl="info.level"
    ></CustomerHeader>
    <router-view></router-view>
  </div>
</template>

<script>
import CustomerHeader from '@/components/customer/Header'
export default {
  name: 'Home',
  components: {CustomerHeader},
  beforeMount: function () {
    this.$ajax({
      url: '/customer/info/get',
      method: 'get'
    }).then(res => {
      if (res.data['AccessDenied']) {
        this.$router.push('/')
      } else {
        this.info.name = res.data.name
        this.info.level = res.data.level
      }
    })
  },
  data: function () {
    return {
      info: {
        name: '',
        level: 0
      }
    }
  }
}
</script>

<style scoped>

</style>
