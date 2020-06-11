if (user.isGuest) {
    model.theme = context.getSiteConfiguration().getProperty("theme");
}