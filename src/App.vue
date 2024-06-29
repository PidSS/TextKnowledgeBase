<script setup>
import { ref, h } from "vue";
import { RouterLink, RouterView } from 'vue-router'
import { UserFriends } from "@vicons/fa";
import { Book20Filled } from "@vicons/fluent";
import { FeedbackFilled } from "@vicons/material";
import { NLayout, NLayoutHeader, NPageHeader, NLayoutSider, NFlex, NIcon, NMenu, NButton, NDialogProvider, NMessageProvider } from 'naive-ui';

const collapsed = ref(true)

function renderIcon (icon) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

const menuOptions = [
  {
    label: () => h(
      RouterLink,
      { to: "/users" },
      { default: () => "用户信息管理" }
    ),
    key: "user",
    icon: renderIcon(UserFriends) 
  },
  {
    label: () => h(
      RouterLink,
      { to: "/entries" },
      { default: () => "知识条目管理" }
    ),
    key: "entry",
    icon: renderIcon(Book20Filled) 
  },
  // {
  //   label: () => h(
  //     RouterLink,
  //     { to: "/users" },
  //     { default: () => "反馈内容管理" }
  //   ),
  //   key: "feedback",
  //   icon: renderIcon(FeedbackFilled) 
  // }
]

//@todo 添加刷新页面时的高亮

</script>

<template>
  <n-dialog-provider><n-message-provider placement="bottom-right">
      <n-layout-header bordered class="px-6">
        <n-page-header subtitle="后端管理">
          <template #header>{{  }}</template>
          <template #title>
            文本知识库
          </template>
          <template #extra>
            <n-button strong secondary>Admin</n-button>
          </template>
          <template #footer>{{  }}</template>
        </n-page-header>
      </n-layout-header>
      <n-layout has-sider class="flex-1">
        <n-layout-sider
          bordered
          collapse-mode="width"
          :collapsed-width="64"
          :width="240"
          :collapsed="collapsed"
          show-trigger
          @collapse="collapsed = true"
          @expand="collapsed = false"
        >
          <n-menu
            class="*:font-bold"
            :collapsed="collapsed"
            :collapsed-width="64"
            :collapsed-icon-size="22"
            :options="menuOptions"
          />
        </n-layout-sider>
        <n-layout class="px-6 py-6">
          <RouterView />
        </n-layout>
      </n-layout>
  </n-message-provider></n-dialog-provider>
</template>

<style scoped>
</style>
