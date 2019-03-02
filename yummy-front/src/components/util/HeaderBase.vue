<template>
  <v-flex xs12 style="margin-bottom: 80px">
    <v-toolbar dark color="primary" app>
      <v-toolbar-title>Yummy外卖平台</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-toolbar-items>
        <slot></slot>
      </v-toolbar-items>
    </v-toolbar>
  </v-flex>
</template>

<script>
export default {
  name: 'HeaderBase'
}
</script>

<style scoped>

</style>
