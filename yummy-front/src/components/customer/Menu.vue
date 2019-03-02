<template>
  <v-layout xs12>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout justify-center>
        <h2>{{restaurant.id}} {{restaurant.name}}</h2>
      </v-layout>
      <v-layout>
        <v-expansion-panel expand v-model="expansion">
          <v-expansion-panel-content>
            <h3 slot="header">满减促销</h3>
            <v-card>
              <v-card-text>
                <p
                  v-for="p in restaurant.promotions"
                  :key="p.amount"
                >
                  满 {{p.amount}} 减 {{p.discount}}
                </p>
              </v-card-text>
            </v-card>
          </v-expansion-panel-content>
          <v-expansion-panel-content>
            <h3 slot="header">菜单</h3>
            <v-tabs show-arrows>
              <v-tabs-slider></v-tabs-slider>
              <v-tab
                v-for="type in Object.keys(menu)"
                :key="type"
                :href="'#' + type"
              >{{type}}</v-tab>
              <v-tabs-items>
                <v-tab-item
                  v-for="(meals, type) in menu"
                  :key="type"
                  :value="type"
                >
                  <v-list two-line>
                    <template v-for="(meal, index) in meals">
                      <v-list-tile
                        :key="meal.id"
                      >
                        <v-list-tile-content>
                          <v-list-tile-title>
                            {{meal.name}}:  {{meal.description}}
                          </v-list-tile-title>
                          <v-list-tile-sub-title>
                            单价：{{meal.price}}  剩余数量：{{meal.num}}
                          </v-list-tile-sub-title>
                        </v-list-tile-content>
                        <v-list-tile-action>
                          <NumField
                            v-model="orderItems[meal.id]"
                            @input="updateCost"
                            label="数量"
                          ></NumField>
                        </v-list-tile-action>
                      </v-list-tile>
                      <v-divider :key="index" v-if="index < menu[type].length - 1"></v-divider>
                    </template>
                  </v-list>
                </v-tab-item>
              </v-tabs-items>
            </v-tabs>
          </v-expansion-panel-content>
          <v-expansion-panel-content>
            <h3 slot="header">
              已选中菜品，合计{{cost.before.toFixed(1)}}元，
              优惠后共{{cost.after.toFixed(1)}}元
            </h3>
            <v-list>
              <template v-for="(item, index) in selectedItems">
                <v-list-tile
                  :key="item.mid"
                >
                  <v-list-tile-content>
                    <v-list-tile-title>
                      {{item.name}} * {{item.num}}
                        共{{item.sum.toFixed(1)}}元
                    </v-list-tile-title>
                  </v-list-tile-content>
                  <v-list-tile-action>
                    <NumField
                      v-model="orderItems[item.mid]"
                      @input="updateCost"
                      label="数量"
                    ></NumField>
                  </v-list-tile-action>
                </v-list-tile>
                <v-divider v-if="index < selectedItems.length - 1" :key="'divider' + index"/>
              </template>
            </v-list>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-layout>
      <v-layout justify-end>
        <v-btn color="success" lg @click="gotoPlace">我选好了</v-btn>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
  </v-layout>
</template>

<script>
import NumField from '@/components/util/NumField'
export default {
  name: 'Menu',
  components: {NumField},
  beforeMount: function () {
    this.$ajax({
      url: '/restaurant/get/' + this.restaurant.id,
      method: 'get'
    }).then(res => {
      this.restaurant.name = res.data.name
      this.restaurant.description = res.data.description
      this.restaurant.type = res.data.type
      this.restaurant.address = res.data.address
      this.restaurant.phone = res.data.phone
      for (let p of res.data.promotions) {
        this.restaurant.promotions.push(p)
      }
      for (let m of res.data.meals) {
        this.$set(this.restaurant.meals, m.id, m)
        this.$set(this.orderItems, m.id, 0)
      }
      this.generateMenu()
    })
  },
  data: function () {
    return {
      restaurant: {
        id: this.$route.params.rid,
        name: '',
        description: '',
        type: '',
        address: '',
        phone: '',
        promotions: [],
        meals: {}
      },
      menu: {},
      expansion: [false, true, false],
      orderItems: {},
      cost: {
        before: 0,
        after: 0
      }
    }
  },
  computed: {
    selectedItems: function () {
      let items = []
      Object.keys(this.orderItems).forEach(mid => {
        let num = this.orderItems[mid]
        if (num > 0) {
          let m = this.restaurant.meals[mid]
          items.push({
            mid: mid,
            name: m.name,
            num: num,
            sum: m.price * num
          })
        }
      })
      return items
    }
  },
  methods: {
    generateMenu: function () {
      Object.values(this.restaurant.meals).forEach(m => {
        if (m.type in this.menu) {
          this.menu[m.type].push(m)
        } else {
          this.$set(this.menu, m.type, [m])
        }
      })
    },
    updateCost: function () {
      let beforeSum = 0
      for (let m of this.selectedItems) {
        beforeSum += m.sum
      }
      this.cost.before = beforeSum
      let items = {}
      Object.keys(this.orderItems).forEach(mid => {
        let num = this.orderItems[mid]
        if (num > 0) {
          items[mid] = num
        }
      })
      this.$ajax({
        url: '/customer/order/cost/calc',
        method: 'post',
        params: {
          'rid': this.restaurant.id
        },
        data: items
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          this.cost.after = res.data.cost
        }
      })
    },
    gotoPlace: function () {
      window.localStorage.setItem('orderInfo', JSON.stringify({
        restaurant: {
          id: this.restaurant.id,
          name: this.restaurant.name,
          address: this.restaurant.address,
          phone: this.restaurant.phone
        },
        items: this.selectedItems,
        cost: this.cost.after
      }))
      this.$router.push('/customer/home/order/place')
    }
  }
}
</script>

<style scoped>

</style>
