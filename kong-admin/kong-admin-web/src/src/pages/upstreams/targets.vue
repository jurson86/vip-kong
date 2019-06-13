<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item :to="{name: 'upstreams'}">服务</el-breadcrumb-item>
			<el-breadcrumb-item>Targets of {{itemName}}</el-breadcrumb-item>
		</el-breadcrumb>
		<div class="plus">
			<el-select v-model="targets" style="width: 80%;" @change="getAllData">
				<el-option v-for="item in targetsArr" :key="item.value" :value="item.value" :label="item.label"></el-option>
			</el-select>
			<el-button type="primary" icon="el-icon-plus" circle @click="addItem" style="float: right;"></el-button>
		</div>
		<el-table :data="tableData" stripe>
			<el-table-column prop="id" label="ID"></el-table-column>
			<el-table-column prop="target" label="Target"></el-table-column>
			<el-table-column prop="weight" label="Weight"></el-table-column>
			<el-table-column prop="created_at" label="Created"></el-table-column>
			<el-table-column label="#" width="180" v-if="targets == 'active'">
				<template slot-scope="scope">
					<el-button type="danger" icon="el-icon-delete" circle @click="deleteItem(scope.row.id)"></el-button>
				</template>
			</el-table-column>
		</el-table>
		<!-- <el-pagination class="page" layout="total, prev, next" :current-page="page" :total="totalCount" :page-size="size" @current-change="changePage"></el-pagination> -->
	</div>
</template>

<script type="es6">
	import { mapMutations, mapGetters } from 'vuex';
	import mixinGetKongUrl from '@/utils/mixinGetKongUrl';

	export default{
		mixins: [mixinGetKongUrl],
		data(){
			return{
				page: 1,
				totalCount: 1,
				size: 10,
				nextUrl: '',
				targets: 'active',
				targetsArr: [
					{ value: 'all', label: 'ALL Targets' },
					{ value: 'active', label: 'active Targets' }
				],
				tableData: []
			}
		},

		computed: {
			itemName(){
				return this.$route.params.name;
			},

			...mapGetters([
				'curUserGroup'
			])
		},

		watch: {
			curUserGroup(val){
				if(val){
					this.getKongUrl();
					setTimeout(() => {
						this.getTableData();
					}, 1000);
				}
			}
		},

		created(){
			this.getTableData();
		},

		methods: {
			addItem(){
				this.$router.push({
					name: 'targetsAdd'
				});
			},

			deleteItem(id){
				this.$confirm('确定删除这一项吗？', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					this.$delete('/upstreams/' + this.itemName + '/targets/' + id).then(res => {
						if(res.status == 204){
							this.$message({
								type: 'success',
								message: '删除成功'
							})

							if(this.targets == 'active'){
								this.getTableData();
							}else{
								this.getAllData();
							}
						}
					})
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消删除'
					})
				})
			},

			getTableData(){
				this.$get('/upstreams/' + this.itemName + '/targets').then(res => {
					if(typeof(res) === 'object'){
						this.totalCount = res.total;

						this.formatTableData(res);
					}
				})
			},

			getAllData(val){
				if(this.targets == 'all'){
					this.$get('/upstreams/' + this.itemName + '/targets/all').then(res => {
						if(typeof(res) == 'object'){
							this.totalCount = res.total;

							this.formatTableData(res);
						}
					})
				}else{
					this.getTableData();
				}
			},

			formatTableData(res){
				if(res.total > this.size && res.next){
					this.nextUrl = res.offset;
				}

				this.tableData = res.data;
				for(let i = 0; i < res.data.length; i++){
					let item = res.data[i];
					this.tableData[i].created_at = this.formatDateTime(item.created_at);
				}
			},

			changePage(cur){
				if(cur == 1){
					this.getAllData();
				}else{
					if(this.targets == 'all'){
						this.$get('/upstreams/' + this.itemName + '/targets/all?size=' + this.size + '&offset=' + this.nextUrl).then(res => {
							this.formatTableData(res);
						})
					}else{
						this.$get('/upstreams/' + this.itemName + '/targets?size=' + this.size + '&offset=' + this.nextUrl).then(res => {
							this.formatTableData(res);
						})
					}
				}
			}
		}
	}
</script>