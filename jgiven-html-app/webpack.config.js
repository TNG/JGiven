var webpack = require('webpack');
var autoprefixer = require('autoprefixer');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var CopyWebpackPlugin = require('copy-webpack-plugin');

var ENV = process.env.npm_lifecycle_event;
var isTest = ENV === 'test' || ENV === 'test-watch';
var isRelease = ENV === 'build';

module.exports = function webPackConfig() {
    var config = {}

    config.entry =  {
        styles: './src/css/styles.scss',
        app: './src/js/app.js'
    }

    config.output = {
        path: __dirname + '/build/dist',
        filename: '[name].bundle.js',
        chunkFilename: '[name].bundle.js'
    }

    config.module = {
        preLoaders: [],
        loaders: [
            {
                test: /\.css$/,
                loader: ExtractTextPlugin.extract('style-loader', 'css-loader?sourceMap!postcss-loader')
            },
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loader: 'babel',
                query: {
                      presets: ['es2015']
                }
            },
            {
                test: /\.scss$/,
                loader: ExtractTextPlugin.extract('style', 'css!sass')
            },
            {
                test: /\.html$/,
                loader: 'raw'
            },
            { test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: 'file-loader?mimetype=image/svg+xml'},
            { test: /\.woff(\?v=\d+\.\d+\.\d+)?$/, loader: 'file-loader?mimetype=application/font-woff'},
            { test: /\.woff2(\?v=\d+\.\d+\.\d+)?$/, loader: 'file-loader?mimetype=application/font-woff'},
            { test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: 'file-loader?mimetype=application/octet-stream'},
            { test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: 'file-loader'}
        ]
    }

    config.postcss = [
        autoprefixer({
            browsers: ['last 2 version']
        })
    ];

    if (isTest) {
        config.devtool = 'inline-source-map';
    } else if (isRelease) {
        //config.devtool = 'source-map';
    } else {
        config.devtool = 'eval-source-map';
    }

    config.resolve = {
        moduleDirectories: ['node_modules']
    }

    config.plugins = []

    if (!isTest) {
        // https://github.com/ampedandwired/html-webpack-plugin
        config.plugins.push(
            new HtmlWebpackPlugin({
                template: './src/public/index.html',
                inject: false
            }),

            new ExtractTextPlugin('[name].css', {disable: !isRelease}),

            // moment.js is used by Chart.js, but actually not needed by JGiven
            // ignoring all locales except for english saves 500KB
            new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /en/)
        )
    }

    if (isRelease) {
        config.plugins.push(
            // Reference: http://webpack.github.io/docs/list-of-plugins.html#noerrorsplugin
            // Only emit files when there are no errors
//            new webpack.NoErrorsPlugin(),

            // Reference: http://webpack.github.io/docs/list-of-plugins.html#dedupeplugin
            // Dedupe modules in the output
  //          new webpack.optimize.DedupePlugin(),

           // new webpack.optimize.UglifyJsPlugin(),

            new CopyWebpackPlugin([{
                from: __dirname + '/src/public'
            }])
        )
    }

    config.devServer = {
        contentBase: './src/public',
        stats: 'minimal'
    };


    return config;
}();
