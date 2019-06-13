<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item :to="{name: 'apis'}">APIs</el-breadcrumb-item>
			<el-breadcrumb-item>{{breadcrumbName}}</el-breadcrumb-item>
		</el-breadcrumb>
		<el-card class="form-card">
			<p slot="header">{{breadcrumbName}}</p>
			<el-form :model="formData" :rules="rules" ref="form" label-width="140px" size="small" class="form">
				<el-form-item label="名称" prop="name">
					<el-input v-model="formData.name" placeholder="API名称"></el-input>
				</el-form-item>
				<!-- <el-form-item label="主机">
					<el-input v-model="formData.hosts" placeholder="主机域名"></el-input>
					<p class="text">例如：example.com。至少应指定一个主机、uris或请求方式。</p>
				</el-form-item> -->
				<el-form-item label="uris" prop="uris">
					<el-input v-model="formData.uris" placeholder="API的uris前缀列表"></el-input>
					<p class="text">例如：/my-path。至少应指定一个uris。</p>
				</el-form-item>
				<el-form-item label="请求方法" prop="methods">
					<el-select v-model="formData.methods" clearable placeholder="请选择HTTP请求方式" class="select">
						<el-option value="GET"></el-option>
						<el-option value="POST"></el-option>
						<el-option value="PUT"></el-option>
						<el-option value="PATCH"></el-option>
						<el-option value="DELETE"></el-option>
						<el-option value="OPTIONS"></el-option>
						<el-option value="HEAD"></el-option>
					</el-select>
					<!-- <p class="text">例如：GET,POST。至少应指定一种请求方式。</p> -->
				</el-form-item>
				<el-form-item label="网址" prop="upstream_url">
					<el-input v-model="formData.upstream_url" placeholder="服务器地址"></el-input>
					<p class="text">该url将用于代理请求。例如：http://example.com，格式：http://{upstreamName}。</p>
				</el-form-item>
				<el-form-item label="是否去掉uris前缀">
					<el-checkbox v-model="formData.strip_uri">是</el-checkbox>
					<p class="text">当通过uris前缀匹配API时，应去掉请求地址中的前缀。默认true。</p>
				</el-form-item>
				<!-- <el-form-item label="是否保留主机头">
					<el-checkbox v-model="formData.preserve_host">是</el-checkbox>
					<p class="text">当通过主机域名匹配API时，请确保请求主机头被转发到上游服务器。默认false，自动从配置的网址中提取主机头。</p>
				</el-form-item> -->
				<el-form-item label="重连次数">
					<el-input-number v-model="formData.retries" :min="1" :max="99999"></el-input-number>
					<p class="text">未能请求成功时的重连次数。默认5次。</p>
				</el-form-item>
				<el-form-item label="连接超时时间">
					<el-input-number v-model="formData.upstream_connect_timeout" :min="1" :max="999999"></el-input-number>
					<p class="text">与服务器建立连接的超时时间（以毫秒为单位），默认60000ms。</p>
				</el-form-item>
				<el-form-item label="发送超时时间">
					<el-input-number v-model="formData.upstream_send_timeout" :min="1" :max="999999"></el-input-number>
					<p class="text">连续两次向服务器请求写操作之间的超时时间（以毫秒为单位），默认60000ms。</p>
				</el-form-item>
				<el-form-item label="读取超时时间">
					<el-input-number v-model="formData.upstream_read_timeout" :min="1" :max="999999"></el-input-number>
					<p class="text">连续两次向服务器请求读取操作之间的超时时间（以毫秒为单位），默认60000ms。</p>
				</el-form-item>
				<el-form-item label="是否只允许https">
					<el-checkbox v-model="formData.https_only">是</el-checkbox>
					<p class="text">如果想仅通过https访问API，请选用合适的端口号(默认为8443)。默认false。</p>
				</el-form-item>
				<!-- <el-form-item label="http是否终止">
					<el-checkbox v-model="formData.http_if_terminated">是</el-checkbox>
					<p class="text">当仅通过https访问时，考虑X-Forwarded-Proto请求头。默认true。</p>
				</el-form-item> -->
				<el-form-item>
					<el-button type="primary" @click="submitForm('form')">确定</el-button>
					<el-button @click="$router.go(-1)">取消</el-button>
				</el-form-item>
			</el-form>
		</el-card>
	</div>
</template>

<script type="es6">
	export default{
		data(){
			return{
				formData: {
					name: '',
					// hosts: '',
					uris: '',
					methods: '',
					upstream_url: '',
					strip_uri: true,
					// preserve_host: false,
					retries: 5,
					upstream_connect_timeout: 60000,
					upstream_send_timeout: 60000,
					upstream_read_timeout: 60000,
					https_only: false,
					// http_if_terminated: true
				},
				rules: {
					name: [	
						{ required: true, message: '名称是必填项', trigger: 'blur' }
					],
					upstream_url: [
						{ required: true, message: 'upstream_url是必填项', trigger: 'blur' }
					],
					uris: [
						{ required: true, message: '必填项', trigger: 'blur' }
					],
					methods: [
						{ required: true, message: '必选项', trigger: 'change' }
					]
				}
			}
		},

		computed: {
			breadcrumbName(){
				return this.itemName ? '编辑 API' : '添加 API';
			},

			itemName(){
				return this.$route.params.itemName;
			}
		},

		created(){
			if(this.itemName){
				this.getDetail();
			}
		},

		methods: {
			submitForm(form){
				this.$refs[form].validate((valid) => {
					if(valid){
						if(this.itemName){
							this.$patch('/apis/' + this.itemName, this.formData).then(res => {
								if(typeof(res) === 'object'){
									this.$message({
										message: '修改成功！',
										type: 'success'
									})
									setTimeout(() => {
										this.$router.push({name: 'apis'});
									}, 1000);
								}
							})
						}else{
							this.$post('/apis', this.formData).then(res => {
								if(typeof(res) === 'object'){
									this.$message({
										message: '新增成功！',
										type: 'success'
									})
									setTimeout(() => {
										this.$router.push({name: 'apis'});
									}, 1000);
								}else{
									this.$message.error('新增失败！');
								}
							})
						}
					}else{
						this.$message({
							type: 'warning',
							message: '缺少必填项或填写有误！'
						})
					}
				})
			},

			getDetail(){
				this.$get('/apis/' + this.itemName).then(res => {
					this.formData = res;
					this.formData.methods = res.methods.toString();
					this.formData.uris = res.uris.toString();
				})
			}
		}
	}
</script>