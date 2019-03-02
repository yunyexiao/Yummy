<template>
  <HeaderBase>
    <v-btn flat @click="$router.push('/restaurant/home')">
      <h3>处理订单</h3>
    </v-btn>
    <v-menu>
      <v-btn flat slot="activator">
        <h3>{{id}}</h3>
        <v-icon>arrow_drop_down</v-icon>
      </v-btn>
      <v-list>
        <v-list-tile
          v-for="(menuItem, index) in menuItems"
          :key="index"
          @click="menuItem.onClick"
        >
          <v-list-tile-title>{{menuItem.title}}</v-list-tile-title>
        </v-list-tile>
      </v-list>
    </v-menu>
  </HeaderBase>
</template>

<script>
import HeaderBase from '@/components/util/HeaderBase'
export default {
  name: 'RestaurantHeader',
  components: {HeaderBase},
  props: {
    'id': String
  },
  data: function () {
    return {
      menuItems: [
        {
          title: '管理餐馆信息',
          onClick: () => this.$router.push('/restaurant/home/info')
        },
        {
          title: '管理菜单',
          onClick: () => this.$router.push('/restaurant/home/meal')
        },
        {
          title: '管理促销活动',
          onClick: () => this.$router.push('/restaurant/home/promotion')
        },
        {
          title: '查看订单记录',
          onClick: () => this.$router.push('/restaurant/home/records')
        },
        {
          title: '查看财务统计',
          onClick: () => this.$router.push('/restaurant/home/financial')
        },
        {
          title: '退出',
          onClick: this.signOut
        }
      ]
    }
  },
  methods: {
    signOut: function () {
      this.$ajax({
        url: '/restaurant/sign-out',
        method: 'post'
      }).then(this.$router.push('/'))
    }
  }
}
</script>

<style scoped>

</style>
