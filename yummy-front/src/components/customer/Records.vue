<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <RecordView role="customer" :types="types" :urls="urls"></RecordView>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
  </v-layout>
</template>

<script>
import RecordView from '@/components/util/RecordView'
export default {
  name: 'Records',
  components: {RecordView},
  data: function () {
    return {
      types: ['订餐记录', '消费记录', '退订记录'],
      urls: [
        '/customer/order/list/all',
        '/customer/order/list/payed',
        '/customer/order/list/cancelled'
      ]
    }
  }
}
</script>

<style scoped>

</style>
