<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout justify-center>
        <h2>平台统计信息</h2>
      </v-layout>
      <v-card>
        <v-card-title>
          <h3>用户统计</h3>
        </v-card-title>
        <v-card-text>
          <v-layout justify-start>
            <p>总顾客人数：{{statistics.customerCount}}&emsp;</p>
            <p>总餐馆数量：{{statistics.restaurantCount}}</p>
          </v-layout>
        </v-card-text>
      </v-card>
      <v-divider></v-divider>
      <v-card>
        <v-card-title>
          <h3>营收状况统计</h3>
        </v-card-title>
        <v-card-text>
          <v-layout justify-start>
            <DatePicker v-model="dateRage.start" label="开始月份" type="month"></DatePicker>
            <DatePicker v-model="dateRage.end" label="结束月份" type="month"></DatePicker>
            <v-btn @click="loadStatistics">查询</v-btn>
          </v-layout>
          <v-layout>
            <p>
              该时间段内共有<span style="font-size: 25px;">{{sums.income}}元</span>流入平台，
              <span style="font-size: 25px;">{{sums.outcome}}元</span>流出平台，
              毛利润<span style="font-size: 25px;">{{sums.profit}}元</span>。
            </p>
          </v-layout>
          <div id="financialChart" style="width: 800px; height: 400px;"></div>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
  </v-layout>
</template>

<script>
import DatePicker from '../util/DatePicker'
import echarts from 'echarts'

export default {
  name: 'Statistics',
  components: {DatePicker},
  mounted: function () {
    this.financialChart = echarts.init(document.getElementById('financialChart'))
    this.financialChart.setOption({
      title: {
        text: '每月营收状况'
      },
      tooltip: {},
      xAxis: {
        data: []
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          formatter: '{value}元'
        }
      },
      legend: {
        data: ['资金流入', '资金流出', '毛利润']
      },
      series: [
        {
          name: '资金流入',
          type: 'line',
          data: []
        },
        {
          name: '资金流出',
          type: 'line',
          data: []
        },
        {
          name: '毛利润',
          type: 'line',
          data: []
        }
      ]
    })
    this.loadStatistics()
  },
  computed: {
    sums: function () {
      let sumIncome = 0
      let sumOutcome = 0
      Object.keys(this.statistics.finance).forEach(month => {
        sumIncome += this.statistics.finance[month]['income']
        sumOutcome += this.statistics.finance[month]['outcome']
      })
      return {
        income: sumIncome.toFixed(2),
        outcome: sumOutcome.toFixed(2),
        profit: (sumIncome - sumOutcome).toFixed(2)
      }
    }
  },
  data: function () {
    return {
      dateRage: {
        start: new Date().toISOString().substr(0, 7),
        end: new Date().toISOString().substr(0, 7)
      },
      statistics: {
        customerCount: 0,
        restaurantCount: 0,
        finance: {}
      },
      financialChart: undefined
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
          this.drawCharts()
        }
      })
    },
    drawCharts: function () {
      let months = Object.keys(this.statistics.finance).sort()
      let incomeData = []
      let outcomeData = []
      let profitData = []
      for (let month of months) {
        let income = this.statistics.finance[month]['income']
        let outcome = this.statistics.finance[month]['outcome']
        incomeData.push(income.toFixed(2))
        outcomeData.push(outcome.toFixed(2))
        profitData.push((income - outcome).toFixed(2))
      }
      this.financialChart.setOption({
        xAxis: {
          data: months
        },
        series: [
          {
            name: '资金流入',
            type: 'line',
            data: incomeData
          },
          {
            name: '资金流出',
            type: 'line',
            data: outcomeData
          },
          {
            name: '毛利润',
            type: 'line',
            data: profitData
          }
        ]
      })
    }
  }
}
</script>

<style scoped>

</style>
