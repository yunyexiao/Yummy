<template>
  <v-layout>
    <v-flex xs1 sm2></v-flex>
    <v-flex>
      <v-layout justify-center>
        <h2>菜单管理</h2>
        <v-btn
          color="pink"
          @click="addMeal"
          absolute
          right
          dark
          fab
        >
          <v-icon>add</v-icon>
        </v-btn>
      </v-layout>
      <v-layout>
        <v-flex>
          <v-tabs show-arrows>
            <v-tabs-slider></v-tabs-slider>
            <v-tab
              v-for="type in Object.keys(menu)"
              :key="type"
              :href="'#' + type"
            >{{type}}</v-tab>
            <v-tab-item
              v-for="(meals, type) in menu"
              :key="type"
              :value="type"
            >
              <v-list two-line>
                <template v-for="(meal, index) in meals">
                  <v-list-tile :key="meal.id" @click="showMealInfo(meal)">
                    <v-list-tile-content>
                      <v-list-tile-title>
                        编号：{{meal.id}}&emsp;&emsp;菜品名：{{meal.name}}&emsp;&emsp;菜品单价：{{meal.price}}
                      </v-list-tile-title>
                      <v-list-tile-sub-title>
                        日供应：{{meal.num}}&emsp;&emsp;开始日期：{{meal.startDate}}&emsp;&emsp;结束日期：{{meal.endDate}}
                      </v-list-tile-sub-title>
                    </v-list-tile-content>
                    <v-list-tile-action>
                      <!-- TODO maybe need to modify a meal info -->
                    </v-list-tile-action>
                  </v-list-tile>
                  <v-divider :key="'d' + index" v-if="index < meals.length - 1"></v-divider>
                </template>
              </v-list>
            </v-tab-item>
          </v-tabs>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-flex xs1 sm2></v-flex>
    <v-dialog v-model="infoDialog.show" width="500">
      <v-card>
        <v-card-text>
          <v-form ref="mealForm" lazy-validation>
            <v-text-field
              v-show="!infoDialog.additionMode"
              label="菜品编号"
              v-model="meal.id"
              :disabled="true"
            ></v-text-field>
            <v-text-field
              label="菜品名称"
              v-model="meal.name"
              :disabled="!infoDialog.editable"
              :counter="mealLengthLimits.name"
              :rules="mealRules.name"
            ></v-text-field>
            <v-text-field
              label="菜品类型"
              v-model="meal.type"
              :disabled="!infoDialog.editable"
              :counter="mealLengthLimits.type"
              :rules="mealRules.type"
            ></v-text-field>
            <v-textarea
              label="菜品介绍"
              v-model="meal.description"
              :disabled="!infoDialog.editable"
              :counter="mealLengthLimits.description"
              :rules="mealRules.description"
            ></v-textarea>
            <v-text-field
              label="菜品单价"
              v-model="meal.price"
              type="number"
              :disabled="!infoDialog.editable"
              :rules="mealRules.price"
            ></v-text-field>
            <v-text-field
              label="日供应量"
              v-model="meal.num"
              type="number"
              :disabled="!infoDialog.editable"
              :rules="mealRules.num"
            ></v-text-field>
            <DatePicker
              label="开始供应日期"
              v-model="meal.startDate"
              :full-width="true"
              :disabled="!infoDialog.editable"
              :rules="mealRules.startDate"
            ></DatePicker>
            <DatePicker
              label="结束供应日期"
              v-model="meal.endDate"
              :full-width="true"
              :disabled="!infoDialog.editable"
              :rules="mealRules.endDate"
            ></DatePicker>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn
            v-show="infoDialog.additionMode"
            color="success"
            @click="publishMeal"
          >发布</v-btn>
          <v-btn
            color="info"
            @click="infoDialog.show = false"
          >取消</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar top v-model="snackbar.show">
      {{snackbar.msg}}
      <v-btn color="pink" flat @click="snackbar.show = false">关闭</v-btn>
    </v-snackbar>
  </v-layout>
</template>

<script>
import DatePicker from '@/components/util/DatePicker'
export default {
  name: 'MealManage',
  components: {DatePicker},
  beforeMount: function () {
    this.loadMenu()
  },
  data: function () {
    return {
      menu: {},
      infoDialog: {
        show: false,
        editable: false,
        additionMode: false
      },
      meal: {
        id: 0,
        name: '',
        type: '',
        description: '',
        price: 0,
        num: 0,
        startDate: '',
        endDate: ''
      },
      snackbar: {
        show: false,
        msg: ''
      },
      mealLengthLimits: {
        name: 40,
        type: 20,
        description: 100
      },
      mealRules: {
        name: [
          name => !!name || '请填写菜品名称',
          name => (name && name.length <= this.mealLengthLimits.name) ||
            '菜品名称长度不得大于' + this.mealLengthLimits.name + '个字符'
        ],
        type: [
          type => !!type || '请填写菜品类型',
          type => (type && type.length <= this.mealLengthLimits.type) ||
            '菜品类型长度不得大于' + this.mealLengthLimits.type + '个字符'
        ],
        description: [
          description => !!description || '请填写菜品介绍',
          description => (description && description.length <= this.mealLengthLimits.description) ||
            '菜品介绍的长度不得超过' + this.mealLengthLimits.description + '个字符'
        ],
        price: [
          price => !!price || '请填写菜品单价',
          price => (price > 0) || '菜品单价必须大于0'
        ],
        num: [
          num => !!num || '请填写日供应量',
          num => (num > 0) || '日供应量必须大于0'
        ],
        startDate: [
          startDate => !!startDate || '请填写开始供应日期',
          startDate => (!this.meal.endDate || new Date(startDate) <= new Date(this.meal.endDate)) ||
            '开始日期应早于结束日期'
        ],
        endDate: [
          endDate => !!endDate || '请填写结束供应日期',
          endDate => (!this.meal.startDate || new Date(this.meal.startDate) <= new Date(endDate)) ||
            '结束日期应晚于开始日期'
        ]
      }
    }
  },
  methods: {
    loadMenu: function () {
      this.$ajax({
        url: '/restaurant/meal/get',
        method: 'get'
      }).then(res => {
        if (res.data['AccessDenied']) {
          this.$router.push('/')
        } else {
          for (let m of res.data) {
            let type = m.type
            if (type in this.menu) {
              this.menu[type].push(m)
            } else {
              this.$set(this.menu, type, [m])
            }
          }
        }
      })
    },
    reloadMenu: function () {
      Object.keys(this.menu).forEach(type => delete this.menu[type])
      this.loadMenu()
    },
    showMealInfo: function (meal) {
      this.meal.id = meal.id
      this.meal.name = meal.name
      this.meal.type = meal.type
      this.meal.description = meal.description
      this.meal.price = meal.price
      this.meal.num = meal.num
      this.meal.startDate = meal.startDate
      this.meal.endDate = meal.endDate
      this.infoDialog.additionMode = false
      this.infoDialog.editable = false
      this.infoDialog.show = true
    },
    addMeal: function () {
      this.meal.name = ''
      this.meal.type = ''
      this.meal.description = ''
      this.meal.price = 0
      this.meal.num = 0
      this.meal.startDate = ''
      this.meal.endDate = ''
      this.$refs.mealForm.resetValidation()
      this.infoDialog.additionMode = true
      this.infoDialog.editable = true
      this.infoDialog.show = true
    },
    publishMeal: function () {
      if (this.$refs.mealForm.validate()) {
        this.$ajax({
          url: '/restaurant/meal/publish',
          method: 'post',
          data: this.meal
        }).then(res => {
          if (res.data['AccessDenied']) {
            this.$router.push('/')
          } else {
            switch (res.data['result']) {
              case 0:
                this.snackbar.msg = '菜品发布成功。'
                this.snackbar.show = true
                this.reloadMenu()
                break
              case 1:
                this.snackbar.msg = '您的餐馆尚未上线，无法发布菜品。'
                this.snackbar.show = true
                break
              default:
                this.snackbar.msg = '服务器未知错误。'
                this.snackbar.show = true
                break
            }
          }
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
