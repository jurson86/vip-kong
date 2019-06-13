webpackJsonp([6],{nU8l:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var s=n("Dd8w"),r=n.n(s),a=n("9rMa"),o={mixins:[n("ScGb").a],data:function(){return{dialogFormVisible:!1,kongUrlList:[],formData:{kongUrl:""},rules:{kongUrl:[{required:!0,message:"必填项",trigger:"blur"}]}}},computed:r()({},Object(a.b)(["kongAdminUrl","curUserGroup"])),created:function(){this.getKongUrl(),this.getKongUrlList()},watch:{curUserGroup:function(e){e&&(this.getKongUrlList(),this.getKongUrl())}},methods:{getKongUrlList:function(){var e=this;this.kongUrlList=[],this.$get("/kong/url/list"+this.suffix).then(function(t){0==t.code?e.kongUrlList=t.data:e.$message({type:"info",message:t.message})})},tableRowClassName:function(e){var t=e.row;e.rowIndex;return t.server==this.kongAdminUrl?"primary-row":""},changeKongUrl:function(e){var t=this;this.$confirm("确定修改吗？","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){t.$post("/user/group/bind_server"+t.suffix,{server:e,groupName:t.curUserGroup}).then(function(e){0==e.code?(t.$message({type:"success",message:"修改kong服务成功！"}),t.getKongUrl(),t.getKongUrlList()):t.$message({type:"warning",message:e.message})})}).catch(function(){t.$message({type:"info",message:"已取消修改"})})},closeDialog:function(){this.$refs.form.resetFields()},clearCache:function(){var e=this;this.$confirm("清理缓存时，确保当前网关无流量进入！确认清理全部缓存吗？","提示",{type:"warning",confirmButtonText:"确定",cancelButtonText:"取消"}).then(function(){e.$delete("/kong/delete/caches"+e.suffix).then(function(t){0==t.data.code?(e.$message({type:"success",message:"缓存清理成功！"}),setTimeout(function(){e.getKongUrlList()},1e3)):e.$message({type:"warning",message:t.data.message})})}).catch(function(){e.$message({type:"info",message:"已取消"})})},clearSingleCache:function(e){var t=this;this.$confirm("清理缓存时，确保当前网关无流量进入！确认清理缓存吗？","提示",{type:"warning",confirmButtonText:"确定",cancelButtonText:"取消"}).then(function(){t.$delete("/kong/delete/cache?kongUrl="+e+t.suffix).then(function(e){console.info(e),0==e.data.code?(t.$message({type:"success",message:"缓存清理成功！"}),setTimeout(function(){t.getKongUrlList()},1e3)):t.$message({type:"warning",message:e.data.message})})}).catch(function(){t.$message({type:"info",message:"已取消"})})}}},i={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("h3",[e._v("Welcome to kong admin")]),e._v(" "),n("el-card",{staticClass:"form-card"},[n("div",{staticClass:"clearfix",staticStyle:{"line-height":"40px"},attrs:{slot:"header"},slot:"header"},[n("span",[e._v("kong服务列表")]),e._v(" "),n("div",{staticStyle:{float:"right"}},[n("el-button",{attrs:{type:"text",size:"small"},on:{click:e.clearCache}},[e._v("清理全部缓存")])],1)]),e._v(" "),n("el-table",{attrs:{data:e.kongUrlList,"row-class-name":e.tableRowClassName}},[n("el-table-column",{attrs:{type:"index",width:"50"}}),e._v(" "),n("el-table-column",{attrs:{prop:"server",label:"kongUrl"}}),e._v(" "),n("el-table-column",{attrs:{label:"操作"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.server==e.kongAdminUrl?n("el-button",{attrs:{type:"info",size:"small"}},[e._v("已绑定")]):n("el-button",{attrs:{type:"primary",size:"small"},on:{click:function(n){e.changeKongUrl(t.row.server)}}},[e._v("绑定")]),e._v(" "),n("el-button",{attrs:{type:"danger",size:"small"},on:{click:function(n){e.clearSingleCache(t.row.server)}}},[e._v("清理缓存")])]}}])})],1)],1)],1)},staticRenderFns:[]},l=n("VU/8")(o,i,!1,null,null,null);t.default=l.exports}});