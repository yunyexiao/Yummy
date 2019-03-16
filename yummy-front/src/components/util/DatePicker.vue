<template>
  <v-menu
    v-model="menu"
    :close-on-content-click="false"
    lazy
    transition="scale-transition"
    offset-y
    :full-width="fullWidth"
    min-width="290px"
    :disabled="disabled"
  >
    <v-text-field
      :label="label"
      :disabled="disabled"
      :rules="rules"
      slot="activator"
      v-model="innerValue"
      @input="$emit('input', innerValue)"
      readonly
    ></v-text-field>
    <v-date-picker
      v-model="innerValue"
      :type="type"
      @input="datePicked"
    ></v-date-picker>
  </v-menu>
</template>

<script>
export default {
  name: 'DatePicker',
  props: {
    label: String,
    value: String,
    disabled: Boolean,
    rules: Array,
    fullWidth: Boolean,
    type: String
  },
  data: function () {
    return {
      menu: false,
      innerValue: this.value
    }
  },
  watch: {
    value: function (newValue, oldValue) {
      this.innerValue = newValue
    }
  },
  methods: {
    datePicked: function () {
      this.$emit('input', this.innerValue)
      this.menu = false
    }
  }
}
</script>

<style scoped>

</style>
