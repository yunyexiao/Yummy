<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout>
        <v-select
          label="订单类型"
          size="5"
          v-model="type"
          :items="types"
          @input="reloadOrders"
        ></v-select>
        <v-flex xs4></v-flex>
        <v-flex xs6></v-flex>
      </v-layout>
      <v-layout justify-center>
        <v-flex v-show="orderList.length > 0">
          <OrderList
            :orders="orderList"
            role="restaurant"
            @order-selected="showOrderInfo"
          >
            <template slot-scope="{ order }">
              <v-btn
                v-show="type === types[0]"
                color="primary"
                @click.stop="acceptOrder(order)"
              >接单</v-btn>
              <v-btn
                v-show="type === types[1]"
                color="primary"
                @click.stop="deliverOrder(order)"
              >送单</v-btn>
            </template>
          </OrderList>
        </v-flex>
        <h2 v-show="orderList.length === 0">当前没有{{type}}的订单</h2>
      </v-layout>
      <v-layout>
        <v-btn v-show="morePage" block @click="loadPage">加载下一页</v-btn>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
    <v-dialog v-model="detailDialog" width="500">
      <v-card>
        <v-card-text>
          <OrderInfo :info="{id: selectedOid, role: 'restaurant', load: detailDialog}"></OrderInfo>
        </v-card-text>
        <v-card-actions>
          <v-btn color="info" @click="detailDialog = false">关闭</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
      types: ['未接单', '未送餐', '正在派送'],
      type: '未接单',
      orderList: [],
      pageSize: 10,
      morePage: true,
      selectedOid: 0,
      detailDialog: false
    }
  },
  methods: {
    loadPage: function () {
      let urls = [
        '/restaurant/order/list/new',
        '/restaurant/order/list/undelivered',
        '/restaurant/order/list/delivery'
      ]
      let index = this.types.indexOf(this.type)
      if (index < 0 || index >= urls.length) {
        console.log('order type invalid: ' + this.type)
        return
      }
      this.$ajax({
        url: urls[index],
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
    reloadOrders: function () {
      this.orderList.splice(0, this.orderList.length)
      this.loadPage()
    },
    showOrderInfo: function (order) {
      this.selectedOid = order.id
      this.detailDialog = true
    },
    acceptOrder: function (order) {
      this.$ajax({
        url: '/restaurant/order/accept/' + order.id,
        method: 'post'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.removeOrder(order.id)
        }
      })
    },
    deliverOrder: function (order) {
      this.$ajax({
        url: '/restaurant/order/deliver/' + order.id,
        method: 'post'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.removeOrder(order.id)
        }
      })
    },
    removeOrder: function (oid) {
      for (let i = 0; i < this.orderList.length; i++) {
        if (this.orderList[i].id === oid) {
          this.orderList.splice(i, 1)
          return
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
