<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout justify-space-between>
        <DatePicker label="开始日期" v-model="startDate"></DatePicker>
        <DatePicker label="结束日期" v-model="endDate"></DatePicker>
        <v-btn color="primary" @click="reloadTransactions">查找交易记录</v-btn>
      </v-layout>
      <v-layout>
        <v-flex>
          <v-list two-line>
            <template v-for="(transaction, index) in transactions">
              <v-list-tile :key="transaction.id">
                <v-list-tile-content>
                  <v-list-tile-title>
                    交易ID: {{transaction.id}}&emsp;&emsp;
                    订单编号: {{transaction.oid}}&emsp;&emsp;
                    收入金额: {{transaction.amount}}
                  </v-list-tile-title>
                  <v-list-tile-sub-title>
                    交易时间: {{transaction.time}}
                  </v-list-tile-sub-title>
                </v-list-tile-content>
              </v-list-tile>
              <v-divider :key="'d' + index" v-if="index < transactions.length - 1"></v-divider>
            </template>
          </v-list>
        </v-flex>
      </v-layout>
      <v-layout>
        <v-btn v-show="morePage" block @click="loadNextPage">加载下一页</v-btn>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
  </v-layout>
</template>

<script>
import DatePicker from '@/components/util/DatePicker'
export default {
  name: 'FinancialStats',
  components: {DatePicker},
  beforeMount: function () {
    this.loadTransactions()
  },
  data: function () {
    return {
      startDate: new Date().toISOString().substr(0, 10),
      endDate: new Date().toISOString().substr(0, 10),
      page: 1,
      pageSize: 10,
      morePage: true,
      transactions: []
    }
  },
  methods: {
    loadTransactions: function () {
      this.$ajax({
        url: '/restaurant/finance/list',
        method: 'get',
        params: {
          'startDate': this.startDate,
          'endDate': this.endDate,
          'pageStart': (this.page - 1) * this.pageSize,
          'pageSize': this.pageSize
        }
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          for (let t of res.data) {
            this.transactions.push(t)
          }
          this.morePage = res.data.length === this.pageSize
        }
      })
    },
    reloadTransactions: function () {
      this.transactions.splice(0, this.transactions.length)
      this.page = 1
      this.loadTransactions()
    },
    loadNextPage: function () {
      this.page++
      this.loadTransactions()
    }
  }
}
</script>

<style scoped>

</style>
