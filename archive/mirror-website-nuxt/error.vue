<template>
	<PageHeader />
	<div class="body">
		<main>
			<p class="error">404</p>

			<hr class="divider">

			<p class="summary">Page <code class="route">{{ $route.path }}</code> not found</p>

			<NuxtLink to="/" class="homeLink">Go to home</NuxtLink>
		</main>
	</div>
	<PageFooter />
</template>

<script setup lang="ts">

</script>

<style scoped>
main {
	text-align: center;
}

.error {
	margin: 8% auto 0;
	font-size: 140px;
}

.summary {
	font-size: 30px;
}

.route {
	background-color: rgb(40, 40, 40);
	padding: 5px;
	border-radius: 15px;
}

.divider {
	width: 50px;
}

.homeLink {
	text-decoration: none;
	font-size: 20px;
}
</style>