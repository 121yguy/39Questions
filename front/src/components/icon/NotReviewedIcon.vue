<template>
  <svg t="1729925625110" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="12076" width="200" height="200"><path d="M513.536 958.464c-246.784 0-448-201.216-448-448s201.216-448 448-448 448 201.216 448 448-200.704 448-448 448z m0-836.608c-214.016 0-388.096 174.08-388.096 388.096s174.08 388.096 388.096 388.096 388.096-174.08 388.096-388.096c0-213.504-174.08-388.096-388.096-388.096z" fill="#d81e06" p-id="12077"></path><path d="M749.056 542.208h-471.04c-11.264 0-20.48-9.216-20.48-20.48v-23.04c0-11.264 9.216-20.48 20.48-20.48h471.04c11.264 0 20.48 9.216 20.48 20.48v23.04c0 11.264-9.216 20.48-20.48 20.48z" fill="#d81e06" p-id="12078"></path></svg>
</template>

<script setup>
const props = defineProps({
  width: {
    type: String,
    default: '200',
  },
  height: {
    type: String,
    default: '200',
  },
});
</script>

<style scoped>
.icon {
  display: block;
}
</style>
