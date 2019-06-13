<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item :to="{name: 'userList'}">用户列表</el-breadcrumb-item>
			<el-breadcrumb-item>{{titleName}}用户</el-breadcrumb-item>
		</el-breadcrumb>
		<el-card class="form-card">
			<p slot="header">{{titleName}}</p>
			<el-form :model="formData" ref="form" :rules="rules" label-width="140px" size="small" class="form">
				<el-form-item label="真实姓名" prop="realName">
					<el-input v-model="formData.realName" placeholder="真实姓名"></el-input>
				</el-form-item>
				<el-form-item label="登录账号" prop="username">
					<el-input v-model="formData.username" :disabled="disabled" placeholder="登录账号"></el-input>
				</el-form-item>
				<el-form-item label="密码" v-if="!itemId" prop="password">
					<el-input type="password" v-model="formData.password" placeholder="密码"></el-input>
				</el-form-item>
				<el-form-item label="用户号码">
					<el-input v-model="formData.mobile" placeholder="用户号码"></el-input>
				</el-form-item>
				<el-form-item label="组" prop="groups">
					<el-select multiple v-model="formData.groups" placeholder="请选择" class="select">
						<el-option v-for="(item, index) in groupList" :key="index" :value="item.group"></el-option>
					</el-select>					
				</el-form-item>
				<!-- <el-form-item label="用户邮箱">
					<el-input v-model="formData.email" placeholder="email"></el-input>
				</el-form-item> -->
				<el-form-item label="状态">
					<el-select v-model="formData.status" class="select">
						<el-option :value="0" label="禁用"></el-option>
						<el-option :value="1" label="启用"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="submitForm('form')">确定</el-button>
					<el-button @click="$router.go(-1)">取消</el-button>
				</el-form-item>
			</el-form>
		</el-card>
	</div>
</template>

<script type="es6">
	import { mapGetters } from 'vuex';

	export default{
		data(){
			return{
				canClick: true,
				disabled: false,
				formData: {
					realName: '',
					username: '',
					// email: '',
					mobile: '',
					password: '',
					status: 1,
					groups: []
				},
				groupList: [],
				rules: {
					realName: [
						{ required: true, message: '必填项', trigger: 'blur' }
					],
					username: [
						{ required: true, message: '必填项', trigger: 'blur' }
					],
					password: [
						{ required: true, message: '必填项', trigger: 'blur' }
					],
					groups: [
						{ required: true, message: '必选项', trigger: 'change' }
					]
				}
			}
		},

		computed: {
			itemId(){
				return this.$route.params.id;
			},

			titleName(){
				return this.itemId ? '修改' : '添加';
			},

			...mapGetters([
				'suffix'
			])
		},

		created(){
			if(this.itemId){
				this.getCurGroup();
				this.getUserDetail();	
				this.disabled = true;			
			}

			this.getGroupList();
		},

		methods: {
			submitForm(form){
				let url = this.itemId ? '/user/update' : '/user/add';
				this.$refs.form.validate((valid) => {
					if(valid && this.canClick){
						this.canClick = false;
						this.$post(url + this.suffix, this.formData).then(res => {
							if(res.code == 0){
								this.$message({
									type: 'success',
									message: this.titleName + '成功'
								});
								setTimeout(() => {
									this.$router.go(-1);
								}, 1000);
							}else{
								this.canClick = true;
								this.$message({
									type: 'warning',
									message: res.message
								})
							}
						})
					}
				})
			},

			getUserDetail(){
				this.$get('/user/info/' + this.itemId + this.suffix).then(res => {
					if(res.code == 0){
						for(let key in res.data){
							this.formData[key] = res.data[key];
						}
						this.formData.userId = res.data.userId;
					}else{
						this.$message({
							type: 'warning',
							message: res.message
						})
					}
				})
			},

			getGroupList(){
				this.$get('/user/group/list' + this.suffix).then(res => {
					if(res.code == 0){
						this.groupList = res.data;
					}else{
						this.$message({
							type: 'warning',
							message: res.message
						})
					}
				})
			},

			getCurGroup(){
				this.$get('/user/group/list/' + this.itemId + this.suffix).then(res => {
					if(res.code == 0){
						for(let item of res.data){
							this.formData.groups.push(item.group);
						}
					}else{
						this.$message({
							type: 'warning',
							message: res.message
						})
					}
				})
			}
		}
	}
</script>