<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout>
        <v-toolbar>
          <v-select
            label="订单种类"
            v-model="type"
            :items="types"
            @input="typeChanged"
          ></v-select>
        </v-toolbar>
      </v-layout>
      <v-layout>
        <v-flex>
          <OrderList :orders="orderList" role="customer" @order-selected="viewOrderInfo">
            <template slot-scope="{ order }">
              <v-btn
                v-if="type === types[0]"
                color="success"
                @click.stop="toPayment(order)"
              >
                支付
              </v-btn>
              <v-btn
                v-else
                color="success"
                :disabled="order.state !== 3"
                @click.stop="confirmDelivery(order)"
              >
                确认收货
              </v-btn>
            </template>
          </OrderList>
        </v-flex>
      </v-layout>
      <v-layout>
        <v-btn v-if="morePage" block @click="loadPage">加载下一页</v-btn>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
    <v-dialog v-model="orderInfoDialog" width="500">
      <v-card>
        <v-card-text>
          <OrderInfo :info="{id: selectedOid, role: 'customer', load: orderInfoDialog}"></OrderInfo>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="orderInfoDialog = false">关闭</v-btn>
          <v-btn
            color="warning"
            @click.stop="cancelOrder(order)"
          >
            取消订单
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="paymentDialog" width="500">
      <v-card>
        <v-card-text>
          <v-select
            label="支付账户"
            v-model="payment.account"
            :items="payment.accounts"
          ></v-select>
          <v-text-field
            label="密码"
            type="password"
            v-model="payment.pwd"
          ></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-btn @click="paymentDialog = false">取消</v-btn>
          <v-btn color="primary" @click="payOrder">支付</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar top v-model="snackbar.show">
      {{snackbar.msg}}
      <v-btn color="pink" flat @click="snackbar.show = false">关闭</v-btn>
    </v-snackbar>
  </v-layout>
</template>

<script>
import OrderList from '@/components/util/OrderList'
import OrderInfo from '@/components/util/OrderInfo'
export default {
  name: 'UnhandledOrders',
  components: {OrderInfo, OrderList},
  beforeMount: function () {
    this.loadPage()
  },
  data: function () {
    return {
      types: ['待支付', '待送达'],
      type: '待支付',
      pageSize: 10,
      orderList: [],
      orderInfoDialog: false,
      selectedOid: 0,
      morePage: true,
      paymentDialog: false,
      payment: {
        accounts: [],
        account: '',
        pwd: ''
      },
      snackbar: {
        show: false,
        msg: ''
      }
    }
  },
  methods: {
    loadPage: function () {
      this.$ajax({
        url: '/customer/order/list/' + (this.type === this.types[0] ? 'placed' : 'waiting'),
        method: 'get',
        params: {
          pageStart: this.orderList.length,
          pageSize: this.pageSize
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
    typeChanged: function () {
      this.orderList.splice(0, this.orderList.length)
      this.loadPage()
    },
    viewOrderInfo: function (order) {
      this.selectedOid = order.id
      this.orderInfoDialog = true
    },
    toPayment: function (order) {
      this.selectedOid = order.id
      this.$ajax({
        url: '/customer/info/payment/get',
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          for (let p of res.data) {
            this.payment.accounts.push(p.pid)
          }
          this.payment.pwd = ''
          this.paymentDialog = true
        }
      })
    },
    payOrder: function () {
      this.$ajax({
        url: '/customer/order/pay/' + this.selectedOid,
        method: 'post',
        params: {
          'pid': this.payment.account,
          'pwd': this.payment.pwd
        }
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else if (res.data.result === 0) {
          this.paymentDialog = false
          this.snackbar.msg = '支付成功'
          this.snackbar.show = true
          this.removeOrder(this.selectedOid)
        } else if (res.data.result === 1) {
          this.snackbar.msg = '支付账户的密码错误'
          this.snackbar.show = true
        } else if (res.data.result === 2) {
          this.snackbar.msg = '该账户余额不足'
          this.snackbar.show = true
        } else {
          this.snackbar.msg = '服务器未知错误'
          this.snackbar.show = true
        }
      })
    },
    confirmDelivery: function (order) {
      this.$ajax({
        url: '/customer/order/complete/' + order.id,
        method: 'post'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.removeOrder(order.id)
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
    cancelOrder: function (order) {
      this.$ajax({
        url: '/customer/order/cancel/' + order.id,
        method: 'post'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else if (res.data.result === 0) {
          this.snackbar.msg = '订单正在取消。'
          this.snackbar.show = true
          this.removeOrder(order.id)
        } else if (res.data.result === 1) {
          this.snackbar.msg = '该订单无法取消。'
          this.snackbar.show = true
        } else {
          this.snackbar.msg = '服务器未知错误。'
          this.snackbar.show = true
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
