<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <RecordView role="restaurant" :types="types" :urls="urls"></RecordView>
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
      types: ['接单记录', '退订记录'],
      urls: ['/restaurant/order/list/accepted', '/restaurant/order/list/cancelled']
    }
  }
}
</script>

<style scoped>

</style>
