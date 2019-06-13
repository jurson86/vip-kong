<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{name: 'home'}">首页</el-breadcrumb-item>
			<el-breadcrumb-item :to="{name: 'upstreams'}">服务</el-breadcrumb-item>
			<el-breadcrumb-item>{{itemName}}的健康状况</el-breadcrumb-item>
		</el-breadcrumb>
		<br>
		<el-table :data="tableData" stripe>
			<el-table-column prop="id" label="ID"></el-table-column>
			<el-table-column prop="target" label="Target"></el-table-column>
			<el-table-column prop="weight" label="Weight"></el-table-column>
			<el-table-column prop="created_at" label="Created"></el-table-column>
			<el-table-column label="#" width="240">
				<template slot-scope="scope">
					<template v-if="scope.row.health == 'HEALTHCHECKS_OFF'">
						<el-select disabled v-model="scope.row.health">
							<el-option value="healthy" label="HEALTHY"></el-option>
							<el-option value="unhealthy" label="UNHEALTHY"></el-option>
						</el-select>
					</template>
					<template v-else>
						<el-select v-model="scope.row.health" @change="changeStatus(scope.$index)">
							<el-option value="healthy" label="HEALTHY"></el-option>
							<el-option value="unhealthy" label="UNHEALTHY"></el-option>
						</el-select>
					</template>
				</template>
			</el-table-column>
		</el-table>
		<!-- <el-pagination class="page" layout="total, prev, next" :current-page="page" :total="totalCount" :page-size="size" @next-click="changePage" @prev-click="changePage"></el-pagination> -->
	</div>
</template>

<script type="es6">
	export default{
		data(){
			return{
				page: 1,
				totalCount: 1,
				size: 10,
				nextUrl: '',
				tableData: []
			}
		},

		computed: {
			itemName(){
				return this.$route.params.name;
			}
		},

		created(){
			this.getTableData();
		},

		methods: {
			getTableData(){
				this.$get('/upstreams/' + this.itemName + '/health').then(res => {
					this.totalCount = res.total;

					this.formatTableData(res);
				})
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
					this.getTableData();
				}else{
					this.$get('/upstreams/' + this.itemName + '/health?size=' + this.size + '&offset=' + this.nextUrl).then(res => {
						this.formatTableData(res);
					})
				}
			},

			changeStatus(index){
				this.$confirm('确定修改健康状况吗？', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					this.$post('/upstreams/' + this.itemName + '/targets/' + this.tableData[index].target + '/' + this.tableData[index].health).then(res => {
						this.$message({
							type: 'success',
							message: '修改成功'
						});
						this.getTableData();
					})
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消修改'
					});
					this.getTableData();
				})	
			}
		}
	}
</script>