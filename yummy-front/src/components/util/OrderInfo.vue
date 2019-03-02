<template>
  <v-flex>
    <v-layout justify-space-around v-if="validOrder.id">
      <h2>订单号：{{validOrder.id}}</h2>
      <h3>当前状态：{{orderState}}</h3>
    </v-layout>
    <v-layout>
      <v-flex>
        <p align="left">顾客：{{validOrder.customer.name}}</p>
        <p align="left">顾客联系电话：{{validOrder.customer.phone}}</p>
        <p align="left">餐馆编号：{{validOrder.restaurant.id}}</p>
        <p align="left">餐馆名称：{{validOrder.restaurant.name}}</p>
        <p align="left">餐馆地址：{{validOrder.restaurant.address}}</p>
        <p align="left">餐馆联系电话：{{validOrder.restaurant.phone}}</p>
        <p align="left">送餐地址：{{validOrder.destination}}</p>
        <p align="left" v-if="validOrder.placeTime">下单时间：{{validOrder.placeTime}}</p>
        <p align="left" v-if="validOrder.completeTime">送达时间：{{validOrder.completeTime}}</p>
        <p align="left" v-if="validOrder.cancelTime">取消时间：{{validOrder.cancelTime}}</p>
      </v-flex>
    </v-layout>
    <v-layout>
      <v-expansion-panel expand>
        <v-expansion-panel-content>
          <h3 slot="header">订单内容</h3>
          <v-card>
            <v-card-text>
              <p v-for="item in validOrder.items" :key="item.mid">
                {{item.name}} * {{item.num}}
              </p>
            </v-card-text>
          </v-card>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-layout>
    <v-layout justify-end>
      <h3>合计：{{validOrder.cost.toFixed(1)}}元</h3>
    </v-layout>
  </v-flex>
</template>

<script>
export default {
  name: 'OrderInfo',
  props: {
    info: Object, // need id(Number), role(String), load(Boolean) props
    order: Object
  },
  watch: {
    info: function (newInfo, oldInfo) {
      if (!newInfo.load) {
        return
      }
      switch (newInfo.role) {
        case 'customer':
          this.$ajax({
            url: '/customer/order/get/' + newInfo.id,
            method: 'get'
          }).then(res => {
            if (res.data.AccessDenied) {
              this.$router.push('/')
            } else {
              this.setInnerOrder(res.data)
            }
          })
          break
        case 'restaurant':
          this.$ajax({
            url: '/restaurant/order/get/' + newInfo.id,
            method: 'get'
          }).then(res => {
            if (res.data.AccessDenied) {
              this.$router.push('/')
            } else {
              this.setInnerOrder(res.data)
            }
          })
          break
      }
    }
  },
  data: function () {
    return {
      innerOrder: {
        id: 0,
        customer: {
          email: '',
          name: '',
          phone: ''
        },
        restaurant: {
          id: '',
          name: '',
          address: '',
          phone: ''
        },
        items: [],
        destination: '',
        state: 0,
        cost: 0,
        placeTime: '',
        completeTime: '',
        cancelTime: ''
      },
      states: ['待支付', '已支付', '已接单', '送餐中', '已送达', '已处理', '取消中', '已取消']
    }
  },
  computed: {
    validOrder: function () {
      if (this.order) {
        console.log('order')
        return this.order
      } else {
        return this.innerOrder
      }
    },
    orderState: function () {
      return this.states[this.validOrder.state]
    }
  },
  methods: {
    setInnerOrder: function (data) {
      this.innerOrder.id = data.id
      this.innerOrder.customer.email = data.customer.email
      this.innerOrder.customer.name = data.customer.name
      this.innerOrder.customer.phone = data.customer.phone
      this.innerOrder.restaurant.id = data.restaurant.id
      this.innerOrder.restaurant.name = data.restaurant.name
      this.innerOrder.restaurant.address = data.restaurant.address
      this.innerOrder.restaurant.phone = data.restaurant.phone
      this.innerOrder.destination = data.destination
      this.innerOrder.state = data.state
      this.innerOrder.cost = data.cost
      this.innerOrder.placeTime = data.placeTime
      this.innerOrder.completeTime = data.completeTime
      this.innerOrder.cancelTime = data.cancelTime
      this.innerOrder.items.splice(0, this.innerOrder.items.length)
      for (let item of data.items) {
        this.innerOrder.items.push(item)
      }
    }
  }
}
</script>

<style scoped>

</style>
