<template>
	<el-menu class="el-menu-vertical" :default-active="activeMenu" background-color="#fdfdfd" text-color="#333" active-text-color="#409eff" @select="handleSelect">
		<el-menu-item index="home">
			<i class="el-icon-location"></i>
			<span slot="title">首页</span>
		</el-menu-item>
		<el-menu-item index="apis">
			<i class="el-icon-menu"></i>
			<span slot="title">APIs</span>
		</el-menu-item>
		<el-menu-item index="upstreams">
			<i class="el-icon-printer"></i>
			<span slot="title">服务</span>
		</el-menu-item>
		<!-- <el-submenu index="plugin">
			<template slot="title">
				<i class="el-icon-setting"></i>
				<span slot="title">插件</span>
			</template>
			<el-menu-item index="plugins">
				<i class="el-icon-document"></i>
				<span>插件1</span>
			</el-menu-item>
			<el-menu-item index="">
				<i class="el-icon-document"></i>
				<span>插件2</span>
			</el-menu-item>
		</el-submenu> -->
		<!-- <el-submenu index="config">
			<template slot="title">
				<i class="el-icon-document"></i>
				<span>配置</span>
			</template>
			<el-menu-item index="rsaConfig">
				<i class="el-icon-more-outline"></i>
				<span>RSA插件秘钥配置</span>
			</el-menu-item>
		</el-submenu> -->

		<el-menu-item index="userList">
			<i class="el-icon-document"></i>
			<span slot="title">用户列表</span>
		</el-menu-item>				
	</el-menu>
</template>

<script type="es6">
	export default{
		data(){
			return{
				activeMenu: ''
			}
		},

		created(){
			const activeMenu = sessionStorage.getItem('activeMenu');
			if(activeMenu){
				this.activeMenu = activeMenu;
			}
		},

		methods: {
			handleSelect(url){
				sessionStorage.setItem('activeMenu', url);
				this.$router.push({name: url});
			}
		}
	}
</script>