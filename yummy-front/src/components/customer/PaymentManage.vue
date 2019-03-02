<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-card>
        <v-card-title>
          <h2>支付账户管理</h2>
        </v-card-title>
        <v-card-text>
          <v-list two-line>
            <template v-for="(payment, index) in payments">
              <v-list-tile
                :key="payment.pid"
                @click="viewDialog(payment)"
              >
                <v-list-tile-title>{{payment.pid}}</v-list-tile-title>
                <v-list-tile-sub-title>余额：{{payment.balance}}</v-list-tile-sub-title>
              </v-list-tile>
              <v-divider :key="'d' + index"></v-divider>
            </template>
          </v-list>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="addDialog">添加支付账户</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
    <v-dialog v-model="detailDialog.show" width="400">
      <v-card>
        <v-card-text>
          <v-form ref="paymentForm">
            <v-text-field
              label="支付帐号"
              :disabled="!detailDialog.additionMode"
              v-model="pid"
              :counter="20"
              :rules="rules.pidRules"
            ></v-text-field>
            <v-text-field
              v-show="detailDialog.additionMode"
              label="密码"
              type="password"
              v-model="pwd"
              :counter="20"
              :rules="rules.pwdRules"
            ></v-text-field>
            <v-text-field
              v-show="!detailDialog.additionMode"
              type="number"
              label="余额"
              disabled
              v-model="balance"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn
            v-show="!detailDialog.additionMode"
            color="error"
            @click="removePayment"
          >删除</v-btn>
          <v-btn
            v-show="detailDialog.additionMode"
            color="success"
            @click="addPayment"
          >提交</v-btn>
          <v-btn
            color="info"
            @click="detailDialog.show = false"
          >关闭</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-layout>
</template>

<script>
export default {
  name: 'PaymentManage',
  beforeMount: function () {
    this.loadPayments()
  },
  data: function () {
    return {
      payments: [],
      pid: '',
      pwd: '',
      balance: 0,
      detailDialog: {
        show: false,
        additionMode: false
      },
      rules: {
        pidRules: [
          pid => !!pid || '请填写支付帐号',
          pid => (pid && pid.length <= 20) || '支付帐号长度不能超过20个字符'
        ],
        pwdRules: [
          pwd => !!pwd || '请填写支付帐号的密码',
          pwd => (pwd && pwd.length <= 20) || '密码长度不能超过20个字符'
        ]
      }
    }
  },
  methods: {
    loadPayments: function () {
      this.$ajax({
        url: '/customer/info/payment/get',
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.payments.splice(0, this.payments.length)
          for (let p of res.data) {
            this.payments.push(p)
          }
        }
      })
    },
    addDialog: function () {
      this.pid = ''
      this.pwd = ''
      this.$refs.paymentForm.resetValidation()
      this.detailDialog.additionMode = true
      this.detailDialog.show = true
    },
    viewDialog: function (payment) {
      this.pid = payment.pid
      this.pwd = ''
      this.balance = payment.balance
      this.$refs.paymentForm.resetValidation()
      this.detailDialog.additionMode = false
      this.detailDialog.show = true
    },
    addPayment: function () {
      if (this.$refs.paymentForm.validate()) {
        this.$ajax({
          url: '/customer/info/payment/add',
          method: 'post',
          params: {
            'pid': this.pid,
            'pwd': this.pwd
          }
        }).then(res => {
          if (res.data['AccessDenied']) {
            this.$router.push('/')
          } else {
            this.detailDialog.show = false
            this.loadPayments()
          }
        })
      }
    },
    removePayment: function () {
      this.$ajax({
        url: '/customer/info/payment/remove',
        method: 'post',
        params: {
          'pid': this.pid
        }
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.detailDialog.show = false
          this.loadPayments()
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
