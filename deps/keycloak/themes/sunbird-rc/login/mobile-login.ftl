<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo; section>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
        <link href="https://fonts.googleapis.com/css?family=Muli" rel="stylesheet"/>
        <link href="${url.resourcesPath}/img/favicon.png" rel="icon"/>
        <script>
            window.onload = function (e) {
                document.getElementById("mobile_number").addEventListener("change", function (evt) {
                    console.log(evt.target.value)
                    sessionStorage.setItem("mobile_number", evt.target.value)
                })
                if(window.location.protocol === "https:") {
                    let formField = document.getElementById("kc-form-login");
                    if (formField) {
                        formField.action = formField.action.replace("http:","https:");
                    }
                }
            }
        </script>
    <#elseif section = "form">
        <div class="ndear-login-card-wrapper">
            <h3>Login</h3>
            <br/>
            <b>${properties.userIdTitle!}</b>
            <div class="box-container">
                <#if realm.password>
                    <div>
                        <form id="kc-form-login" class="form" onsubmit="login.disabled = true; return true;"
                              action="${url.loginAction}" method="post">
                            <div class="input-wrapper">
                                <div class="input-field mobile">
                                    <input id="mobile_number" class="login-field" placeholder="XXXXXXXXXX"
                                           type="text"
                                           name="mobile_number"
                                           tabindex="1"/>
                                </div>

                            </div>
                            <input type="hidden" id="type-hidden-input" name="form_type" value="login"/>
                            <button id="submit-btn" class="submit" type="submit" tabindex="3">
                                <span>Continue</span>
                            </button>
                        </form>
                    </div>
                </#if>


            </div>
        </div>
    </#if>
</@layout.registrationLayout>
