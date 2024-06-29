import { createGlobalState, useStorage } from '@vueuse/core'

export const useTokenState = createGlobalState(
  () => useStorage('admin-token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NSwibmFtZSI6IkFkbWluIiwiYWRtaW4iOnRydWUsImlhdCI6MTcxOTU3NzQzN30.Pl7toYMeGEaZYlSeneA1tXsAL8Nf8xo7JI_J1SNnwg0'),
)