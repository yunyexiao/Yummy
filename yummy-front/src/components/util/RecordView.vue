<template>
  <div>
    <v-layout>
      <v-select
        label="订单记录类型"
        v-model="type"
        :items="types"
        @input="reloadRecords"
      ></v-select>
      <DatePicker label="开始日期" v-model="startTime"></DatePicker>
      <DatePicker label="结束日期" v-model="endTime"></DatePicker>
      <v-text-field
        type="number"
        label="金额下限"
        v-model="costBottom"
      ></v-text-field>
      <v-text-field
        type="number"
        label="金额上限"
        v-model="costTop"
      ></v-text-field>
    </v-layout>
    <v-layout>
      <v-switch
        :label="role === 'customer' ? '按餐厅分组' : '按顾客分组'"
        v-model="grouped"
      ></v-switch>
      <v-btn color="primary" @click="reloadRecords">查询订单记录</v-btn>
    </v-layout>
    <v-layout>
      <v-flex>
        <OrderList
          :role="role"
          :orders="orderList"
          @order-selected="viewOrderInfo"
        ></OrderList>
      </v-flex>
    </v-layout>
    <v-layout>
      <v-btn block v-show="morePage" @click="loadNextPage">加载下一页</v-btn>
    </v-layout>
    <v-dialog v-model="orderInfoDialog" width="500">
      <v-card>
        <v-card-text>
          <OrderInfo :info="{id: selectedOid, role: role, load: orderInfoDialog}"></OrderInfo>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="orderInfoDialog = false">关闭</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import DatePicker from '@/components/util/DatePicker'
import OrderList from '@/components/util/OrderList'
import OrderInfo from '@/components/util/OrderInfo'
export default {
  name: 'RecordView',
  components: {OrderInfo, OrderList, DatePicker},
  props: {
    role: String,
    types: Array,
    urls: Array
  },
  beforeMount: function () {
    this.loadRecords()
  },
  data: function () {
    return {
      type: this.types[0],
      startTime: new Date().toISOString().substr(0, 10),
      endTime: new Date().toISOString().substr(0, 10),
      costBottom: undefined,
      costTop: undefined,
      grouped: false,
      morePage: true,
      page: 1,
      pageSize: 10,
      orderList: [],
      selectedOid: 0,
      orderInfoDialog: false
    }
  },
  methods: {
    reloadRecords: function () {
      this.orderList.splice(0, this.orderList.length)
      this.page = 1
      this.loadRecords()
    },
    loadRecords: function () {
      let params = {
        'startTime': this.startTime + ' 00:00:00.000',
        'endTime': this.endTime + ' 23:59:59.999',
        'pageStart': (this.page - 1) * this.pageSize,
        'pageSize': this.pageSize
      }
      if (this.role === 'customer') {
        params['groupByRestaurant'] = this.grouped
      } else {
        params['groupByCustomer'] = this.grouped
      }
      if (this.costBottom) {
        params['costBottom'] = this.costBottom
      }
      if (this.costTop) {
        params['costTop'] = this.costTop
      }
      this.$ajax({
        url: this.urls[this.types.indexOf(this.type)],
        method: 'get',
        params: params
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          for (let o of res.data) {
            this.orderList.push(o)
          }
          this.morePage = res.data.length === params['pageSize']
        }
      })
    },
    viewOrderInfo: function (order) {
      this.selectedOid = order.id
      this.orderInfoDialog = true
    },
    loadNextPage: function () {
      this.page++
      this.loadRecords()
    }
  }
}
</script>

<style scoped>

</style>
