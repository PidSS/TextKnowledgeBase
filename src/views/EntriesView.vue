<script setup>
import { ref, onMounted } from "vue"
import { NScrollbar, NCard, NButton, NText, NP, NFlex } from "naive-ui"
import EntryCard from "@/components/EntryCard.vue"
import EmptyEntryCard from "@/components/EmptyEntryCard.vue"
import { get, post } from "@/utils/vfetch"

const entryList = ref([])

onMounted( async () => {
    entryList.value = await get("/entry/list")
})

async function refetch() {
    entryList.value = await get("/entry/list")
}

</script>

<template>
    <n-scrollbar>
        <EmptyEntryCard
            @data-updated="refetch"
        />
        <EntryCard v-for="entry in entryList"
            :entry="entry"
            @data-updated="refetch"
        />
    </n-scrollbar>
</template>