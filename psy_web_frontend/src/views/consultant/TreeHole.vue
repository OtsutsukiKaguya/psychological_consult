<!-- 咨询师树洞页面 -->
<template>
    <ConsultantBaseLayout>
        <div class="main-container">
            <TreeHoleList />
        </div>
    </ConsultantBaseLayout>
</template>

<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import TreeHoleList from '@/components/treehole/TreeHoleList.vue'
</script>

<style scoped>
.main-container {
    padding: 20px;
}
</style>