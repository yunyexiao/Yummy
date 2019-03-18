<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout justify-center>
        <h2>审核餐馆</h2>
      </v-layout>
      <v-layout>
        <v-flex xs6 sm4>
          <v-select
            label="类型"
            v-model="type"
            :items="types"
            @input="reloadRestaurants"
          ></v-select>
        </v-flex>
        <v-flex>
          <v-text-field
            label="搜索餐馆......"
            v-model="searchCondition"
            prepend-icon="search"
            @keyup.enter="reloadRestaurants"
          ></v-text-field>
        </v-flex>
      </v-layout>
      <v-layout>
        <v-flex>
          <v-list three-line>
            <template v-for="(r, index) in restaurants">
              <v-list-tile :key="r.id" @click="showInfo(r)">
                <v-list-tile-content>
                  <v-list-tile-title>
                    餐馆编号: {{r.id}}&emsp;&emsp;
                    餐馆名称: {{r.name}}&emsp;&emsp;
                  </v-list-tile-title>
                  <v-list-tile-sub-title>
                    简介: {{r.description}}
                  </v-list-tile-sub-title>
                  <v-list-tile-sub-title>
                    餐馆类型: {{r.type}}&emsp;&emsp;
                    餐馆地址: {{r.address}}
                  </v-list-tile-sub-title>
                </v-list-tile-content>
                <v-list-tile-action>
                </v-list-tile-action>
              </v-list-tile>
              <v-divider :key="'d' + index" v-if="index < restaurants.length - 1"></v-divider>
            </template>
          </v-list>
        </v-flex>
      </v-layout>
      <v-layout>
        <v-btn block v-show="morePage" @click="loadRestaurants">加载下一页</v-btn>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
    <v-dialog v-model="infoDialog" :width="type === types[0] ? 400 : 600">
      <v-card>
        <v-card-title>
          <v-layout justify-center>
            <h2>餐馆详细信息</h2>
          </v-layout>
          <v-layout justify-end>
            <h4>编号: {{restaurant.id}}</h4>
          </v-layout>
        </v-card-title>
        <v-card-text>
          <v-layout justify-space-around>
            <v-flex>
              <h3>{{type === types[0] ? '餐馆信息' : '修改前的餐馆信息'}}</h3>
              <p>餐馆名称: {{restaurant.name}}</p>
              <p>餐馆类型: {{restaurant.type}}</p>
              <p>餐馆介绍: {{restaurant.description}}</p>
              <p>餐馆地址: {{restaurant.address}}</p>
              <p>餐馆纬度: {{restaurant.latitude}}</p>
              <p>餐馆经度: {{restaurant.longitude}}</p>
              <p>餐馆服务电话: {{restaurant.phone}}</p>
            </v-flex>
            <v-flex v-show="type === types[1]">
              <h3>修改后的餐馆信息</h3>
              <p>餐馆名称: {{draft.name}}</p>
              <p>餐馆类型: {{draft.type}}</p>
              <p>餐馆介绍: {{draft.description}}</p>
              <p>餐馆地址: {{draft.address}}</p>
              <p>餐馆纬度: {{draft.latitude}}</p>
              <p>餐馆经度: {{draft.longitude}}</p>
              <p>餐馆服务电话: {{draft.phone}}</p>
            </v-flex>
          </v-layout>
        </v-card-text>
        <v-card-actions>
          <v-btn color="info" @click="infoDialog = false">关闭</v-btn>
          <v-btn color="success" @click.stop="checkRestaurant(true)">通过</v-btn>
          <v-btn color="error" @click.stop="checkRestaurant(false)">拒绝</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-layout>
</template>

<script>
export default {
  name: 'RestaurantCheck',
  beforeMount: function () {
    this.loadRestaurants()
  },
  data: function () {
    return {
      restaurants: [],
      restaurant: {
        id: '',
        name: '',
        type: '',
        description: '',
        address: '',
        latitude: 0,
        longitude: 0,
        phone: ''
      },
      draft: {
        name: '',
        type: '',
        description: '',
        address: '',
        latitude: 0,
        longitude: 0,
        phone: ''
      },
      types: ['新餐馆', '餐馆信息修改'],
      type: '新餐馆',
      searchCondition: '',
      pageSize: 10,
      morePage: true,
      infoDialog: false
    }
  },
  computed: {
    searchMode: function () {
      return this.searchCondition.length > 0
    }
  },
  methods: {
    loadRestaurants: function () {
      let listUrls = ['/admin/list/restaurant/invalid', '/admin/list/restaurant/drafted']
      let searchUrls = ['/admin/search/restaurant/invalid', '/admin/search/restaurant/drafted']
      let index = this.types.indexOf(this.type)
      this.$ajax({
        url: this.searchMode ? searchUrls[index] : listUrls[index],
        method: 'get',
        params: this.searchMode ? {
          'name': this.searchCondition,
          'pageStart': this.restaurants.length,
          'pageSize': this.pageSize
        } : {
          'pageStart': this.restaurants.length,
          'pageSize': this.pageSize
        }
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          for (let r of res.data) {
            this.restaurants.push(r)
          }
          this.morePage = res.data.length === this.pageSize
        }
      })
    },
    reloadRestaurants: function () {
      this.restaurants.splice(0, this.restaurants.length)
      this.loadRestaurants()
    },
    showInfo: function (r) {
      this.$ajax({
        url: '/admin/get/restaurant/' + r.id,
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.restaurant.id = res.data.id
          this.restaurant.name = res.data.name
          this.restaurant.type = res.data.type
          this.restaurant.description = res.data.description
          this.restaurant.address = res.data.address
          this.restaurant.latitude = res.data.latitude
          this.restaurant.longitude = res.data.longitude
          this.restaurant.phone = res.data.phone
          if (this.type === this.types[1]) {
            this.draft.name = res.data.draft.name
            this.draft.type = res.data.draft.type
            this.draft.description = res.data.draft.description
            this.draft.address = res.data.draft.address
            this.draft.latitude = res.data.draft.latitude
            this.draft.longitude = res.data.draft.longitude
            this.draft.phone = res.data.draft.phone
          }
          this.infoDialog = true
        }
      })
    },
    checkRestaurant: function (approved) {
      this.$ajax({
        url: '/admin/check/' + this.restaurant.id,
        method: 'post',
        params: {
          'approved': approved
        }
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.removeRestaurant(this.restaurant.id)
          this.infoDialog = false
        }
      })
    },
    removeRestaurant: function (id) {
      for (let i = 0; i < this.restaurants.length; i++) {
        if (this.restaurants[i].id === id) {
          this.restaurants.splice(i, 1)
          return
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
