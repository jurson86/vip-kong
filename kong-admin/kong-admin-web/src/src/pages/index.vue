<template>
	<div class="wrapper">
		<el-container class="wrapper-container">
			<el-aside width="240px" class="wrapper-aside">
				<Menu></Menu>
			</el-aside>
			<el-container>
				<el-header>
					<span>kong-admin管理系统</span>
					<div style="float: right;">						
						<span style="margin-right: 10px;">欢迎您，{{userInfo}}</span>
						切换组：
						<el-select size="small" v-model="curUserGroup" @change="changeGroup" style="width: 150px; margin-right: 10px;">
							<el-option v-for="(item, index) in userList" :key="index" :value="item.group"></el-option>
						</el-select>
						<el-button type="text" icon="el-icon-edit-outline" @click="dialogFormVisible = true" style="margin-top: 10px;">修改密码</el-button>
						<el-button type="text" icon="el-icon-sold-out" @click="logout" style="margin-top: 10px;">退出系统</el-button>
					</div>
				</el-header>
				<el-main>
					<router-view></router-view>
				</el-main>
			</el-container>
		</el-container>	

		<el-dialog title="修改密码" :visible.sync="dialogFormVisible" width="500px" @close="closeDialog">
			<el-form ref="form" :model="formData" :rules="rules" label-width="100px" size="small">
				<el-form-item label="旧密码" prop="password">
					<el-input type="password" v-model="formData.password" placeholder="旧密码"></el-input>
				</el-form-item>
				<el-form-item label="新密码" prop="newPassword">
					<el-input type="password" v-model="formData.newPassword" placeholder="新密码"></el-input>
				</el-form-item>
				<el-form-item label="确认新密码" prop="confirmPassword">
					<el-input type="password" v-model="formData.confirmPassword" placeholder="确认新密码"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="submitForm('form')">确定</el-button>
					<el-button @click="dialogFormVisible = false">取消</el-button>
				</el-form-item>
			</el-form>
		</el-dialog>	
	</div>
</template>

<script type="es6">
	import Menu from '@/components/menu.vue';
	import {mapMutations, mapGetters} from 'vuex';

	export default{
		components: {
			Menu
		},

		data(){
			const validatePass = (rule, value, callback) => {
				if(!value){
					callback(new Error('请输入旧密码'));
				}else{
					callback();
				}
			};
			const validateNewPass = (rule, value, callback) => {
				if(!value){
					callback(new Error('请输入新密码'));
				}else{
					callback();
				}
			};
			const validateConfirmPass = (rule, value, callback) => {
				if(!value){
					callback(new Error('请再次输入新密码'));
				}else if(value !== this.formData.newPassword){
					callback(new Error('确认密码与新密码不一致'));
				}else{
					callback();
				}
			};
			return{
				dialogFormVisible: false,
				formData: {
					password: '',
					newPassword: '',
					confirmPassword: ''
				},
				rules: {
					password: [
						{ validator: validatePass, trigger: 'blur' }
					],
					newPassword: [
						{ validator: validateNewPass, trigger: 'blur' }
					],
					confirmPassword: [
						{ validator: validateConfirmPass, trigger: 'blur' }
					]
				},
				userList: [],
				curUserGroup: '',
				userInfo: ''
			}
		},

		created(){
			this.getUserList();

			this.userInfo = localStorage.getItem('userInfo')
		},

		computed: {
            ...mapGetters([
                'suffix'
            ])
        },

		methods: {
			...mapMutations({
                login_out: 'LOGIN_OUT',
                setCurUserGroup: 'SET_CURUSERGROUP'
            }),

			logout(){
				this.login_out();
				this.$router.push({name: 'login'});
				sessionStorage.removeItem('activeMenu');
			},

			closeDialog(){
				this.$refs.form.resetFields();
			},

			submitForm(form){
				this.$refs[form].validate((valid) => {
					if(valid){
						this.$post('/user/update_password' + this.suffix, {
							password: this.formData.password,
							newPassword: this.formData.newPassword
						}).then(res => {
							if(res.code == 0){
								this.$message({
									type: 'success',
									message: '修改成功'
								});
								setTimeout(() => {
									this.dialogFormVisible = false;
								}, 1000);
							}else{
								this.$message({
									type: 'warning',
									message: res.message
								})
							}
						})
					}
				})
			},

			getUserList(){
				this.$get('/user/group/list' + this.suffix).then(res => {
					if(res.code == 0){
						this.userList = res.data;
						for(let item of res.data){
							if(item.defaultFlag){
								this.curUserGroup = item.group;
								this.setCurUserGroup(item.group);
							}
						}
					}else{
						this.$message({
							type: 'warning',
							message: res.message
						})
					}
				})
			},

			changeGroup(val){
				this.$post('/user/group/switch_group/' + val + this.suffix).then(res => {
					if(res.code == 0){
						this.$message({
							type: 'success',
							message: '组切换成功'
						});
						this.setCurUserGroup(val);
						this.getUserList();
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