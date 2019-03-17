<template>
  <v-container>
    <v-layout>
      <v-flex xs1 sm2></v-flex>
      <v-flex>
        <v-card>
          <v-toolbar>
            <v-select
              label="收货地址"
              v-model="address"
              :items="addresses"
              @change="reloadRestaurantList"
            ></v-select>
            <v-flex></v-flex>
            <v-text-field label="范围"
                          v-model="distance"
                          type="number"
                          suffix="km以内"
                          @keyup.enter="reloadRestaurantList"
            ></v-text-field>
          </v-toolbar>
          <v-toolbar>
            <v-text-field
              label="搜索......"
              v-model="searchCondition"
              prepend-icon="search"
              @keyup.enter="reloadRestaurantList"
            ></v-text-field>
          </v-toolbar>
          <v-list two-line>
            <template v-for="(r, index) in list">
              <v-list-tile
                :key="r.id"
                @click="selectRestaurant(r.id)"
              >
                <v-list-tile-content>
                  <v-list-tile-title>
                    {{r.id}} {{r.name}} {{r.description}}
                    <v-spacer></v-spacer>
                  </v-list-tile-title>
                  <v-list-tile-sub-title>
                    {{r.type}} {{r.address}}
                  </v-list-tile-sub-title>
                </v-list-tile-content>
              </v-list-tile>
              <v-divider :key="index" v-if="index < list.length - 1"></v-divider>
            </template>
          </v-list>
          <v-btn v-if="morePage" block @click="loadRestaurantList">加载下一页</v-btn>
        </v-card>
      </v-flex>
      <v-flex xs1 sm2></v-flex>
    </v-layout>
  </v-container>
</template>

<script>
export default {
  name: 'RestaurantList',
  beforeMount: function () {
    this.loadAddresses()
  },
  data: function () {
    return {
      address: '',
      addressMaps: {},
      distance: 15,
      morePage: true,
      pageSize: 10,
      list: [],
      searchCondition: ''
    }
  },
  computed: {
    addresses: function () {
      return Object.keys(this.addressMaps)
    },
    aid: function () {
      return this.addressMaps[this.address]
    },
    searchMode: function () {
      return this.searchCondition.length > 0
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
        } else if (res.data.length > 0) {
          for (let a of res.data) {
            this.$set(this.addressMaps, a.address, a.id)
          }
          this.address = res.data[0].address
          this.loadRestaurantList()
        } else {
          this.$router.push('/customer/home/address')
        }
      })
    },
    loadRestaurantList: function () {
      this.$ajax({
        url: this.searchMode ? '/restaurant/search' : '/customer/order/restaurants',
        method: 'get',
        params: this.searchMode ? {
          'name': this.searchCondition,
          'pageStart': this.list.length,
          'pageSize': this.pageSize
        } : {
          'aid': this.aid,
          'distance': this.distance,
          'pageStart': this.list.length,
          'pageSize': this.pageSize
        }
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          for (let r of res.data) {
            this.list.push(r)
          }
          this.morePage = res.data.length === this.pageSize
        }
      })
    },
    reloadRestaurantList: function () {
      this.list.splice(0, this.list.length)
      this.loadRestaurantList()
    },
    selectRestaurant: function (rid) {
      window.localStorage.setItem('destination', this.address)
      this.$router.push('/customer/home/menu/' + rid)
    }
  }
}
</script>

<style scoped>

</style>
