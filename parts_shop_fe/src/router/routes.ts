import type {RouteRecordRaw} from 'vue-router';
import MainLayout from 'layouts/MainLayout.vue';
import {EMAIL_CHECK_PATH, LOGIN_PATH, ORDERS_PATH} from "@/constants/routes";

const loadPage = (page: string) => () =>
  import(`pages/${page}.vue`).catch((err) => {
    console.error(`Failed to load page: ${page}`, err);
    throw err;
  });

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: MainLayout,
    children: [{ path: '', component: loadPage('HomePage'), meta: { title: 'Home' } }],
  },

  {
    path: '/login',
    component: MainLayout,
    children: [{ path: '', component: loadPage('LoginPage'), meta: { title: 'Login' } }],
  },

  {
    path: '/register',
    component: MainLayout,
    children: [{ path: '', component: loadPage('RegisterPage'), meta: { title: 'Register' } }],
  },

  {
    path: '/profile',
    component: MainLayout,
    children: [
      {
        path: '',
        component: loadPage('ProfilePage'),
        meta: { requiresAuth: true, title: 'Profile' },
      },
      {
        path: 'update',
        component: loadPage('UpdateProfilePage'),
        meta: { requiresAuth: true, title: 'Profile Update' },
      },
    ],
  },

  {
    path: '/password-change',
    component: MainLayout,
    children: [
      {
        path: '',
        component: loadPage('ChangePasswordPage'),
        meta: { requiresAuth: true, title: 'Change password' },
      },
    ],
  },

  {
    path: '/password-reset',
    component: MainLayout,
    children: [
      {
        path: '',
        component: loadPage('PasswordResetPage'),
        meta: { requiresAuth: true, title: 'Reset Password' },
      },
    ],
  },
  {
    path: '/reset-password',
    component: MainLayout,
    children: [
      {
        path: '',
        component: loadPage('ChangePasswordPage'),
        meta:  { title: 'Reset Password' },
        beforeEnter: (to, from, next) => {
          if (to.query.email === null || to.query.email === undefined) {
            next(LOGIN_PATH); // Redirect to the home page
          } else {
            to.meta.requiresAuth = true;
            next();
          }
        }
      },
    ],
  },
  {
    path: EMAIL_CHECK_PATH,
    component: MainLayout,
    children: [
      {
        path: '',
        component: loadPage('CheckEmail'),
        meta: { requiresAuth: false, title: 'Check Email' },
        props: true
      },
    ],
  },
  {
    path: '/cart',
    component: MainLayout,
    children: [{ path: '', component: loadPage('ShoppingCart'), meta: { title: 'Cart' } }],
  },

  {
    path: '/products',
    component: MainLayout,
    children: [
      { path: '', component: loadPage('ProductsPage'), meta: { title: 'Products' } },
      {
        path: ':slug',
        component: loadPage('SingleProductPage'),
        props: true,
        meta: { title: 'Product Details' },
      },
    ],
  },
  {
    path: '/products-edit',
    component: MainLayout,
    children: [
      { path: '', component: loadPage('ProductsEditor'), meta: { title: 'Products Edit' } },
    ],
  },
  {
    path: '/category-edit',
    component: MainLayout,
    children: [
      { path: '', component: loadPage('CategoryEditor'), meta: { title: 'Products' } },
    ],
  },

  {
    path: '/checkout',
    component: MainLayout,
    children: [
      { path: '', component: loadPage('CheckoutPage'), meta: { title: 'Checkout' } },
      {
        path: 'order-overview',
        component: loadPage('OrderOverviewPage'),
        meta: { title: 'Order' },
      },
    ],
  },

  {
    path: '/thankyou',
    component: MainLayout,
    children: [{ path: '', component: loadPage('ThankYouPage'), meta: { title: 'Thank You!' } }],
  },

  {
    path: '/settings',
    component: MainLayout,
    children: [{ path: '', component: loadPage('SettingsPage'), meta: { title: 'Settings' } }],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: MainLayout,
    children: [{ path: '', component: loadPage('ErrorPage'), meta: { title: 'Not Found' } }],
  },
  {
    path: ORDERS_PATH,
    component: MainLayout,
    children: [
      {
        path: ':id',
        component: loadPage('OrderDetailsComponent'),
        props: true,
        meta: { title: 'Order Details' },
      },
    ],
  }
];

export default routes;
