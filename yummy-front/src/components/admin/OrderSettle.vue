<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout justify-center>
        <h2>处理订单</h2>
      </v-layout>
      <v-layout>
        <v-flex>
          <OrderList :orders="orderList" role="admin">
            <v-btn
              slot-scope="{ order }"
              color="primary"
              @click="settleOrder(order.id)"
            >立即处理</v-btn>
          </OrderList>
        </v-flex>
      </v-layout>
      <v-layout>
        <v-btn v-show="morePage" block @click="loadPage">加载下一页</v-btn>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
  </v-layout>
</template>

<script>
import OrderList from '@/components/util/OrderList'
export default {
  name: 'OrderSettle',
  components: {OrderList},
  beforeMount: function () {
    this.loadPage()
  },
  data: function () {
    return {
      orderList: [],
      pageSize: 10,
      morePage: true
    }
  },
  methods: {
    loadPage: function () {
      this.$ajax({
        url: '/admin/list/order/unsettled',
        method: 'get',
        params: {
          'pageStart': this.orderList.length,
          'pageSize': this.pageSize
        }
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          for (let o of res.data) {
            this.orderList.push(o)
          }
          this.morePage = res.data.length === this.pageSize
        }
      })
    },
    removeOrder: function (id) {
      for (let i = 0; i < this.orderList.length; i++) {
        if (this.orderList[i].id === id) {
          this.orderList.splice(i, 1)
          return
        }
      }
    },
    settleOrder: function (id) {
      this.$ajax({
        url: '/admin/settle/' + id,
        method: 'post'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.removeOrder(id)
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
