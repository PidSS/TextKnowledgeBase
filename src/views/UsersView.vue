<script setup>
import { ref, onMounted, computed, h } from 'vue';
import { NGrid, NGridItem, NDataTable, NAvatar, NIcon, NButton, useDialog, useMessage } from 'naive-ui';
import { UserSecret } from "@vicons/fa";
import { get, post } from "../utils/vfetch"

const dialog = useDialog()
const message = useMessage()

const sleep = (seconds) => new Promise((resolve) => setTimeout(resolve, seconds * 1000))

function Avatar(url) {
  return h(
    NAvatar,
    {
      round: true,
      size: "medium",
      src: `http://joi.work${url}`
    }
  )
}

const AdminIcon = h(
  NIcon,
  { size: "20", class: "text-rose-400" },
  { default: () => h(UserSecret) }
)

function deleteUser(id) {
  const d = dialog.warning({
    title: "警告",
    content: "数据删除后无法恢复",
    positiveText: '删除',
    negativeText: '取消',
    negativeButtonProps: {
      secondary: true,
      ghost: false,
      type: "default"
    },
    onPositiveClick: async () => {
      d.loading = true
      try {
        console.log(id)
        await post("/admin/deleteUser", { id: id })
        message.success("删除成功")
        userList.value = await get("/admin/listUsers")
      } catch (e) {
        message.error("删除失败")
      }
    },
    closable: false
  })
}

const DeleteButton = (id) => h(
  NButton,
  {
    ghost: true,
    strong: true,
    type: "error",
    onClick: () => deleteUser(id)
  },
  { default: () => "删除" }
)

const userList = ref([])

onMounted( async () => {
  userList.value = await get("/admin/listUsers")
})

const table_columns = [
  { title: "#", key: "id" },
  { title: "头像", key: "avatar" },
  { title: "用户名", key: "name" },
  { title: "身份", key: "admin" },
  { title: "操作", key: "action" }
]

const table_data = computed(() => 
  userList.value.map(item => ({
    id: item.id,
    avatar: Avatar(item.avatar),
    name: item.name,
    admin: item.admin ? AdminIcon : "",
    action: DeleteButton(item.id)
  }))
)

</script>

<template>
  <n-data-table
    :columns="table_columns"
    :data="table_data"
  >
  </n-data-table>
</template>
