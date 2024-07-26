<template>
    <footer>
        <a href="/">Codeforces</a> &copy; 2022 by Mike Mirzayanov
        <div class="userStat">
          users: {{ usersNumber }}
        </div>
        <div class="postStat">
          posts: {{ postsNumber }}
        </div>
    </footer>
</template>

<script>
export default {
    name: "Footer",
    props: ["postsNumber", "usersNumber"]
}
</script>

<style scoped>

</style>