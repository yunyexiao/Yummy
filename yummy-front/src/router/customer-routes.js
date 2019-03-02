import SignUp from '@/components/customer/SignUp'
import EmailVerify from '@/components/customer/EmailVerify'
import Home from '@/components/customer/Home'
import RestaurantList from '@/components/customer/RestaurantList'
import Menu from '@/components/customer/Menu'
import OrderPlace from '@/components/customer/OrderPlace'
import UnhandledOrders from '@/components/customer/UnhandledOrders'
import AccountCancel from '@/components/customer/AccountCancel'
import InfoManage from '@/components/customer/InfoManage'
import AddressManage from '@/components/customer/AddressManage'
import PaymentManage from '@/components/customer/PaymentManage'
import Records from '@/components/customer/Records'

export default [
  {
    path: '/customer/sign-up',
    name: 'CustomerSignUp',
    component: SignUp
  },
  {
    path: '/customer/verify-email',
    name: 'EmailVerify',
    component: EmailVerify
  },
  {
    path: '/customer/home',
    component: Home,
    children: [
      {
        path: '',
        name: 'CustomerHome',
        component: RestaurantList
      },
      {
        path: 'menu/:rid',
        name: 'Menu',
        component: Menu
      },
      {
        path: 'order/place',
        name: 'OrderPlace',
        component: OrderPlace
      },
      {
        path: 'orders/unhandled',
        name: 'CustomerUnhandledOrders',
        component: UnhandledOrders
      },
      {
        path: 'account/cancel',
        name: 'CustomerAccountCancel',
        component: AccountCancel
      },
      {
        path: 'records',
        name: 'CustomerRecords',
        component: Records
      },
      {
        path: 'info',
        name: 'CustomerInfoManage',
        component: InfoManage
      },
      {
        path: 'address',
        name: 'CustomerAddressManage',
        component: AddressManage
      },
      {
        path: 'payment',
        name: 'CustomerPaymentManage',
        component: PaymentManage
      }
    ]
  }
]
