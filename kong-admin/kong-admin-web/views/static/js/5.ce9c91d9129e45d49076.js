webpackJsonp([5],{QlWu:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=r("Dd8w"),s=r.n(n),a=r("9rMa"),i=r("bzuE"),o={data:function(){return{formInline:{username:"",password:"",captcha:""},ruleInline:{username:[{required:!0,message:"请输入账号",trigger:"blur"}],password:[{required:!0,message:"请输入密码",trigger:"blur"},{type:"string",min:3,message:"密码长度不能小于3位",trigger:"blur"}],captcha:[{required:!0,message:"请输入验证码",trigger:"blur"}]},codeSrc:i.a.baseUrl+"/captcha.jpg?t="+(new Date).getTime()}},computed:s()({},Object(a.b)(["token","suffix"])),created:function(){this.token&&this.toLogin()},watch:{token:function(e){e&&this.toLogin()}},methods:s()({handleRefreshCode:function(){this.codeSrc="/captcha.jpg?t="+(new Date).getTime()},handleSubmit:function(e){var t=this;this.$refs[e].validate(function(e){e&&t.$post("/login"+t.suffix,t.formInline).then(function(e){0===e.code?(t.login(e.data.token),t.setUserInfo(e.data.username),t.$router.push(t.$route.query.redirect?t.$route.query.redirect:"/index"),t.$message({type:"success",message:"登录成功!"})):t.$message({type:"error",message:e.message})})})},toLogin:function(){this.$router.push(this.$route.query.redirect?this.$route.query.redirect:"/index")}},Object(a.c)({login:"HAS_TOKEN",setUserInfo:"SET_USERINFO"}))},l={render:function(){var e=this,t=e.$createElement,r=e._self._c||t;return r("transition",{attrs:{name:"fade"}},[r("div",{staticClass:"login"},[r("div",{staticClass:"login-con"},[r("el-card",[r("div",{attrs:{slot:"header"},slot:"header"},[r("i",{staticClass:"el-icon-d-arrow-right"}),e._v(" kong-admin管理系统")]),e._v(" "),r("div",{staticClass:"form-con"}),e._v(" "),r("el-form",{ref:"formInline",attrs:{model:e.formInline,rules:e.ruleInline}},[r("el-form-item",{attrs:{prop:"username"}},[r("el-input",{attrs:{placeholder:"账号"},model:{value:e.formInline.username,callback:function(t){e.$set(e.formInline,"username",t)},expression:"formInline.username"}})],1),e._v(" "),r("el-form-item",{attrs:{prop:"password"}},[r("el-input",{attrs:{type:"password",placeholder:"密码"},model:{value:e.formInline.password,callback:function(t){e.$set(e.formInline,"password",t)},expression:"formInline.password"}})],1),e._v(" "),r("el-form-item",{attrs:{prop:"captcha"}},[r("el-input",{attrs:{placeholder:"验证码"},nativeOn:{keyup:function(t){if(!("button"in t)&&e._k(t.keyCode,"enter",13,t.key,"Enter"))return null;e.handleSubmit("formInline")}},model:{value:e.formInline.captcha,callback:function(t){e.$set(e.formInline,"captcha",t)},expression:"formInline.captcha"}}),e._v(" "),r("img",{attrs:{src:e.codeSrc,alt:"点击重新获取"},on:{click:e.handleRefreshCode}})],1),e._v(" "),r("el-form-item",[r("el-button",{staticStyle:{width:"100%"},attrs:{type:"primary"},on:{click:function(t){e.handleSubmit("formInline")}}},[e._v("登录")])],1)],1)],1)],1)])])},staticRenderFns:[]};var c=r("VU/8")(o,l,!1,function(e){r("UgjC")},null,null);t.default=c.exports},UgjC:function(e,t){}});