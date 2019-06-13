<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item :to="{name: 'plugins'}">插件</el-breadcrumb-item>
			<el-breadcrumb-item>{{breadcrumbName}}</el-breadcrumb-item>
		</el-breadcrumb>
		<el-card class="form-card">
			<p slot="header">{{breadcrumbName}}</p>
			<el-form :model="formData" :rules="rules" ref="form" label-width="140px" size="small" class="form">
				<el-form-item label=""></el-form-item>
			</el-form>
		</el-card>
	</div>
</template>

<script type="es6">
	export default{
		data(){
			return{
				formData: {},
				rules: {}
			}
		},

		computed: {
			breadcrumbName(){
				return this.itemName ? '修改' : '新增';
			},

			itemName(){
				return this.$route.params.name;
			}
		}
	}
</script>