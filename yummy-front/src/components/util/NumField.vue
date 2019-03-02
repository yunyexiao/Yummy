<template>
  <span>
    <v-btn
      icon
      v-show="value === 0"
      @click="plus"
    >
      <v-icon>add_circle</v-icon>
    </v-btn>
    <v-text-field
      v-show="value > 0"
      size="3"
      :label="label"
      v-model="innerValue"
      @input="$emit('input', innerValue ? parseInt(innerValue) : 0)"
    >
      <v-btn slot="prepend" @click="minus" icon>
        <v-icon>remove_circle</v-icon>
      </v-btn>
      <v-btn slot="append-outer" @click="plus" icon>
        <v-icon>add_circle</v-icon>
      </v-btn>
    </v-text-field>
  </span>
</template>

<script>
export default {
  name: 'NumField',
  props: {
    'label': String,
    'value': Number
  },
  data: function () {
    return {
      innerValue: this.value
    }
  },
  watch: {
    value: function (newValue, oldValue) {
      this.innerValue = newValue
    }
  },
  methods: {
    plus: function () {
      let value = parseInt(this.innerValue) + 1
      this.$emit('input', value)
    },
    minus: function () {
      let value = parseInt(this.innerValue) - 1
      if (value >= 0) {
        this.$emit('input', value)
      }
    }
  }
}
</script>

<style scoped>

</style>
