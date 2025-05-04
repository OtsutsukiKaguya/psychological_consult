<script setup>
import SupervisorBaseLayout from '@/components/layout/SupervisorBaseLayout.vue'
import TreeHoleList from '@/components/treehole/TreeHoleList.vue'
</script>

<template>
    <SupervisorBaseLayout>
        <div class="main-container">
            <TreeHoleList />
        </div>
    </SupervisorBaseLayout>
</template>

<style scoped>
.main-container {
    padding: 20px;
}
</style>