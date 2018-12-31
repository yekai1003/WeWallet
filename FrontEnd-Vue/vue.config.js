module.exports = {
    baseUrl: './',
    outputDir: 'dist',
    assetsDir: 'public',
    productionSourceMap: false,
    css: {
        extract: false,
        sourceMap: false
    },


    devServer: {

        proxy: {
            '/api': {
                target: 'https://api.coindesk.com', //接口地址域名
                changeOrigin: true,
                secure: false,  //https需要加该配置
                pathRewrite: {
                    '^/api': ''  //设置后调用接口需要在前面加上/api 来代替target
                }
            }
        }
    },

}