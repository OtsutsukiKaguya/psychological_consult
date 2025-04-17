<!-- 咨询师树洞详情页面 -->
<template>
    <ConsultantBaseLayout :active-menu="'/consultant/tree-hole'">
        <div class="main-container">
            <TreeHoleDetail />
        </div>
    </ConsultantBaseLayout>
</template>

<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import TreeHoleDetail from '@/components/treehole/TreeHoleDetail.vue'
</script>

<style scoped>
.main-container {
    padding: 20px;
}
</style>