import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home,
      children: [
        {
          path: '/',
          name: 'china',
          component: () => import('@/views/China.vue'),
        },
        {
          path: '/tourProduct',
          name: 'TourProduct',
          component: () => import('@/views/TourProduct.vue'),
        },
        {
          path: '/comments',
          name: 'comments',
          component: () => import('@/views/Comments.vue'),
        }
      ]
    },
  ]
})

export default router
