<@markup id="custom-favicon" target="favicons" action="replace" scope="global">

<!-- Icons -->
    <#if theme == msg(theme + ".theme.id") && msg(theme + ".favicon")?has_content>
        <link rel="shortcut icon" href="${url.context}/res/themes/${theme}/images/${msg(theme + ".favicon")}" type="image/vnd.microsoft.icon" />
        <link rel="icon" href="${url.context}/res/themes/${theme}/images/${msg(theme + ".favicon")}" type="image/vnd.microsoft.icon" />
    <#else>
        <link rel="shortcut icon" href="${url.context}/res/favicon.ico" type="image/vnd.microsoft.icon" />
        <link rel="icon" href="${url.context}/res/favicon.ico" type="image/vnd.microsoft.icon" />
    </#if>

</@>