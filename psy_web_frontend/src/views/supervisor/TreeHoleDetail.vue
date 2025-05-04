<script setup>
import SupervisorBaseLayout from '@/components/layout/SupervisorBaseLayout.vue'
import TreeHoleDetail from '@/components/treehole/TreeHoleDetail.vue'
</script>

<template>
    <SupervisorBaseLayout>
        <div class="main-container">
            <TreeHoleDetail />
        </div>
    </SupervisorBaseLayout>
</template>

<style scoped>
.main-container {
    padding: 20px;
}
</style>