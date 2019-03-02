<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout justify-center>
        <h2>平台统计信息</h2>
      </v-layout>
      <v-layout>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
  </v-layout>
</template>

<script>
export default {
  name: 'Statistics',
  beforeMount: function () {
    this.loadStatistics()
  },
  data: function () {
    return {
      dateRage: {
        start: new Date().toISOString().substr(0, 10),
        end: new Date().toISOString().substr(0, 10)
      },
      statistics: {
        customerCount: 0,
        restaurantCount: 0,
        finance: {}
      }
    }
  },
  methods: {
    loadStatistics: function () {
      this.$ajax({
        url: '/admin/statistics',
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.statistics.customerCount = res.data.customerCount
          this.statistics.restaurantCount = res.data.restaurantCount
        }
      })
      this.$ajax({
        url: '/admin/finance',
        method: 'get',
        params: this.dateRage
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          Object.keys(this.statistics.finance).forEach(
            month => this.$delete(this.statistics.finance, month)
          )
          Object.keys(res.data).forEach(
            month => this.$set(this.statistics.finance, month, res.data[month])
          )
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
