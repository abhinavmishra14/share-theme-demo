<@markup id="custom-html" target="html" action="replace" scope="global">
      <@uniqueIdDiv>
         <div class="quickshare-header">

            <div class="quickshare-header-brand-colors">
               <div class="brand-bgcolor-6"></div>
               <div class="brand-bgcolor-5"></div>
               <div class="brand-bgcolor-4"></div>
               <div class="brand-bgcolor-3"></div>
               <div class="brand-bgcolor-2"></div>
               <div class="brand-bgcolor-1"></div>
               <div class="clear"></div>
            </div>

            <div class="quickshare-header-left">
                <#if theme == msg(theme + ".theme.id")>
			    	<img src="${url.context}/res/themes/${theme}/images/app-logo-48.png">
			    <#else>
			       <img width="180" src="${url.context}/res/components/images/alfresco-logo.svg">
			    </#if>
            </div>

            <div class="quickshare-header-right">
               <@markup id="linkButtons">
                  <#list linkButtons as linkButton>
                     <a href="${linkButton.href}" class="brand-button ${linkButton.cssClass!""}" tabindex="0">${linkButton.label?html}</a>
                  </#list>
               </@markup>
            </div>
            
            <#if page.url.args.error! == "true">
               <script>
                  Alfresco.util.PopupManager.displayMessage({
                     text: "${authfailureMessage?js_string}"
                  });
               </script>
            </#if>

            <div class="clear"></div>

         </div>
    </@>
  </@>