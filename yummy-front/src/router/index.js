import Vue from 'vue'
import Router from 'vue-router'
import SignIn from '@/components/SignIn'
import customerRoutes from '@/router/customer-routes'
import restaurantRoutes from '@/router/restaurant-routes'
import adminRoutes from '@/router/admin-routes'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'SignIn',
      component: SignIn
    }
  ].concat(customerRoutes)
    .concat(restaurantRoutes)
    .concat(adminRoutes)
})
