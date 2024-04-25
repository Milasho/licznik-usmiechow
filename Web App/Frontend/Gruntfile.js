module.exports = function (grunt) {
    grunt.initConfig({
		copy: {
			mainApp: {
				files: [
					{cwd: 'config',  src: '**/*.json', dest: 'release/config', expand: true},
					{cwd: 'lib',  src: 'bootstrap/**/fonts/*.*', dest: 'release/lib', expand: true},
					{cwd: 'images',  src: '**/*.*', dest: 'release/images', expand: true},
					{cwd: 'files',  src: '**/*.*', dest: 'release/files', expand: true},
					{cwd: 'demo',  src: '**/*.*', dest: 'release/demo', expand: true},
					{src: 'index.min.html', dest: 'release/index.html'},
				]
			}
		},
		uglify: {
			mainApp: {
				src: [
					'app/app.js',
					'app/config/**/*.js',
					'app/connectors/**/*.js',
					'app/pages/**/*.js',
					'app/components/**/*.js',
					'release/templates.js'
				],
				dest: 'release/app.min.js',
				flatten: true
			},
			vendor: {
				src: [
					'lib/jquery/**/*.js',
					'lib/perfect-scrollbar/**/*.js',
					'lib/angular/**/*.js',
					'lib/bootstrap/**/*.js'
				],
				dest: 'release/vendor.min.js',
				flatten: true
			}
		},
		cssmin: {
			options: {
				shorthandCompacting: false,
				roundingPrecision: -1
			},
			combine: {
				files: {
				  'release/styles/app.min.css': ['style/*.css'],
				  'release/styles/vendor.min.css': ['lib/**/*.css']
				}
			}
		},
		ngtemplates:  {
		  app:        { cwd: 'app', src: '**/*.html', dest: 'release/templates.js'},
		  options:    {
			  htmlmin:  { 
			      collapseBooleanAttributes:      true,
				  collapseWhitespace:             true,
				  removeAttributeQuotes:          true,
				  removeComments:                 true,
				  removeEmptyAttributes:          true,
				  removeRedundantAttributes:      true,
				  removeScriptTypeAttributes:     true,
				  removeStyleLinkTypeAttributes:  true
			  },
			  module: "app",
			  bootstrap:  function(module, script) {
				return '(function () {angular.module("' + module + '").run(["$templateCache", function($templateCache) { ' + script + ' }])}());';
			  },
			  url:    function(url) { return "app/" + url; }
		  }
		}
});

// load plugins
grunt.loadNpmTasks('grunt-contrib-watch');
grunt.loadNpmTasks('grunt-contrib-uglify');
grunt.loadNpmTasks('grunt-contrib-cssmin');
grunt.loadNpmTasks('grunt-contrib-copy');
grunt.loadNpmTasks('grunt-angular-templates');

// register at least this one task
grunt.registerTask('default', [ 'ngtemplates', 'uglify', 'cssmin', 'copy' ]);
}; 