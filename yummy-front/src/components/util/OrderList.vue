<template>
  <v-list two-line>
    <template v-for="(order, index) in orders">
      <v-list-tile
        :key="order.id"
        @click="$emit('order-selected', order)"
      >
        <v-list-tile-content>
          <v-list-tile-title v-if="role === 'restaurant'">
            订单编号：{{order.id}}  顾客名称：{{order.customer.name}}  订单金额：{{order.cost.toFixed(1)}}
          </v-list-tile-title>
          <v-list-tile-title v-else-if="role === 'customer'">
            订单编号：{{order.id}}  餐馆名称：{{order.restaurant.name}}  订单金额：{{order.cost.toFixed(1)}}
          </v-list-tile-title>
          <v-list-tile-title v-else>
            订单编号：{{order.id}}  顾客：{{order.customer}}  餐馆：{{order.restaurant}} 订单金额：{{order.cost.toFixed(1)}}
          </v-list-tile-title>
          <v-list-tile-sub-title>
            送餐地址：{{order.destination}} 下单时间：{{order.placeTime}}
          </v-list-tile-sub-title>
        </v-list-tile-content>
        <v-list-tile-action>
          <slot :order="order"></slot>
        </v-list-tile-action>
      </v-list-tile>
      <v-divider :key="'s' + index" v-if="index < orders.length - 1"></v-divider>
    </template>
  </v-list>
</template>

<script>
export default {
  name: 'OrderList',
  props: {
    orders: Array,
    role: String
  }
}
</script>

<style scoped>

</style>
