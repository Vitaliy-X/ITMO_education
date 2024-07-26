export const toPost = function () {
    this.$root.$emit("toPost", this.post.id);
}