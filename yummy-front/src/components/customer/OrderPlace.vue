<template>
  <v-layout xs12>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout>
        <OrderInfo :order="orderInfo"></OrderInfo>
      </v-layout>
      <v-layout justify-end>
        <v-btn color="success" lg @click="placeOrder">立即下单</v-btn>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
    <v-dialog v-model="dialog" width="300">
      <v-card>
        <v-card-title><h3>下单成功。</h3></v-card-title>
        <v-card-actions>
          <v-btn color="primary" @click="$router.push('/customer/home')">返回主页</v-btn>
          <v-btn color="primary" @click="$router.push('/customer/home/orders/unhandled')">去支付</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar v-model="snackbar.show">
      {{snackbar.msg}}
      <v-btn color="pink" flat @click="snackbar.show = false">关闭</v-btn>
    </v-snackbar>
  </v-layout>
</template>

<script>
import OrderInfo from '@/components/util/OrderInfo'
export default {
  name: 'OrderPlace',
  components: {OrderInfo},
  beforeMount: function () {
    this.$ajax({
      url: '/customer/info/get',
      method: 'get'
    }).then(res => {
      if (res.data['AccessDenied']) {
        this.$router.push('/')
      } else {
        let customer = {
          name: res.data.name,
          phone: res.data.phone
        }
        this.$set(this.orderInfo, 'customer', customer)
      }
    })
    this.loadInfo()
  },
  data: function () {
    return {
      orderInfo: {
        customer: {
          name: '',
          phone: ''
        },
        restaurant: {
          id: '',
          name: '',
          address: '',
          phone: ''
        },
        destination: '',
        items: [],
        cost: 0
      },
      dialog: false,
      snackbar: {
        show: false,
        msg: ''
      }
    }
  },
  methods: {
    loadInfo: function () {
      let info = JSON.parse(window.localStorage.getItem('orderInfo'))
      this.orderInfo.restaurant.id = info.restaurant.id
      this.orderInfo.restaurant.name = info.restaurant.name
      this.orderInfo.restaurant.address = info.restaurant.address
      this.orderInfo.restaurant.phone = info.restaurant.phone
      this.orderInfo.destination = window.localStorage.getItem('destination')
      for (let item of info.items) {
        this.orderInfo.items.push(item)
      }
      this.orderInfo.cost = info.cost
      window.localStorage.removeItem('orderInfo')
    },
    placeOrder: function () {
      let items = {}
      for (let item of this.orderInfo.items) {
        items[item.mid] = item.num
      }
      this.$ajax({
        url: '/customer/order/place',
        method: 'post',
        params: {
          'rid': this.orderInfo.restaurant.id
        },
        data: items
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else if (res.data.result === 0) {
          this.dialog = true
          this.snackbar.show = false
        } else if (res.data.result === 1) {
          this.snackbar.msg = '部分菜品剩余数量不足，请重新下单。'
          this.snackbar.show = true
        } else {
          this.snackbar.msg = '服务器未知错误'
          this.snackbar.show = true
        }
      })
    }
  }
}
</script>

<style>
</style>
