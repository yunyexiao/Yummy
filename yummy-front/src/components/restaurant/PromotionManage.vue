<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout justify-center>
        <h2>促销活动管理</h2>
        <v-btn
          fab
          right
          color="pink"
          dark
          absolute
          @click="addPromotion"
        >
          <v-icon>add</v-icon>
        </v-btn>
      </v-layout>
      <v-layout>
        <v-flex>
          <v-list>
            <template v-for="(p, index) in promotions">
              <v-list-tile :key="p.amount" @click="showInfo(p)">
                <v-list-tile-content>
                  <v-list-tile-title>
                    满{{p.amount}}减{{p.discount}}&emsp;&emsp;
                    开始日期：{{p.startDate}}&emsp;&emsp;结束日期：{{p.endDate}}
                  </v-list-tile-title>
                </v-list-tile-content>
              </v-list-tile>
              <v-divider :key="'d' + index" v-if="index < promotions.length - 1"></v-divider>
            </template>
          </v-list>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
    <v-dialog v-model="infoDialog.show" width="400">
      <v-card>
        <v-card-text>
          <v-form ref="promotionForm">
            <v-layout>
              <v-text-field
                label="满"
                v-model="promotion.amount"
                size="3"
                prefix="￥"
                type="number"
                :disabled="!infoDialog.editable"
                :rules="rules.amount"
              ></v-text-field>
              <v-text-field
                label="减"
                v-model="promotion.discount"
                size="3"
                prefix="￥"
                type="number"
                :disabled="!infoDialog.editable"
                :rules="rules.discount"
              ></v-text-field>
            </v-layout>
            <DatePicker
              label="开始日期"
              v-model="promotion.startDate"
              :full-width="true"
              :disabled="!infoDialog.editable"
              :rules="rules.startDate"
            ></DatePicker>
            <DatePicker
              label="结束日期"
              v-model="promotion.endDate"
              :full-width="true"
              :disabled="!infoDialog.editable"
              :rules="rules.endDate"
            ></DatePicker>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn
            v-show="infoDialog.editable"
            color="success"
            @click="publishPromotion"
          >提交</v-btn>
          <v-btn color="info" @click="infoDialog.show = false">取消</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar v-model="snackbar.show" top>
      {{snackbar.msg}}
      <v-btn color="pink" @click="snackbar.show = false" flat>关闭</v-btn>
    </v-snackbar>
  </v-layout>
</template>

<script>
import DatePicker from '@/components/util/DatePicker'
export default {
  name: 'PromotionManage',
  components: {DatePicker},
  beforeMount: function () {
    this.loadPromotion()
  },
  data: function () {
    return {
      promotions: [],
      promotion: {
        amount: 0,
        discount: 0,
        startDate: '',
        endDate: ''
      },
      infoDialog: {
        show: false,
        editable: false,
        additionMode: false
      },
      snackbar: {
        show: false,
        msg: ''
      },
      rules: {
        amount: [
          amount => !!amount || '请填写额度',
          amount => !this.alreadyExist(amount) || '该额度的促销已存在'
        ],
        discount: [
          discount => !!discount || '请填写优惠额'
        ],
        startDate: [
          startDate => !!startDate || '请填写开始日期',
          startDate => (!this.promotion.endDate || new Date(startDate) <= new Date(this.promotion.endDate)) ||
            '开始日期必须早于结束日期'
        ],
        endDate: [
          endDate => !!endDate || '请填写结束日期',
          endDate => (!this.promotion.startDate || new Date(this.promotion.startDate) <= new Date(endDate)) ||
            '结束日期必须晚于开始日期'
        ]
      }
    }
  },
  methods: {
    loadPromotion: function () {
      this.$ajax({
        url: '/restaurant/promotion/get',
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.promotions.splice(0, this.promotions.length)
          for (let p of res.data) {
            this.promotions.push(p)
          }
        }
      })
    },
    addPromotion: function () {
      this.promotion.amount = 0
      this.promotion.discount = 0
      this.promotion.startDate = ''
      this.promotion.endDate = ''
      this.$refs.promotionForm.resetValidation()
      this.infoDialog.additionMode = true
      this.infoDialog.editable = true
      this.infoDialog.show = true
    },
    showInfo: function (promotion) {
      this.promotion.amount = promotion.amount
      this.promotion.discount = promotion.discount
      this.promotion.startDate = promotion.startDate
      this.promotion.endDate = promotion.endDate
      this.$refs.promotionForm.resetValidation()
      this.infoDialog.additionMode = false
      this.infoDialog.editable = false
      this.infoDialog.show = true
    },
    publishPromotion: function () {
      if (this.$refs.promotionForm.validate()) {
        this.$ajax({
          url: '/restaurant/promotion/publish',
          method: 'post',
          data: this.promotion
        }).then(res => {
          if (res.data['AccessDenied']) {
            this.$router.push('/')
          } else {
            switch (res.data['result']) {
              case 0:
                this.snackbar.msg = '促销活动发布成功。'
                this.snackbar.show = true
                this.loadPromotion()
                break
              case 1:
                this.snackbar.msg = '您的餐馆尚未上线，无法发布促销活动。'
                this.snackbar.show = true
                break
              default:
                this.snackbar.msg = '服务器未知错误。'
                this.snackbar.show = true
                break
            }
          }
        })
      }
    },
    alreadyExist: function (amount) {
      for (let p of this.promotions) {
        if (p.amount === amount) {
          return true
        }
      }
      return false
    }
  }
}
</script>

<style scoped>

</style>
