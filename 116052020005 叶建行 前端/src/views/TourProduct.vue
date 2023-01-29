<template>
	<div class="productTour">
		<div class="value">
			/ {{ value }} /
		</div>
		<div class="filter">
			<el-form :inline="true" class="demo-form-inline">
				<el-form-item label="市区">
					<el-select v-model="city_value" placeholder="请选择市区">
						<el-option v-for="(item, index) in cities" :key="item" :label="item" :value="item"
							@click.native="(city_value_index = index)">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="预算">
					<el-select v-model="price_value" placeholder="请选择预算" clearable>
						<el-option label="100-500" value="100-500" />
						<el-option label="500-1000" value="500-1000" />
						<el-option label="1000-2000" value="1000-2000" />
						<el-option label="2000-5000" value="2000-5000" />
						<el-option label="5000-10000" value="5000-10000" />
					</el-select>
				</el-form-item>
				<el-form-item label="名称">
					<el-input v-model="name_value" clearable placeholder="请输入计划名称"></el-input>
				</el-form-item>

				<el-form-item>
					<el-button type="primary" @click="query()">查询计划</el-button>
				</el-form-item>

			</el-form>
		</div>
		<div class="scens">
			<div class="top">
				<div class="name">/ 推荐计划</div>
				<div :class="item.class" @click="change(index)" v-for="(item, index) in sortList" :key="index">
					{{ item.lable }}
					<div class="box-icon">
						<div class="up" :class="item.status === 1 ? 'opacity-5' : ''"></div>
						<div class="down" :class="item.status === 1 ? 'opacity-1' : ''"></div>
					</div>
				</div>
			</div>
			<div class="scheduleList">
				<div class="scheduleList_item" v-for="item, index in scheduleList" @click="LinkToComments(item)">
					<div class="item_name">
						<span style="background: orange;padding: 5px 12px;color: #fff">{{ index + 1 }}</span> {{
								item.name
						}}
					</div>
					<div style="overflow: hidden;">
						<img class="item_img" :src="item.img" />
					</div>
					<div class="item_message">
						<div>
							<span>热度：{{ item.number }}</span>
							<span>价格：{{ item.price }}</span>
						</div>
						<div>
							<span>城市：{{ item.city }}</span>
							<span>评论数：{{ item.commentCount }}</span>
						</div>
					</div>
				</div>
			</div>
		</div>
		<el-pagination class="page" @current-change="handleCurrentChange" v-model:current-page="pageNum"
			:page-size="pageSize" layout="prev, pager, next" :total="total">
		</el-pagination>
	</div>
</template>

<script>

export default ({
	name: 'province',
	data() {
		return {
			city_value: '',
			city_value_index: 0,
			price_value: '',
			name_value: '',
			cities: [],
			value: '',
			pageSize: 6,
			total: null,
			pageNum: 1,
			scheduleList: [
			],
			sortList: [
				{ lable: "评论数", value: "commentCount", status: 1, class: "right" },
				{ lable: "价格", value: "price", status: 0, class: "right" },
				{ lable: "热度", value: "number", status: 0, class: "left" },
			],
			sort: null,
			sortby: null,
			centerDialogVisible: false,
		}
	},
	beforeRouteEnter(to, from, next) {
		next(vm => {
			if (from.name == "comments") {
				Object.assign(vm.$data, JSON.parse(sessionStorage.getItem('data')))
			} else {
				vm.city_value_index = 0
			}
		})
	},
	beforeRouteLeave(to, from, next) {
		if (to.name == "comments") {
			sessionStorage.setItem('data', JSON.stringify(this.$data))
		}
		next()
	},
	methods: {
		change(index) {
			this.sortList[index].status === 0
				? (this.sortList[index].status = 1)
				: (this.sortList[index].status = 0);

			// this.sortList[!index].class = "right";
			for (let i = 0; i < this.sortList.length; i++) {
				if (i == index)
					this.sortList[i].class = "left";
				else
					this.sortList[i].class = "right";
			}
			this.sort = this.sortList[index].value;
			this.sortby = this.sortList[index].status
			this.query();
		},
		handleCurrentChange(val) {
			this.pageNum = val;
			this.query();
		},
		query() {
			const _this = this;
			this.$axios.get("/attraction/query?pageNum=" + this.pageNum + '&pageSize=' + this.pageSize + '&nameValue='
				+ this.name_value + '&cityValue=' + this.city_value + '&priceValue=' + this.price_value
				+ "&sort=" + this.sort + "&sortBy=" + this.sortby
			).then(response => {
				var data = response.data.data;
				console.log(data);
				this.scheduleList = data.list;
				this.total = data.total;
			})
		},

		getCitiesByProvince() {
			this.$axios.get("/province/getCities?name=" + this.value
			).then(response => {
				this.cities = response.data.data
				this.city_value = this.cities[this.city_value_index]
				this.query()
			})
		},
		LinkToComments(item) {
			if (item.commentCount != 0) {
				this.$router.push({
					path: '/comments',
					query: { productId: item.productId, name: item.name, city: item.city },
				})
			} else {
				this.$message({
					type: 'warning',
					message: "暂无评论"
				});
			}
		},
	},
	created() {
		this.value = this.$route.query.value
		// this.cities = this.$route.query.cities
		this.getCitiesByProvince()
		// this.query();
	},
})
</script>

<style lang="scss" scoped>
.page {
	margin-left: 300px;
}

.filter {
	margin-left: 100px;
}

.productTour {
	width: 1000px;
}

img {
	width: 100%;
	height: 100%;
}

.demo-form-inline {
	margin-top: 10px;
}

.top {
	height: 50px;
	margin: 0 auto;
}

.name {
	padding: 10px;
	font-size: 1.2em;
	text-shadow: 5px 5px 5px orange;
	width: 200px;
	float: left;
}

.value {
	position: relative;
	text-align: center;
	font-weight: bolder;
	font-family: kaiti;
	font-size: 35px;
	text-shadow: 5px 5px 5px orange;
}

.scheduleList {
	display: flex;
	justify-content: space-around;
	flex-direction: row;
	flex-wrap: wrap;
}

.scheduleList_item {
	width: 32%;
	cursor: pointer;
	padding: 5px 20px;
	border: 1px solid orange;
	margin-bottom: 15px;
}

.scheduleList_item:hover .item_name {
	color: orange
}

.scheduleList_item:hover .item_img {
	transform: scale(1.2, 1.2);
}

.item_name {
	font-size: 1.1em;
	margin: 10px 0;
}

.loading_more {
	text-align: center;
	cursor: pointer;
}

.loading_more:hover {
	color: orange;
}

.right {
	padding: 0 20px;
	height: 32px;
	width: 100px;
	color: #505363;
	font-weight: 400;
	font-size: 14px;
	margin-top: 10px;
	margin-left: 10px;
	border: 1px solid rgb(233, 231, 231);
	display: flex;
	justify-content: center;
	line-height: 32px;
	cursor: pointer;
	float: right;
}

.left {
	color: #217aff;
	border: 1px solid #217aff;
	padding: 0 20px;
	height: 32px;
	width: 100px;
	font-weight: 400;
	font-size: 14px;
	margin-top: 10px;
	margin-left: 10px;
	display: flex;
	justify-content: center;
	line-height: 32px;
	cursor: pointer;
	float: right;
}

.right:hover {
	color: #217aff;
	border: 1px solid #217aff;

	.up {
		border-bottom: 6px solid #217aff;
	}

	.down {
		border-top: 6px solid #217aff;
	}
}

.opacity-5 {
	opacity: 0.5;
}

.opacity-1 {
	opacity: 1 !important;
}

.box-icon {
	height: 30px;
	margin-top: 7px;

	.up {
		width: 0px;
		/*设置宽高为0，所以div的内容为空，从才能形成三角形尖角*/
		height: 0px;
		border-bottom: 6px solid #a3a5b3;
		border-left: 4px solid transparent;
		/*transparent 表示透明*/
		border-right: 4px solid transparent;
		margin-bottom: 4px;
	}

	.down {
		width: 0px;
		height: 0px;
		opacity: 0.5;
		border-top: 6px solid #a3a5b3;
		border-left: 4px solid transparent;
		border-right: 4px solid transparent;
	}
}

.box-icon div {
	height: 10px;
	margin-left: 4px;
}

.comment {
	width: 750px;
	margin: 0 auto;
	font-family: PingFang SC, HarmonyOS_Regular, Helvetica Neue, Microsoft YaHei,
		sans-serif;
}

.author-title:not(:last-child) {
	border-bottom: 1px solid rgba(178, 186, 194, 0.3);
}

.author-title {
	padding: 10px;
}

.author-title .header-img {
	display: inline-block;
	vertical-align: top;
}

.author-title .author-info {
	display: inline-block;
	margin-left: 5px;
	width: 60%;
	height: 40px;
	line-height: 20px;
}

.author-title .author-info>span {
	display: block;
	cursor: pointer;
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}

.author-title .author-info .author-name {
	color: #303133;
	font-size: 18px;
	font-weight: 500;
}

.reply-box .talk-box {
	color: #606266;
}

.reply-box .talk-box span {
	color: #6298ce;
}

.author-title .author-info .author-time {
	font-size: 14px;
}

.author-time {
	color: #606266;
}


@media screen and (max-width: 1200px) {
	.author-title .icon-btn {
		width: 20%;
		padding: 7px;
	}
}

.author-title .talk-box {
	margin: 0 50px;
}

.author-title .talk-box>p {
	margin: 0;
}

.item_message {
	div {
		display: flex;
		font-size: 16px;
		color: orange;

		span {
			color: black;
			margin-left: 20px;
		}
	}
}
</style>