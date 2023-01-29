<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div>
    <!-- 头部区域 -->
    <div style="height: 60px; line-height: 60px; background-color: white; margin-bottom: 2px; display: flex">
      <div style="width: 300px">
        <img src="@/assets/logo.svg" alt="" style="width: 40px; position: relative; top: 10px; left: 20px">
        <span style="margin-left: 25px; font-size: 24px">旅游推荐系统</span>
      </div>
    </div>

    <!-- 侧边栏和主体 -->
    <div style="display: flex ">
      <!-- 侧边栏导航 -->
      <el-scrollbar
        style="height:600px;width: 200px; min-height: calc(100vh - 62px); overflow: hidden; margin-right: 2px; background-color: white">
        <el-menu :default-active="$route.path" router class="el-menu-demo">
          <el-menu-item index="/">
            <el-icon>
              <View />
            </el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-sub-menu index="/province">
            <template #title>
              <el-icon>
                <Star />
              </el-icon>
              <span>省份</span>
            </template>
            <el-sub-menu v-for="item in this.menuList" :index="item.name">
              <template #title>{{ item.name }}</template>
              <el-menu-item v-for="item1 in item.province" :key="item1" :index="item1"
                :route="{ name: 'TourProduct', query: { value: item1 } }">
                {{ item1 }}
              </el-menu-item>
            </el-sub-menu>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>

    </div>
  </div>
  <RouterView :key="key">
  </RouterView>
</template>
<script>
export default {
  data() {
    return {
      menuList: [
        {
          name: "东北",
          province: ["黑龙江", "吉林", "辽宁"]
        },
        {
          name: "华东",
          province: ["上海", "江苏", "浙江", "安徽", "福建", "江西", "山东", "台湾"]
        },
        {
          name: "华北",
          province: ["北京", "天津", "山西", "河北", "内蒙古"]
        },
        {
          name: "华南",
          province: ["广东", "广西", "海南", "香港", "澳门"]
        },
        {
          name: "西南",
          province: ["四川", "贵州", "云南", "重庆", "西藏"]
        },
        {
          name: "西北",
          province: ["陕西", "甘肃", "青海", "宁夏", "新疆"]
        }
      ]
    }
  },
  methods: {
  },
  created() {
  },
  //给router-view添加唯一key，否则每次跳转路由时，由于是跳转到同一组件，数据不会更新，所欲需要独有key标识
  computed: {
    key() {
      return this.$route.path + Math.random();
    }
  },
}
</script>
<style lang="less" scoped>

</style>

<style scoped>

</style>
