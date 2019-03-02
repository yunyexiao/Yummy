<template>
  <HeaderBase>
    <v-btn
      flat
      @click="$router.push('/customer/home')"
    >
      <h3>订餐</h3>
    </v-btn>
    <v-btn flat @click="$router.push('/customer/home/orders/unhandled')">
      <h3>待处理订单</h3>
    </v-btn>
    <v-menu>
      <v-btn flat slot="activator">
        <h3>{{name}} <br/> Lv: {{lvl}}</h3>
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
  name: 'CustomerHeader',
  components: {HeaderBase},
  props: {
    'name': String,
    'lvl': Number
  },
  data: function () {
    return {
      menuItems: [
        {
          title: '管理个人信息',
          onClick: () => this.$router.push('/customer/home/info')
        },
        {
          title: '管理收货地址',
          onClick: () => this.$router.push('/customer/home/address')
        },
        {
          title: '管理支付账户',
          onClick: () => this.$router.push('/customer/home/payment')
        },
        {
          title: '查看订单记录',
          onClick: () => this.$router.push('/customer/home/records')
        },
        {
          title: '退出',
          onClick: this.signOut
        },
        {
          title: '删除我的账户',
          onClick: () => this.$router.push('/customer/home/account/cancel')
        }
      ]
    }
  },
  methods: {
    signOut: function () {
      this.$ajax({
        url: '/customer/sign-out',
        method: 'post'
      }).then(() => this.$router.push('/'))
    }
  }
}
</script>

<style scoped>

</style>
