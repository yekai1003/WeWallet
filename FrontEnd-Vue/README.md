# WalletVue

## Use introduction
点击底部bar第二个tab可切换到登录注册页
api接口域名在vue.config.js中的devServer-proxy-target处配置，具体接口url在src/views/Login.vue中调用时配置，methods-loginSubmit/registSubmit axios url处配置接口 /api/xxxx，以/api代表域名

## Project setup
```
npm install
//已把所有nodemodule上传,可不用执行
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Run your tests
```
npm run test
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
