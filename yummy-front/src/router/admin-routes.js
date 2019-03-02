import Home from '@/components/admin/Home'
import OrderSettle from '@/components/admin/OrderSettle'
import RestaurantCheck from '@/components/admin/RestaurantCheck'
import Statistics from '@/components/admin/Statistics'

export default [
  {
    path: '/admin',
    component: Home,
    children: [
      {
        path: '',
        name: 'AdminOrderSettle',
        component: OrderSettle
      },
      {
        path: 'restaurants',
        name: 'AdminRestaurantCheck',
        component: RestaurantCheck
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: Statistics
      }
    ]
  }
]
