import SignUp from '@/components/restaurant/SignUp'
import Home from '@/components/restaurant/Home'
import UnhandledOrders from '@/components/restaurant/UnhandledOrders'
import InfoManage from '@/components/restaurant/InfoManage'
import MealManage from '@/components/restaurant/MealManage'
import PromotionManage from '@/components/restaurant/PromotionManage'
import Records from '@/components/restaurant/Records'
import FinancialStats from '@/components/restaurant/FinancialStats'

export default [
  {
    path: '/restaurant/sign-up',
    name: 'RestaurantSignUp',
    component: SignUp
  },
  {
    path: '/restaurant/home',
    component: Home,
    children: [
      {
        path: '',
        name: 'RestaurantHome',
        component: UnhandledOrders
      },
      {
        path: 'info',
        name: 'RestaurantInfoManage',
        component: InfoManage
      },
      {
        path: 'meal',
        name: 'RestaurantMealManage',
        component: MealManage
      },
      {
        path: 'promotion',
        name: 'RestaurantPromotionManage',
        component: PromotionManage
      },
      {
        path: 'records',
        name: 'RestaurantRecords',
        component: Records
      },
      {
        path: 'financial',
        name: 'RestaurantFinancialStats',
        component: FinancialStats
      }
    ]
  }
]
