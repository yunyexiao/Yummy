<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-card>
        <v-card-title>
          <h2>收货地址管理</h2>
        </v-card-title>
        <v-card-text>
          <v-list>
            <template v-for="(addressInfo, index) in addressInfos">
              <v-list-tile
                :key="addressInfo.id"
                @click="showDialog(addressInfo)"
              >
                <v-list-tile-title>
                  {{addressInfo.address}}
                </v-list-tile-title>
              </v-list-tile>
              <v-divider :key="'d' + index"></v-divider>
            </template>
          </v-list>
        </v-card-text>
        <v-card-actions>
          <v-btn
            color="primary"
            @click="addDialog"
          >
            添加收货地址
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
    <v-dialog v-model="detailDialog.show" width="400">
      <v-card>
        <v-card-text>
          <v-form ref="addressForm" lazy-validation>
            <v-text-field
              label="地址"
              v-model="address"
              :rules="rules.addressRules"
              :counter="50"
              :disabled="!detailDialog.editable"
            ></v-text-field>
            <v-text-field
              type="number"
              label="纬度"
              :rules="rules.latitudeRules"
              v-model="latitude"
              :disabled="!detailDialog.editable"
            ></v-text-field>
            <v-text-field
              type="number"
              label="经度"
              :rules="rules.longitudeRules"
              v-model="longitude"
              :disabled="!detailDialog.editable"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <template v-if="!detailDialog.additionMode">
            <v-btn color="primary" @click="detailDialog.editable = !detailDialog.editable">
              {{detailDialog.editable ? '取消' : '编辑'}}
            </v-btn>
            <v-btn color="success" :disabled="!detailDialog.editable" @click="modifyAddress">
              提交
            </v-btn>
            <v-btn color="error" @click="removeAddress">删除</v-btn>
          </template>
          <v-btn v-else color="success" @click="addAddress">提交</v-btn>
          <v-btn color="info" @click="detailDialog.show = false">关闭</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-layout>
</template>

<script>
export default {
  name: 'AddressManage',
  beforeMount: function () {
    this.loadAddresses()
  },
  data: function () {
    return {
      addressInfos: [],
      aid: 0,
      address: '',
      latitude: 0,
      longitude: 0,
      detailDialog: {
        show: false,
        additionMode: false,
        editable: false
      },
      rules: {
        addressRules: [
          address => !!address || '请填写详细地址',
          address => (address && address.length <= 50) || '详细地址长度在50字符以内'
        ],
        latitudeRules: [
          latitude => !!latitude || '请填写纬度',
          latitude => (latitude && latitude > -90 && latitude < 90) || '纬度不正确'
        ],
        longitudeRules: [
          longitude => !!longitude || '请填写经度',
          longitude => (longitude && longitude > -180 && longitude < 180) || '经度不正确'
        ]
      }
    }
  },
  methods: {
    loadAddresses: function () {
      this.$ajax({
        url: '/customer/info/address/get',
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.addressInfos.splice(0, this.addressInfos.length)
          for (let addressInfo of res.data) {
            this.addressInfos.push(addressInfo)
          }
        }
      })
    },
    addDialog: function () {
      this.aid = 0
      this.address = ''
      this.latitude = ''
      this.longitude = ''
      this.$refs.addressForm.resetValidation()
      this.detailDialog.additionMode = true
      this.detailDialog.editable = true
      this.detailDialog.show = true
    },
    showDialog: function (addressInfo) {
      this.aid = addressInfo.id
      this.address = addressInfo.address
      this.latitude = addressInfo.latitude
      this.longitude = addressInfo.longitude
      this.$refs.addressForm.resetValidation()
      this.detailDialog.additionMode = false
      this.detailDialog.editable = false
      this.detailDialog.show = true
    },
    removeAddress: function () {
      this.$ajax({
        url: '/customer/info/address/remove',
        method: 'post',
        params: {
          'aid': this.aid
        }
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.detailDialog.show = false
          this.loadAddresses()
        }
      })
    },
    modifyAddress: function () {
      if (this.$refs.addressForm.validate()) {
        this.$ajax({
          url: '/customer/info/address/modify',
          method: 'post',
          params: {
            'aid': this.aid,
            'address': this.address,
            'latitude': this.latitude,
            'longitude': this.longitude
          }
        }).then(res => {
          if (res.data['AccessDenied']) {
            this.$router.push('/')
          } else {
            this.detailDialog.show = false
            this.loadAddresses()
          }
        })
      }
    },
    addAddress: function () {
      if (this.$refs.addressForm.validate()) {
        this.$ajax({
          url: '/customer/info/address/add',
          method: 'post',
          params: {
            'address': this.address,
            'latitude': this.latitude,
            'longitude': this.longitude
          }
        }).then(res => {
          if (res.data['AccessDenied']) {
            this.$router.push('/')
          } else {
            this.detailDialog.show = false
            this.loadAddresses()
          }
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
