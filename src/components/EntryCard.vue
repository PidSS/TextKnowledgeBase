<script setup>
import { ref, onMounted } from "vue"
import { NScrollbar, NCard, NButton, NText, NP, NFlex, NInput, NSpace, useMessage } from "naive-ui"
import { get, post } from "@/utils/vfetch"

const message = useMessage()

const props = defineProps({
    entry: {
        type: Object,
        required: true
    }
})

const editing = ref(false)

const form = ref({
    id: props.entry.id,
    name: props.entry.name,
    introduction: props.entry.introduction,
    content: props.entry.content
})

const emit = defineEmits(['data-updated'])
async function updateEntry() {
    await post("/admin/updateEntry", form.value)
    message.success("更新成功")
    editing.value = false
    emit('data-updated')
}

async function deleteEntry() {
    console.log(form.value.id)
    await post("/admin/deleteEntry", { id: form.value.id })
    message.success("删除成功")
    emit('data-updated')
}
</script>

<template>
    <n-card v-if="!editing"
        class="mb-6"
        :title="entry.name"
        :segmented="{
            content: true
        }"
    >
        <template #header-extra>
            # {{ entry.id }}
        </template>
        <!-- @todo 添加换行效果 优化文本显示 -->
        <n-p class="relative pl-5 opacity-60 after:absolute after:inset-0 after:w-1 after:bg-current after:opacity-50">
            {{ entry.introduction }}
        </n-p>
        <n-p class=""> 
            {{ entry.content }}
        </n-p>
        <template #action>
            <n-flex justify="end">
                <n-button secondary strong type="info"
                    @click="editing = true"
                >
                    修改
                </n-button>
                <n-button secondary strong type="error"
                    @click="deleteEntry"
                >
                    删除
                </n-button>
            </n-flex>
        </template>
    </n-card>
    <n-card v-else
        class="mb-6"
        :title="`编辑知识条目 - #${entry.id}`"
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
                    放弃修改
                </n-button>
                <n-button strong type="primary"
                    @click="updateEntry"
                >
                    更新
                </n-button>
            </n-flex>
        </template>
    </n-card>
</template>