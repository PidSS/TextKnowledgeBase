import { createRouter, createWebHistory } from 'vue-router'
import UsersView from '../views/UsersView.vue'
import EntriesView from '../views/EntriesView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/users',
      name: 'users',
      component: UsersView
    },
    {
      path: '/entries',
      name: 'entries',
      component: EntriesView
    }
  ]
})

export default router
