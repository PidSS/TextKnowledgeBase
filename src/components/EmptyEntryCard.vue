<script setup>
import { ref, onMounted } from "vue"
import { NScrollbar, NCard, NButton, NText, NP, NFlex, NInput, NSpace, useMessage } from "naive-ui"
import { get, post } from "@/utils/vfetch"

const editing = ref(false)

const message = useMessage()

const form = ref({})

const emit = defineEmits(['data-updated'])
async function createEntry() {
    await post("/admin/createEntry", form.value)
    message.success("发布成功")
    editing.value = false
    emit('data-updated')
    form.value = {}
}
</script>

<template>
    <n-button v-if="!editing"
        secondary strong type="primary"
        class="mb-6 w-full h-20"
        @click="editing = true"
    >
        新增
    </n-button>
    <n-card v-else
        class="mb-6"
        title="新知识条目"
        :segmented="{
            content: true
        }"
    >
        <n-space vertical>
            <n-input type="text" v-model:value="form.name" placeholder="标题" />
            <n-input type="textarea" v-model:value="form.introduction" placeholder="简介" />
            <n-input type="textarea" v-model:value="form.content" placeholder="正文" />
        </n-space>
        <template #action>
            <n-flex justify="end">
                <n-button secondary strong
                    @click="editing = false"
                >
                    取消
                </n-button>
                <n-button strong type="primary"
                    @click="createEntry"
                >
                    发布
                </n-button>
            </n-flex>
        </template>
    </n-card>
</template>