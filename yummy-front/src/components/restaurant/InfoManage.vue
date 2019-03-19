<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout justify-center>
        <h2>餐馆信息管理</h2>
      </v-layout>
      <v-layout justify-space-around>
        <h4>编号：{{generalInfo.id}}</h4>
        <h4>状态：{{generalInfo.valid ? '已上线' : '未上线'}}</h4>
        <h4>余额：{{generalInfo.balance.toFixed(2)}}</h4>
      </v-layout>
      <v-layout>
        <v-expansion-panel expand v-model="expansion">
          <v-expansion-panel-content>
            <h3 slot="header">已过审信息</h3>
            <v-card>
              <v-card-text v-if="publicInfo.exist">
                <p>餐馆名：{{publicInfo.name}}</p>
                <p>餐馆简介：{{publicInfo.description}}</p>
                <p>餐馆类型：{{publicInfo.type}}</p>
                <p>地址：{{publicInfo.address}}</p>
                <p>纬度：{{publicInfo.latitude}}</p>
                <p>经度：{{publicInfo.longitude}}</p>
                <p>客服电话：{{publicInfo.phone}}</p>
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
                <v-form v-show="draft.exist || additionMode" ref="infoForm">
                  <v-text-field
                    label="餐馆名"
                    v-model="draft.name"
                    :counter="40"
                    :rules="rules.nameRules"
                    :disabled="!editable"
                  ></v-text-field>
                  <v-textarea
                    label="餐馆简介"
                    v-model="draft.description"
                    :counter="100"
                    :rules="rules.descriptionRules"
                    :disabled="!editable"
                  ></v-textarea>
                  <v-text-field
                    label="餐馆类型"
                    v-model="draft.type"
                    :counter="20"
                    :rules="rules.typeRules"
                    :disabled="!editable"
                  ></v-text-field>
                  <v-text-field
                    label="地址"
                    v-model="draft.address"
                    :counter="50"
                    :rules="rules.addressRules"
                    :disabled="!editable"
                  ></v-text-field>
                  <v-text-field
                    label="纬度"
                    v-model="draft.latitude"
                    type="number"
                    :rules="rules.latitudeRules"
                    :disabled="!editable"
                  ></v-text-field>
                  <v-text-field
                    label="经度"
                    v-model="draft.longitude"
                    type="number"
                    :rules="rules.longitudeRules"
                    :disabled="!editable"
                  ></v-text-field>
                  <v-text-field
                    label="客服电话"
                    v-model="draft.phone"
                    :counter="11"
                    :rules="rules.phoneRules"
                    :disabled="!editable"
                  ></v-text-field>
                </v-form>
                <h4 v-show="!draft.exist && !additionMode">无待审核信息</h4>
              </v-card-text>
              <v-card-actions>
                <v-btn v-show="draft.exist" color="primary" @click="editable = !editable">
                  {{editable ? '取消' : '修改'}}
                </v-btn>
                <v-btn v-show="!draft.exist" color="primary" @click="switchMode">
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
  },
  data: function () {
    return {
      generalInfo: {
        id: '',
        valid: 0,
        balance: 0
      },
      publicInfo: {
        exist: false,
        name: '',
        description: '',
        type: '',
        address: '',
        latitude: 0,
        longitude: 0,
        phone: ''
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
  methods: {
    loadInfo: function () {
      this.$ajax({
        url: '/restaurant/info/get',
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          // load general info
          this.generalInfo.id = res.data.id
          this.generalInfo.balance = res.data.balance
          this.generalInfo.valid = res.data.valid
          // load other info
          if (res.data.valid === 0) {
            this.publicInfo.exist = false
            this.draft.exist = true
            this.draft.name = res.data.name
            this.draft.description = res.data.description
            this.draft.type = res.data.type
            this.draft.address = res.data.address
            this.draft.latitude = res.data.latitude
            this.draft.longitude = res.data.longitude
            this.draft.phone = res.data.phone
          } else {
            this.publicInfo.exist = true
            this.publicInfo.name = res.data.name
            this.publicInfo.description = res.data.description
            this.publicInfo.type = res.data.type
            this.publicInfo.address = res.data.address
            this.publicInfo.latitude = res.data.latitude
            this.publicInfo.longitude = res.data.longitude
            this.publicInfo.phone = res.data.phone
            this.loadDraft()
          }
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
              this.editable = !this.editable
              this.loadInfo()
            }
          })
        }
      }
    },
    draftSameWithInfo: function () {
      return this.draft.name === this.publicInfo.name &&
        this.draft.type === this.publicInfo.type &&
        this.draft.description === this.publicInfo.description &&
        this.draft.address === this.publicInfo.address &&
        this.draft.latitude === this.publicInfo.latitude &&
        this.draft.longitude === this.publicInfo.longitude &&
        this.draft.phone === this.publicInfo.phone
    }
  }
}
</script>

<style scoped>

</style>
