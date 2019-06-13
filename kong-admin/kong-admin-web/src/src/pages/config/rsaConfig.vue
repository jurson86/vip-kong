<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item>RSA插件秘钥配置</el-breadcrumb-item>
		</el-breadcrumb>
		<el-card class="form-card">
			<p slot="header">RSA双向加解密秘钥配置</p>
			<el-form :model="formData" ref="form" label-width="140px" size="small" class="form">
				<el-form-item label="服务私钥">
					<el-input type="textarea" autosize v-model="formData.pri_server_key" placeholder="服务私钥"></el-input>
				</el-form-item>
				<el-form-item label="服务公钥">
					<el-input type="textarea" autosize v-model="formData.pub_server_key" placeholder="服务公钥"></el-input>
				</el-form-item>
				<el-form-item label="客户端私钥">
					<el-input type="textarea" autosize v-model="formData.pri_client_key" placeholder="客户端私钥"></el-input>
				</el-form-item>
				<el-form-item label="客户端公钥">
					<el-input type="textarea" autosize v-model="formData.pub_client_key" placeholder="客户端公钥"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="updateRsa">更新</el-button>
					<el-button @click="getRsaDetail">取消</el-button>
				</el-form-item>
			</el-form>
		</el-card>
	</div>
</template>

<script type="es6">
	import request from '../../utils/fetch.js';

	export default{
		data(){
			return{
				formData: {
					pri_server_key: '',
					pub_server_key: '',
					pri_client_key: '',
					pub_client_key: ''
				}
			}
		},

		created(){
			this.getRsaDetail();
		},

		methods: {
			getRsaDetail(){
				request.rqJson('get', '/kong/rsa/key').then(res => {
					this.formData = res;
				});
			},

			updateRsa(){
				request.rqJson('post', '/kong/rsa/key', {
					priServerKey: this.formData.pri_server_key,
					pubServerKey: this.formData.pub_server_key,
					priClientKey: this.formData.pri_client_key,
					pubClientKey: this.formData.pub_client_key
				}).then(res => {
					this.$message({
						type: 'success',
						message: '更新成功！'
					});
					this.getRsaDetail();
				})
			}
		}
	}
</script>