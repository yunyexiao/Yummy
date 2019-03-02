<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout justify-center>
        <h2>餐馆信息管理</h2>
      </v-layout>
      <v-layout justify-space-around>
        <h4>编号：{{restaurant.id}}</h4>
        <h4>状态：{{restaurant.valid ? '已上线' : '未上线'}}</h4>
        <h4>余额：{{restaurant.balance}}</h4>
      </v-layout>
      <v-layout>
        <v-expansion-panel expand v-model="expansion">
          <v-expansion-panel-content>
            <h3 slot="header">已过审信息</h3>
            <v-card>
              <v-card-text v-if="validInfo.exist">
                <p>餐馆名：{{validInfo.name}}</p>
                <p>餐馆简介：{{validInfo.description}}</p>
                <p>餐馆类型：{{validInfo.type}}</p>
                <p>地址：{{validInfo.address}}</p>
                <p>纬度：{{validInfo.latitude}}</p>
                <p>经度：{{validInfo.longitude}}</p>
                <p>客服电话：{{validInfo.phone}}</p>
              </v-card-text>
              <v-card-text v-else>
                <h4>无已过审信息</h4>
              </v-card-text>
            </v-card>
          </v-expansion-panel-content>
          <v-expansion-panel-content>
            <h3 slot="header">待审核信息</h3>
            <v-card>
              <v-card-text>
                <v-form v-show="invalidInfo.exist || additionMode" ref="infoForm">
                  <v-text-field
                    label="餐馆名"
                    v-model="invalidInfo.name"
                    :counter="40"
                    :rules="rules.nameRules"
                    :disabled="!editable"
                  ></v-text-field>
                  <v-textarea
                    label="餐馆简介"
                    v-model="invalidInfo.description"
                    :counter="100"
                    :rules="rules.descriptionRules"
                    :disabled="!editable"
                  ></v-textarea>
                  <v-text-field
                    label="餐馆类型"
                    v-model="invalidInfo.type"
                    :counter="20"
                    :rules="rules.typeRules"
                    :disabled="!editable"
                  ></v-text-field>
                  <v-text-field
                    label="地址"
                    v-model="invalidInfo.address"
                    :counter="50"
                    :rules="rules.addressRules"
                    :disabled="!editable"
                  ></v-text-field>
                  <v-text-field
                    label="纬度"
                    v-model="invalidInfo.latitude"
                    type="number"
                    :rules="rules.latitudeRules"
                    :disabled="!editable"
                  ></v-text-field>
                  <v-text-field
                    label="经度"
                    v-model="invalidInfo.longitude"
                    type="number"
                    :rules="rules.longitudeRules"
                    :disabled="!editable"
                  ></v-text-field>
                  <v-text-field
                    label="客服电话"
                    v-model="invalidInfo.phone"
                    :counter="11"
                    :rules="rules.phoneRules"
                    :disabled="!editable"
                  ></v-text-field>
                </v-form>
                <h4 v-show="!invalidInfo.exist && !additionMode">无待审核信息</h4>
              </v-card-text>
              <v-card-actions>
                <v-btn v-show="invalidInfo.exist" color="primary" @click="editable = !editable">
                  {{editable ? '取消' : '修改'}}
                </v-btn>
                <v-btn v-show="!invalidInfo.exist" color="primary" @click="switchMode">
                  {{additionMode ? '取消' : '新增'}}
                </v-btn>
                <v-btn v-show="editable" color="success" @click="updateInfo">
                  提交
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
    <v-snackbar v-model="snackbar.show">
      {{snackbar.msg}}
      <v-btn color="pink" flat @click="snackbar.show = false">关闭</v-btn>
    </v-snackbar>
  </v-layout>
</template>

<script>
import inputRules from '@/components/restaurant/input-rules'
export default {
  name: 'InfoManage',
  beforeMount: function () {
    this.loadInfo()
    this.loadDraft()
  },
  data: function () {
    return {
      restaurant: {
        id: '',
        name: '',
        description: '',
        type: '',
        address: '',
        latitude: 0,
        longitude: 0,
        phone: '',
        balance: 0,
        valid: 0
      },
      draft: {
        exist: false,
        name: '',
        description: '',
        type: '',
        address: '',
        latitude: 0,
        longitude: 0,
        phone: ''
      },
      editable: false,
      additionMode: false,
      rules: inputRules,
      snackbar: {
        show: false,
        msg: ''
      },
      expansion: [true, false]
    }
  },
  computed: {
    validInfo: function () {
      if (this.restaurant.valid) {
        return {
          exist: true,
          ...this.restaurant
        }
      } else {
        return {
          exist: false
        }
      }
    },
    invalidInfo: function () {
      if (this.restaurant.valid) {
        return this.draft
      } else {
        return {
          exist: true,
          ...this.restaurant
        }
      }
    }
  },
  methods: {
    loadInfo: function () {
      this.$ajax({
        url: '/restaurant/info/get',
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.restaurant.id = res.data.id
          this.restaurant.name = res.data.name
          this.restaurant.description = res.data.description
          this.restaurant.type = res.data.type
          this.restaurant.address = res.data.address
          this.restaurant.latitude = res.data.latitude
          this.restaurant.longitude = res.data.longitude
          this.restaurant.phone = res.data.phone
          this.restaurant.balance = res.data.balance
          this.restaurant.valid = res.data.valid
        }
      })
    },
    loadDraft: function () {
      this.$ajax({
        url: '/restaurant/info/draft',
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else if (res.data) {
          this.draft.exist = true
          this.draft.name = res.data.name
          this.draft.description = res.data.description
          this.draft.type = res.data.type
          this.draft.address = res.data.address
          this.draft.latitude = res.data.latitude
          this.draft.longitude = res.data.longitude
          this.draft.phone = res.data.phone
        } else {
          this.draft.exist = false
        }
      })
    },
    switchMode: function () {
      this.additionMode = !this.additionMode
      this.editable = !this.editable || this.additionMode
      if (this.additionMode) {
        this.$refs.infoForm.resetValidation()
      }
    },
    updateInfo: function () {
      if (this.$refs.infoForm.validate()) {
        if (this.draftSameWithInfo()) {
          this.snackbar.msg = '新的信息与原信息完全一致。'
          this.snackbar.show = true
        } else {
          let {exist, ...draft} = this.draft
          this.$ajax({
            url: '/restaurant/info/modify',
            method: 'post',
            data: draft
          }).then(res => {
            if (res.data['AccessDenied']) {
              this.$router.push('/')
            } else {
              this.snackbar.msg = '提交成功，等待经理审核'
              this.snackbar.show = true
              this.loadInfo()
              this.loadDraft()
            }
          })
        }
      }
    },
    draftSameWithInfo: function () {
      return this.draft.name === this.restaurant.name &&
        this.draft.type === this.restaurant.type &&
        this.draft.description === this.restaurant.description &&
        this.draft.address === this.restaurant.address &&
        this.draft.latitude === this.restaurant.latitude &&
        this.draft.longitude === this.restaurant.longitude &&
        this.draft.phone === this.restaurant.phone
    }
  }
}
</script>

<style scoped>

</style>
