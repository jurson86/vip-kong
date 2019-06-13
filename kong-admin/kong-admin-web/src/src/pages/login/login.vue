<template>
	<transition name="fade">
        <div class="login">
            <div class="login-con">
                <el-card>
                	<div slot="header"><i class="el-icon-d-arrow-right"></i> kong-admin管理系统</div>
                	<div class="form-con"></div>
                	<el-form ref="formInline" :model="formInline" :rules="ruleInline">
                		<el-form-item prop="username">
                			<el-input v-model="formInline.username" placeholder="账号"></el-input>
                		</el-form-item>
                		<el-form-item prop="password">
                			<el-input type="password" v-model="formInline.password" placeholder="密码"></el-input>
                		</el-form-item>
                		<el-form-item prop="captcha">
                			<el-input v-model="formInline.captcha" placeholder="验证码" @keyup.enter.native="handleSubmit('formInline')"></el-input>
                			<img @click="handleRefreshCode" :src="codeSrc" alt="点击重新获取">
                		</el-form-item>
                		<el-form-item>
                			<el-button type="primary" @click="handleSubmit('formInline')" style="width: 100%">登录</el-button>
                		</el-form-item>
                	</el-form>
                </el-card>
            </div>
        </div>
    </transition>
</template>

<script type="es6">
	import {mapMutations, mapGetters} from 'vuex';
    import config from '@/utils/config';

	export default{
		data(){
			return{
				formInline: {
                    username: '',
                    password: '',
                    captcha: ''
                },
                ruleInline: {
                    username: [
                        {required: true, message: '请输入账号', trigger: 'blur'}
                    ],
                    password: [
                        {required: true, message: '请输入密码', trigger: 'blur'},
                        {
                            type: 'string',
                            min: 3,
                            message: '密码长度不能小于3位',
                            trigger: 'blur'
                        }
                    ],
                    captcha: [
                    	{ required: true, message: '请输入验证码', trigger: 'blur' }
                    ]
                },
                codeSrc: config.baseUrl + '/captcha.jpg?t=' + new Date().getTime()
			}
		},

		computed: {
            ...mapGetters([
                'token',
                'suffix'
            ])
        },

        created(){
        	if(this.token){
        		this.toLogin();
        	}
        },

        watch: {
        	token(newval){
        		if(newval){
        			this.toLogin();
        		}
        	}
        },

		methods: {
			handleRefreshCode() {
                this.codeSrc = `/captcha.jpg?t=${new Date().getTime()}`
            },

			handleSubmit(name) {
                this.$refs[name].validate((valid) => {
                    if (valid) {
                        this.$post('/login' + this.suffix, this.formInline).then(res => {
                            if (res.code === 0) {
                                this.login(res.data.token);
                                this.setUserInfo(res.data.username);
                                this.$router.push(this.$route.query.redirect ? this.$route.query.redirect : '/index')
                                this.$message({
                                	type: 'success',
                                    message: '登录成功!'
                                });
                            } else {
                                this.$message({
                                	type: 'error',
                                	message: res.message
                                });
                            }
                        })
                    }
                })
            },

            toLogin(){
            	this.$router.push(this.$route.query.redirect ? this.$route.query.redirect : '/index');
            },

            ...mapMutations({
                login: 'HAS_TOKEN',
                setUserInfo: 'SET_USERINFO'
            })
		}
	}
</script>

<style lang="scss">
    .login{
        width: 100%;
        height: 100%;
        background-image: url('../../assets/images/bg.jpg');
        background-size: cover;
        background-position: center;
        position: relative;
        &-con{
            position: absolute;
            right: 260px;
            top: 50%;
            transform: translateY(-60%);
            width: 320px;
            .form-con{
                padding: 10px 0 0;
            }
        }

        .el-form-item:nth-child(3){
            .el-form-item__content{
                position: relative;
                .el-input {
                    position: relative;
                    width: 150px;
                }
                img{
                    display: block;
                    width: 120px;
                    height: 40px;
                    position: absolute;
                    right: 0;
                    top: 0;
                    border-radius: 4px;
                    cursor: pointer;
                    text-align: center;
                    font-size: 14px;
                    color: #fff;
                }
            }
        }
    }
</style>
