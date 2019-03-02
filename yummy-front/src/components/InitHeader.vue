<template>
  <Header>
    <v-toolbar-items>
      <v-btn flat @click="$router.push('/')">登录</v-btn>
      <v-menu>
        <v-btn flat slot="activator">
          注册
          <v-icon>arrow_drop_down</v-icon>
        </v-btn>
        <v-list>
          <v-list-tile @click="$router.push('/customer/sign-up')">
            <v-list-tile-title>
              注册为顾客
            </v-list-tile-title>
          </v-list-tile>
          <v-list-tile @click="$router.push('/restaurant/sign-up')">
            <v-list-tile-title>
              注册为商家
            </v-list-tile-title>
          </v-list-tile>
        </v-list>
      </v-menu>
    </v-toolbar-items>
  </Header>
</template>

<script>
import Header from '@/components/util/HeaderBase'
export default {
  name: 'InitHeader',
  components: {Header}
}
</script>

<style scoped>

</style>
