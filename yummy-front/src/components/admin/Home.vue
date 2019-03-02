<template>
  <div>
    <AdminHeader></AdminHeader>
    <router-view></router-view>
  </div>
</template>

<script>
import AdminHeader from '@/components/admin/Header'
export default {
  name: 'Home',
  components: {AdminHeader}
}
</script>

<style scoped>

</style>
