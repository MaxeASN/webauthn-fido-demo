const t = {};

class e extends HTMLElement {
    constructor() {
        super(), this.root = this.attachShadow({mode: "open"}), this._onFormSubmitListener = this._onFormSubmit.bind(this), this.assertionStartUrl = "/api/assertion/start", this.assertionFinishUrl = "/api/assertion/finish", this.fetchOptions = {
            method: "POST",
            credentials: "include",
            headers: {"Content-Type": "application/json"}
        }
    }

    static get observedAttributes() {
        return ["no-username", "label", "input-type", "input-name", "button-text"]
    }

    connectedCallback() {
        this.update(), this.root.querySelector("form").addEventListener("submit", this._onFormSubmitListener)
    }

    disconnectedCallback() {
        this.root.querySelector("form").removeEventListener("submit", this._onFormSubmitListener)
    }

    attributeChangedCallback(t, e, n) {
        if (!this.root.innerHTML) return;
        if (n === e) return;
        const i = this.root.querySelector("label"), s = this.root.querySelector("input"),
            r = this.root.querySelector("button");
        switch (t) {
            case"no-username":
                this._shouldUseUsername();
                break;
            case"label":
                i.textContent = n || this.label;
                break;
            case"button-text":
                r.textContent = n || this.buttonText;
                break;
            case"input-type":
                s.type = n || this.inputType;
                break;
            case"input-name":
                s.name = n || this.inputName
        }
    }

    update() {
        this.root.innerHTML || (this.root.innerHTML =
            `
            <style>
        form#login-form {
            position: relative;
            top: 30px;
        }
        input#el-id-6606-8 {
            height: 60;
            padding: 1 16;
            background-color: rgb(249, 249, 249);
            width: 90%;
            border-radius: 12px;
            font-size: 18;
        } 
        button#submit-btn {
            position: relative;
            top: 20;
            background-color: #9a7cff;
            height: 50;
            width: 90%;
            border-radius: 12px;
            border: none;
            font-family: Arial, sans-serif;
            font-size: 16px;
            font-weight: bold;
            color: #fff;
            cursor: pointer;
        }
        
        </style>
        <form part="form" class="el-form el-form--default el-form--label-right" id="login-form">
\t\t\t\t\t\t\t<div class="el-form-item asterisk-left up-form-item" type="email">
\t\t\t\t\t\t\t\t<!--v-if-->
\t\t\t\t\t\t\t\t<div class="el-form-item__content">
\t\t\t\t\t\t\t\t\t<div class="el-input el-input--prefix el-input--suffix up-input">
\t\t\t\t\t\t\t\t\t\t<!-- input -->
\t\t\t\t\t\t\t\t\t\t<!-- prepend slot -->
\t\t\t\t\t\t\t\t\t\t<!--v-if-->
\t\t\t\t\t\t\t\t\t\t<div class="el-input__wrapper">
\t\t\t\t\t\t\t\t\t\t\t<!-- prefix slot -->
\t\t\t\t\t\t\t\t\t\t\t<span class="el-input__prefix">
\t\t\t\t\t\t\t\t\t\t\t\t<span class="el-input__prefix-inner">
\t\t\t\t\t\t\t\t\t\t\t\t\t<div class="left"></div>
\t\t\t\t\t\t\t\t\t\t\t\t\t<div class="center"></div>
\t\t\t\t\t\t\t\t\t\t\t\t\t<div class="right"></div>
\t\t\t\t\t\t\t\t\t\t\t\t\t<div class="bottom"></div>
\t\t\t\t\t\t\t\t\t\t\t\t\t<!--v-if-->
\t\t\t\t\t\t\t\t\t\t\t\t</span>
\t\t\t\t\t\t\t\t\t\t\t</span>
\t\t\t\t\t\t\t\t\t\t\t<input class="el-input__inner" autofocus="" type="text" autocomplete="off" tabindex="0" placeholder="请输入用户名" id="el-id-6606-8" name = "username"><!-- suffix slot -->
\t\t\t\t\t\t\t\t\t\t\t<!--v-if-->
\t\t\t\t\t\t\t\t\t\t</div><!-- append slot -->
\t\t\t\t\t\t\t\t\t\t<!--v-if-->
\t\t\t\t\t\t\t\t\t</div>
\t\t\t\t\t\t\t\t\t<!--v-if-->
\t\t\t\t\t\t\t\t</div>
\t\t\t\t\t\t\t</div>
\t\t\t\t\t\t\t<button class="el-button el-button--primary submit up-button submit" aria-disabled="false" type="submit" id="submit-btn">
\t\t\t\t\t\t\t\t<!--v-if-->
\t\t\t\t\t\t\t\t<span class="">登录</span>
\t\t\t\t\t\t\t</button>
\t\t\t\t\t\t</form>
            `
        ), this._shouldUseUsername()
    }

    get noUsername() {
        return this.hasAttribute("no-username")
    }

    set noUsername(t) {
        t ? this.setAttribute("no-username", "") : this.removeAttribute("no-username")
    }

    get label() {
        return this.getAttribute("label") || "Username"
    }

    set label(t) {
        this.setAttribute("label", t)
    }

    get buttonText() {
        return this.getAttribute("button-text") || "Login"
    }

    set buttonText(t) {
        this.setAttribute("button-text", t)
    }

    get inputType() {
        return this.getAttribute("input-type") || "text"
    }

    set inputType(t) {
        this.setAttribute("input-type", t)
    }

    get inputName() {
        return this.getAttribute("input-name") || "username"
    }

    set inputName(t) {
        this.setAttribute("input-name", t)
    }

    _shouldUseUsername() {
        const t = this.root.querySelector("input");
        this.noUsername ? (t.required = !1, t.hidden = !0, t.value = "") : (t.required = !0, t.hidden = !1)
    }

    async _getPublicKeyCredentialRequestOptionsDecoder() {
        if ("function" == typeof this.publicKeyCredentialRequestOptionsDecoder) return this.publicKeyCredentialRequestOptionsDecoder;
        if ("function" == typeof t.publicKeyCredentialRequestOptionsDecoder) return t.publicKeyCredentialRequestOptionsDecoder;
        const {decodePublicKeyCredentialRequestOptions: e} = await import("./utils/parse.js");
        return t.publicKeyCredentialRequestOptionsDecoder = e, t.publicKeyCredentialRequestOptionsDecoder
    }

    async _getLoginCredentialEncoder() {
        if ("function" == typeof this.loginCredentialEncoder) return this.loginCredentialEncoder;
        if ("function" == typeof t.loginCredentialEncoder) return t.loginCredentialEncoder;
        const {encodeLoginCredential: e} = await import("./utils/parse.js");
        return t.loginCredentialEncoder = e, t.loginCredentialEncoder
    }

    async _onFormSubmit(t) {
        try {
            if (t.preventDefault(), !window.PublicKeyCredential) throw new Error("Web Authentication is not supported on this platform");
            this.dispatchEvent(new CustomEvent("login-started"));
            const e = new FormData(t.target).get(this.inputName), n = await fetch(this.assertionStartUrl, {
                ...this.fetchOptions,
                body: JSON.stringify({username: e})
            }), {assertionId: i, publicKeyCredentialRequestOptions: s} = await n.json();
            if (!n.ok) throw new Error("Could not successfuly start login");
            const r = await this._getPublicKeyCredentialRequestOptionsDecoder(),
                o = await navigator.credentials.get({publicKey: r(s)});
            this.dispatchEvent(new CustomEvent("login-retrieved"));
            const a = await this._getLoginCredentialEncoder(), u = await fetch(this.assertionFinishUrl, {
                ...this.fetchOptions,
                body: JSON.stringify({assertionId: i, credential: a(o)})
            });
            if (!u.ok) throw new Error("Could not successfuly complete login");
            const l = await u.json();
            this.dispatchEvent(new CustomEvent("login-finished", {detail: l}))
        } catch (t) {
            this.dispatchEvent(new CustomEvent("login-error", {detail: {message: t.message}}))
        }
    }
}

window.customElements.define("webauthn-login", e);
export {e as WebAuthnLogin};
