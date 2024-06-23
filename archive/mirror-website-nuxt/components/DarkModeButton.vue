<template>
	<button @click="" class="modeSwitch">
		<Icon :name="iconNames[colorStates.system]" size="30px" />
	</button>
</template>

<script setup lang="ts">
enum colorStates {
	system,
	dark,
	light
}


const iconNames = ['mdi:theme-light-dark', 'mdi:weather-night', 'mdi:white-balance-sunny']

const modeSwitch = () => {


}

</script>

<style scoped>
.modeSwitch {
	height: 40px;
	width: 40px;
	border: none;
	border-radius: 10px;
	position: absolute;
	top: 21px;
	right: 21px;
}
</style>